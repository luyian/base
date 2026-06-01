package com.base.system.controller;

import com.base.system.annotation.OperationLog;
import com.base.system.common.Result;
import com.base.system.dto.knowledge.AttachmentBindRequest;
import com.base.system.dto.knowledge.AttachmentResponse;
import com.base.system.service.KnowledgeBaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 知识库文档附件控制器
 */
@Api(tags = "知识库文档附件管理")
@RestController
@RequestMapping("/attachments")
@RequiredArgsConstructor
public class AttachmentController {

    private final KnowledgeBaseService knowledgeBaseService;

    /**
     * 获取文档附件列表
     */
    @ApiOperation("获取文档附件列表")
    @GetMapping
    public Result<List<AttachmentResponse>> list(@RequestParam Long documentId) {
        return Result.success(knowledgeBaseService.listAttachments(documentId));
    }

    /**
     * 绑定文档附件
     */
    @ApiOperation("绑定文档附件")
    @PostMapping
    @OperationLog(module = "知识库", operation = "上传文档附件")
    public Result<Void> bind(@Validated @RequestBody AttachmentBindRequest request) {
        knowledgeBaseService.bindAttachment(request);
        return Result.success();
    }

    /**
     * 删除文档附件
     */
    @ApiOperation("删除文档附件")
    @DeleteMapping("/{id}")
    @OperationLog(module = "知识库", operation = "删除文档附件")
    public Result<Void> delete(@PathVariable Long id) {
        knowledgeBaseService.deleteAttachment(id);
        return Result.success();
    }
}
