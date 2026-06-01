package com.base.system.controller;

import com.base.system.annotation.OperationLog;
import com.base.system.common.Result;
import com.base.system.dto.knowledge.DocumentPageRequest;
import com.base.system.dto.knowledge.DocumentQueryRequest;
import com.base.system.dto.knowledge.DocumentResponse;
import com.base.system.dto.knowledge.DocumentSaveRequest;
import com.base.system.service.KnowledgeBaseService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 知识库文档控制器
 */
@Api(tags = "知识库文档管理")
@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final KnowledgeBaseService knowledgeBaseService;

    /**
     * 获取文档列表
     */
    @ApiOperation("获取文档列表")
    @GetMapping
    public Result<List<DocumentResponse>> list(DocumentQueryRequest request) {
        return Result.success(knowledgeBaseService.listDocuments(request));
    }

    /**
     * 获取全部文档列表（跨知识库，文档维度）
     */
    @ApiOperation("获取全部文档列表（分页）")
    @GetMapping("/all")
    public Result<Page<DocumentResponse>> listAll(DocumentPageRequest request) {
        return Result.success(knowledgeBaseService.listAllDocuments(request));
    }

    /**
     * 获取文档详情
     */
    @ApiOperation("获取文档详情")
    @GetMapping("/{id}")
    public Result<DocumentResponse> getById(@PathVariable Long id) {
        return Result.success(knowledgeBaseService.getDocumentById(id));
    }

    /**
     * 创建文档
     */
    @ApiOperation("创建文档")
    @PostMapping
    @OperationLog(module = "知识库", operation = "创建文档")
    public Result<DocumentResponse> create(@Validated @RequestBody DocumentSaveRequest request) {
        return Result.success(knowledgeBaseService.createDocument(request));
    }

    /**
     * 更新文档
     */
    @ApiOperation("更新文档")
    @PutMapping
    @OperationLog(module = "知识库", operation = "更新文档")
    public Result<Void> update(@Validated @RequestBody DocumentSaveRequest request) {
        knowledgeBaseService.updateDocument(request);
        return Result.success();
    }

    /**
     * 删除文档
     */
    @ApiOperation("删除文档")
    @DeleteMapping("/{id}")
    @OperationLog(module = "知识库", operation = "删除文档")
    public Result<Void> delete(@PathVariable Long id) {
        knowledgeBaseService.deleteDocument(id);
        return Result.success();
    }
}
