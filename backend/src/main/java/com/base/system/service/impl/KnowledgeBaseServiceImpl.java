package com.base.system.service.impl;

import com.base.common.service.CosService;
import com.base.common.util.SecurityUtils;
import com.base.system.dto.knowledge.*;
import com.base.system.entity.*;
import com.base.system.enums.FileAreaTypeEnum;
import com.base.system.enums.FileLinkTypeEnum;
import com.base.system.mapper.*;
import com.base.system.service.KnowledgeBaseService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 知识库服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeBaseServiceImpl implements KnowledgeBaseService {

    private final KbKnowledgeBaseMapper knowledgeBaseMapper;
    private final KbDirectoryMapper directoryMapper;
    private final KbDocumentMapper documentMapper;
    private final KbTagMapper tagMapper;
    private final KbDocumentTagMapper documentTagMapper;
    private final FileLinkObjMapper fileLinkObjMapper;
    private final KbCommentMapper commentMapper;
    private final SysFileMapper sysFileMapper;
    private final UserMapper userMapper;
    private final CosService cosService;

    // ==================== 知识库 ====================

    @Override
    public List<KnowledgeBaseResponse> listKnowledgeBases() {
        LambdaQueryWrapper<KbKnowledgeBase> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(KbKnowledgeBase::getUpdateTime);
        List<KbKnowledgeBase> list = knowledgeBaseMapper.selectList(wrapper);
        return list.stream().map(this::toKnowledgeBaseResponse).collect(Collectors.toList());
    }

    @Override
    public KnowledgeBaseResponse getKnowledgeBaseById(Long id) {
        KbKnowledgeBase entity = knowledgeBaseMapper.selectById(id);
        if (entity == null) {
            throw new RuntimeException("知识库不存在");
        }
        return toKnowledgeBaseResponse(entity);
    }

    @Override
    public void createKnowledgeBase(KnowledgeBaseSaveRequest request) {
        KbKnowledgeBase entity = new KbKnowledgeBase();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        knowledgeBaseMapper.insert(entity);
    }

    @Override
    public void updateKnowledgeBase(KnowledgeBaseSaveRequest request) {
        KbKnowledgeBase entity = knowledgeBaseMapper.selectById(request.getId());
        if (entity == null) {
            throw new RuntimeException("知识库不存在");
        }
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        knowledgeBaseMapper.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteKnowledgeBase(Long id) {
        knowledgeBaseMapper.deleteById(id);
        // 级联删除关联数据
        LambdaQueryWrapper<KbDirectory> dirWrapper = new LambdaQueryWrapper<>();
        dirWrapper.eq(KbDirectory::getKnowledgeBaseId, id);
        directoryMapper.delete(dirWrapper);

        // 先清理该知识库下所有文档的标签关联（关联表无逻辑删除，需物理清理），再删文档
        LambdaQueryWrapper<KbDocument> docWrapper = new LambdaQueryWrapper<>();
        docWrapper.eq(KbDocument::getKnowledgeBaseId, id);
        List<KbDocument> docs = documentMapper.selectList(docWrapper);
        if (!docs.isEmpty()) {
            Set<Long> docIds = docs.stream().map(KbDocument::getId).collect(Collectors.toSet());
            LambdaQueryWrapper<KbDocumentTag> docTagWrapper = new LambdaQueryWrapper<>();
            docTagWrapper.in(KbDocumentTag::getDocumentId, docIds);
            documentTagMapper.delete(docTagWrapper);
        }
        documentMapper.delete(docWrapper);

        LambdaQueryWrapper<KbTag> tagWrapper = new LambdaQueryWrapper<>();
        tagWrapper.eq(KbTag::getKnowledgeBaseId, id);
        tagMapper.delete(tagWrapper);
    }

    // ==================== 目录 ====================

    @Override
    public List<DirectoryResponse> getDirectoryTree(Long knowledgeBaseId) {
        LambdaQueryWrapper<KbDirectory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KbDirectory::getKnowledgeBaseId, knowledgeBaseId);
        wrapper.orderByAsc(KbDirectory::getSort);
        List<KbDirectory> allDirs = directoryMapper.selectList(wrapper);

        List<DirectoryResponse> allResponses = allDirs.stream().map(dir -> {
            DirectoryResponse resp = new DirectoryResponse();
            resp.setId(dir.getId());
            resp.setParentId(dir.getParentId());
            resp.setName(dir.getName());
            resp.setSort(dir.getSort());
            return resp;
        }).collect(Collectors.toList());

        return buildTree(allResponses, 0L);
    }

    @Override
    public void createDirectory(DirectorySaveRequest request) {
        KbDirectory entity = new KbDirectory();
        entity.setKnowledgeBaseId(request.getKnowledgeBaseId());
        entity.setParentId(request.getParentId() != null ? request.getParentId() : 0L);
        entity.setName(request.getName());
        directoryMapper.insert(entity);
    }

    @Override
    public void updateDirectory(DirectorySaveRequest request) {
        KbDirectory entity = directoryMapper.selectById(request.getId());
        if (entity == null) {
            throw new RuntimeException("目录不存在");
        }
        entity.setName(request.getName());
        if (request.getParentId() != null) {
            entity.setParentId(request.getParentId());
        }
        directoryMapper.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDirectory(Long id) {
        directoryMapper.deleteById(id);
        // 递归删除子目录
        LambdaQueryWrapper<KbDirectory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KbDirectory::getParentId, id);
        List<KbDirectory> children = directoryMapper.selectList(wrapper);
        for (KbDirectory child : children) {
            deleteDirectory(child.getId());
        }
    }

    // ==================== 文档 ====================

    @Override
    public List<DocumentResponse> listDocuments(DocumentQueryRequest request) {
        LambdaQueryWrapper<KbDocument> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KbDocument::getKnowledgeBaseId, request.getKnowledgeBaseId());
        if (request.getDirectoryId() != null) {
            wrapper.eq(KbDocument::getDirectoryId, request.getDirectoryId());
        }
        wrapper.orderByDesc(KbDocument::getUpdateTime);
        List<KbDocument> list = documentMapper.selectList(wrapper);

        // 按标签筛选
        if (request.getTagId() != null) {
            LambdaQueryWrapper<KbDocumentTag> tagWrapper = new LambdaQueryWrapper<>();
            tagWrapper.eq(KbDocumentTag::getTagId, request.getTagId());
            List<KbDocumentTag> docTags = documentTagMapper.selectList(tagWrapper);
            Set<Long> docIds = docTags.stream().map(KbDocumentTag::getDocumentId).collect(Collectors.toSet());
            list = list.stream().filter(d -> docIds.contains(d.getId())).collect(Collectors.toList());
        }

        return list.stream().map(this::toDocumentResponse).collect(Collectors.toList());
    }

    @Override
    public Page<DocumentResponse> listAllDocuments(DocumentPageRequest request) {
        long pageNum = request.getPageNum() != null ? request.getPageNum() : 1L;
        long pageSize = request.getPageSize() != null ? request.getPageSize() : 12L;

        LambdaQueryWrapper<KbDocument> wrapper = new LambdaQueryWrapper<>();
        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            wrapper.like(KbDocument::getTitle, request.getKeyword());
        }
        wrapper.orderByDesc(KbDocument::getUpdateTime);

        Page<KbDocument> page = documentMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

        Page<DocumentResponse> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        if (page.getRecords().isEmpty()) {
            result.setRecords(new ArrayList<>());
            return result;
        }
        // 批量查询所属知识库名称
        Set<Long> kbIds = page.getRecords().stream()
                .map(KbDocument::getKnowledgeBaseId).collect(Collectors.toSet());
        Map<Long, String> kbNameMap = knowledgeBaseMapper.selectBatchIds(kbIds).stream()
                .collect(Collectors.toMap(KbKnowledgeBase::getId, KbKnowledgeBase::getName, (a, b) -> a));
        List<DocumentResponse> records = page.getRecords().stream().map(doc -> {
            DocumentResponse resp = toDocumentResponse(doc);
            resp.setKnowledgeBaseName(kbNameMap.get(doc.getKnowledgeBaseId()));
            return resp;
        }).collect(Collectors.toList());
        result.setRecords(records);
        return result;
    }

    @Override
    public DocumentResponse getDocumentById(Long id) {
        KbDocument entity = documentMapper.selectById(id);
        if (entity == null) {
            throw new RuntimeException("文档不存在");
        }
        return toDocumentResponse(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DocumentResponse createDocument(DocumentSaveRequest request) {
        List<String> cleanTags = filterBlankTags(request.getTags());
        KbDocument entity = new KbDocument();
        entity.setKnowledgeBaseId(request.getKnowledgeBaseId());
        entity.setTitle(request.getTitle());
        entity.setContent(request.getContent() != null ? request.getContent() : "");
        entity.setDirectoryId(request.getDirectoryId());
        entity.setTags(cleanTags);
        documentMapper.insert(entity);
        // 同步文档-标签关联表，供按标签筛选使用
        syncDocumentTags(entity.getId(), entity.getKnowledgeBaseId(), cleanTags);
        return toDocumentResponse(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDocument(DocumentSaveRequest request) {
        KbDocument entity = documentMapper.selectById(request.getId());
        if (entity == null) {
            throw new RuntimeException("文档不存在");
        }
        if (request.getTitle() != null) {
            entity.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            entity.setContent(request.getContent());
        }
        if (request.getDirectoryId() != null) {
            entity.setDirectoryId(request.getDirectoryId());
        }
        if (request.getTags() != null) {
            List<String> cleanTags = filterBlankTags(request.getTags());
            entity.setTags(cleanTags);
            documentMapper.updateById(entity);
            syncDocumentTags(entity.getId(), entity.getKnowledgeBaseId(), cleanTags);
        } else {
            documentMapper.updateById(entity);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDocument(Long id) {
        documentMapper.deleteById(id);
        // 删除关联标签
        LambdaQueryWrapper<KbDocumentTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KbDocumentTag::getDocumentId, id);
        documentTagMapper.delete(wrapper);
    }

    // ==================== 标签 ====================

    @Override
    public List<TagResponse> listTags(Long knowledgeBaseId) {
        LambdaQueryWrapper<KbTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KbTag::getKnowledgeBaseId, knowledgeBaseId);
        wrapper.orderByAsc(KbTag::getName);
        List<KbTag> list = tagMapper.selectList(wrapper);
        return list.stream().map(this::toTagResponse).collect(Collectors.toList());
    }

    @Override
    public void createTag(TagSaveRequest request) {
        // 检查同知识库下标签名是否已存在
        LambdaQueryWrapper<KbTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KbTag::getKnowledgeBaseId, request.getKnowledgeBaseId());
        wrapper.eq(KbTag::getName, request.getName());
        if (tagMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("标签名称已存在");
        }
        KbTag entity = new KbTag();
        entity.setKnowledgeBaseId(request.getKnowledgeBaseId());
        entity.setName(request.getName());
        entity.setColor(request.getColor());
        tagMapper.insert(entity);
    }

    @Override
    public void updateTag(TagSaveRequest request) {
        KbTag entity = tagMapper.selectById(request.getId());
        if (entity == null) {
            throw new RuntimeException("标签不存在");
        }
        entity.setName(request.getName());
        if (request.getColor() != null) {
            entity.setColor(request.getColor());
        }
        tagMapper.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTag(Long id) {
        tagMapper.deleteById(id);
        // 清理引用该标签的文档关联
        LambdaQueryWrapper<KbDocumentTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KbDocumentTag::getTagId, id);
        documentTagMapper.delete(wrapper);
    }

    // ==================== 附件 ====================

    @Override
    public List<AttachmentResponse> listAttachments(Long documentId) {
        LambdaQueryWrapper<FileLinkObj> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FileLinkObj::getAreaType, FileAreaTypeEnum.KNOWLEDGE);
        wrapper.eq(FileLinkObj::getLinkType, FileLinkTypeEnum.DOC_ATTACHMENT);
        wrapper.eq(FileLinkObj::getLinkId, documentId);
        wrapper.orderByDesc(FileLinkObj::getCreateTime);
        List<FileLinkObj> links = fileLinkObjMapper.selectList(wrapper);

        List<AttachmentResponse> result = new ArrayList<>();
        for (FileLinkObj link : links) {
            SysFile file = sysFileMapper.selectById(link.getFileId());
            if (file == null) {
                continue;
            }
            AttachmentResponse resp = new AttachmentResponse();
            resp.setId(link.getId());
            resp.setFileId(file.getId());
            resp.setFileName(file.getOriginalName());
            resp.setFileSize(file.getFileSize());
            resp.setFileUrl(resolveFileUrl(file));
            resp.setCreateTime(link.getCreateTime());
            result.add(resp);
        }
        return result;
    }

    @Override
    public void bindAttachment(AttachmentBindRequest request) {
        SysFile file = sysFileMapper.selectById(request.getFileId());
        if (file == null) {
            throw new RuntimeException("文件不存在");
        }
        FileLinkObj link = new FileLinkObj();
        link.setFileId(request.getFileId());
        link.setAreaType(FileAreaTypeEnum.KNOWLEDGE);
        link.setLinkType(FileLinkTypeEnum.DOC_ATTACHMENT);
        link.setLinkId(request.getDocumentId());
        fileLinkObjMapper.insert(link);
    }

    @Override
    public void deleteAttachment(Long id) {
        fileLinkObjMapper.deleteById(id);
    }

    // ==================== 评论 ====================

    @Override
    public List<CommentResponse> listComments(Long documentId) {
        LambdaQueryWrapper<KbComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KbComment::getDocumentId, documentId);
        wrapper.orderByAsc(KbComment::getCreateTime);
        List<KbComment> all = commentMapper.selectList(wrapper);

        List<CommentResponse> responses = all.stream()
                .map(this::toCommentResponse).collect(Collectors.toList());

        // 组装两层树：顶级评论 + 一级回复
        Map<Long, CommentResponse> idMap = responses.stream()
                .collect(Collectors.toMap(CommentResponse::getId, resp -> resp));
        List<CommentResponse> roots = new ArrayList<>();
        for (CommentResponse resp : responses) {
            Long parentId = resp.getParentId();
            if (parentId == null || parentId == 0L) {
                roots.add(resp);
                continue;
            }
            CommentResponse parent = idMap.get(parentId);
            if (parent == null) {
                // 父评论已删除，降级为顶级展示
                roots.add(resp);
                continue;
            }
            if (parent.getChildren() == null) {
                parent.setChildren(new ArrayList<>());
            }
            parent.getChildren().add(resp);
        }
        return roots;
    }

    @Override
    public CommentResponse addComment(CommentSaveRequest request) {
        KbComment entity = new KbComment();
        entity.setKnowledgeBaseId(request.getKnowledgeBaseId());
        entity.setDocumentId(request.getDocumentId());
        entity.setParentId(request.getParentId() != null ? request.getParentId() : 0L);
        entity.setContent(request.getContent());
        // 评论人信息显式写入（createBy 由全局填充为 system，不能作为评论人）
        Long userId = SecurityUtils.getCurrentUserId();
        entity.setCommenterId(userId);
        if (userId != null) {
            User user = userMapper.selectById(userId);
            if (user != null) {
                entity.setCommenterName(user.getNickname() != null ? user.getNickname() : user.getUsername());
                entity.setCommenterAvatar(user.getAvatar());
            }
        }
        if (entity.getCommenterName() == null) {
            entity.setCommenterName(SecurityUtils.getCurrentUsername());
        }
        commentMapper.insert(entity);
        return toCommentResponse(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long id) {
        KbComment entity = commentMapper.selectById(id);
        if (entity == null) {
            return;
        }
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null || !userId.equals(entity.getCommenterId())) {
            throw new RuntimeException("只能删除本人的评论");
        }
        commentMapper.deleteById(id);
        // 级联删除其下的回复
        LambdaQueryWrapper<KbComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KbComment::getParentId, id);
        commentMapper.delete(wrapper);
    }

    // ==================== 私有方法 ====================

    /**
     * 同步文档-标签关联表（先清空再按标签名重建）
     *
     * @param documentId      文档ID
     * @param knowledgeBaseId 所属知识库ID
     * @param tagNames        标签名称列表
     */
    private void syncDocumentTags(Long documentId, Long knowledgeBaseId, List<String> tagNames) {
        // 先删除该文档已有的标签关联
        LambdaQueryWrapper<KbDocumentTag> delWrapper = new LambdaQueryWrapper<>();
        delWrapper.eq(KbDocumentTag::getDocumentId, documentId);
        documentTagMapper.delete(delWrapper);

        if (tagNames == null || tagNames.isEmpty()) {
            return;
        }

        // 按知识库ID + 名称查出对应标签，建立 名称 -> ID 映射
        LambdaQueryWrapper<KbTag> tagWrapper = new LambdaQueryWrapper<>();
        tagWrapper.eq(KbTag::getKnowledgeBaseId, knowledgeBaseId);
        tagWrapper.in(KbTag::getName, tagNames);
        List<KbTag> tags = tagMapper.selectList(tagWrapper);
        Map<String, Long> nameToId = tags.stream()
                .collect(Collectors.toMap(KbTag::getName, KbTag::getId, (a, b) -> a));

        // 重建关联记录（去重，跳过不存在的标签名）
        Set<Long> inserted = new HashSet<>();
        for (String name : tagNames) {
            Long tagId = nameToId.get(name);
            if (tagId == null || !inserted.add(tagId)) {
                continue;
            }
            KbDocumentTag relation = new KbDocumentTag();
            relation.setDocumentId(documentId);
            relation.setTagId(tagId);
            documentTagMapper.insert(relation);
        }
    }

    private KnowledgeBaseResponse toKnowledgeBaseResponse(KbKnowledgeBase entity) {
        KnowledgeBaseResponse resp = new KnowledgeBaseResponse();
        resp.setId(entity.getId());
        resp.setName(entity.getName());
        resp.setDescription(entity.getDescription());
        resp.setUpdateTime(entity.getUpdateTime());
        resp.setCreateTime(entity.getCreateTime());
        return resp;
    }

    private DocumentResponse toDocumentResponse(KbDocument entity) {
        DocumentResponse resp = new DocumentResponse();
        resp.setId(entity.getId());
        resp.setKnowledgeBaseId(entity.getKnowledgeBaseId());
        resp.setTitle(entity.getTitle());
        resp.setContent(entity.getContent());
        resp.setDirectoryId(entity.getDirectoryId());
        resp.setTags(entity.getTags() != null ? entity.getTags() : new ArrayList<>());
        resp.setUpdateTime(entity.getUpdateTime());
        resp.setCreateTime(entity.getCreateTime());
        return resp;
    }

    /**
     * 过滤空白标签名
     */
    private List<String> filterBlankTags(List<String> tags) {
        if (tags == null) {
            return new ArrayList<>();
        }
        return tags.stream()
                .filter(t -> t != null && !t.trim().isEmpty())
                .collect(Collectors.toList());
    }

    private TagResponse toTagResponse(KbTag entity) {
        TagResponse resp = new TagResponse();
        resp.setId(entity.getId());
        resp.setName(entity.getName());
        resp.setColor(entity.getColor());
        return resp;
    }

    /**
     * 构建树结构
     */
    private List<DirectoryResponse> buildTree(List<DirectoryResponse> allNodes, Long parentId) {
        return allNodes.stream()
                .filter(node -> Objects.equals(node.getParentId(), parentId))
                .peek(node -> node.setChildren(buildTree(allNodes, node.getId())))
                .collect(Collectors.toList());
    }

    private CommentResponse toCommentResponse(KbComment entity) {
        CommentResponse resp = new CommentResponse();
        resp.setId(entity.getId());
        resp.setParentId(entity.getParentId());
        resp.setContent(entity.getContent());
        resp.setCommenterId(entity.getCommenterId());
        resp.setCommenterName(entity.getCommenterName());
        resp.setCommenterAvatar(resolveAvatarUrl(entity.getCommenterAvatar()));
        resp.setCreateTime(entity.getCreateTime());
        return resp;
    }

    /**
     * 将文件的 COS key 转为预签名访问 URL（已是 http 链接则原样返回）
     */
    private String resolveFileUrl(SysFile file) {
        String key = file.getFilePath();
        if (key == null || key.isEmpty()) {
            return file.getFileUrl();
        }
        if (key.startsWith("http")) {
            return key;
        }
        return cosService.getFileUrl(key);
    }

    /**
     * 将头像 COS key 转为预签名访问 URL
     */
    private String resolveAvatarUrl(String avatar) {
        if (avatar == null || avatar.isEmpty() || avatar.startsWith("http")) {
            return avatar;
        }
        return cosService.getFileUrl(avatar);
    }
}
