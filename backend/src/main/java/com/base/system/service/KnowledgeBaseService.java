package com.base.system.service;

import com.base.system.dto.knowledge.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 知识库服务接口
 */
public interface KnowledgeBaseService {

    // ==================== 知识库 ====================

    /**
     * 获取知识库列表
     */
    List<KnowledgeBaseResponse> listKnowledgeBases();

    /**
     * 获取知识库详情
     */
    KnowledgeBaseResponse getKnowledgeBaseById(Long id);

    /**
     * 创建知识库
     */
    void createKnowledgeBase(KnowledgeBaseSaveRequest request);

    /**
     * 更新知识库
     */
    void updateKnowledgeBase(KnowledgeBaseSaveRequest request);

    /**
     * 删除知识库
     */
    void deleteKnowledgeBase(Long id);

    // ==================== 目录 ====================

    /**
     * 获取目录树
     */
    List<DirectoryResponse> getDirectoryTree(Long knowledgeBaseId);

    /**
     * 创建目录
     */
    void createDirectory(DirectorySaveRequest request);

    /**
     * 更新目录
     */
    void updateDirectory(DirectorySaveRequest request);

    /**
     * 删除目录
     */
    void deleteDirectory(Long id);

    // ==================== 文档 ====================

    /**
     * 获取文档列表
     */
    List<DocumentResponse> listDocuments(DocumentQueryRequest request);

    /**
     * 分页获取全部文档列表（跨知识库，文档维度）
     */
    Page<DocumentResponse> listAllDocuments(DocumentPageRequest request);

    /**
     * 获取文档详情
     */
    DocumentResponse getDocumentById(Long id);

    /**
     * 创建文档
     */
    DocumentResponse createDocument(DocumentSaveRequest request);

    /**
     * 更新文档
     */
    void updateDocument(DocumentSaveRequest request);

    /**
     * 删除文档
     */
    void deleteDocument(Long id);

    // ==================== 标签 ====================

    /**
     * 获取标签列表
     */
    List<TagResponse> listTags(Long knowledgeBaseId);

    /**
     * 获取所有标签（跨知识库，按名称去重）
     */
    List<TagResponse> listAllTags();

    /**
     * 创建标签
     */
    void createTag(TagSaveRequest request);

    /**
     * 更新标签
     */
    void updateTag(TagSaveRequest request);

    /**
     * 删除标签
     */
    void deleteTag(Long id);

    // ==================== 附件 ====================

    /**
     * 获取文档附件列表
     */
    List<AttachmentResponse> listAttachments(Long documentId);

    /**
     * 绑定文档附件
     */
    void bindAttachment(AttachmentBindRequest request);

    /**
     * 删除文档附件（仅删除关联，不删除文件本身）
     */
    void deleteAttachment(Long id);

    // ==================== 评论 ====================

    /**
     * 获取文档评论列表（顶级评论 + 一级回复）
     */
    List<CommentResponse> listComments(Long documentId);

    /**
     * 发表评论
     */
    CommentResponse addComment(CommentSaveRequest request);

    /**
     * 删除评论（仅本人可删）
     */
    void deleteComment(Long id);
}
