package com.base.system.controller;

import com.base.system.annotation.OperationLog;
import com.base.system.common.Result;
import com.base.system.dto.knowledge.CommentResponse;
import com.base.system.dto.knowledge.CommentSaveRequest;
import com.base.system.service.KnowledgeBaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 知识库文档评论控制器
 */
@Api(tags = "知识库文档评论管理")
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final KnowledgeBaseService knowledgeBaseService;

    /**
     * 获取文档评论列表
     */
    @ApiOperation("获取文档评论列表")
    @GetMapping
    public Result<List<CommentResponse>> list(@RequestParam Long documentId) {
        return Result.success(knowledgeBaseService.listComments(documentId));
    }

    /**
     * 发表评论
     */
    @ApiOperation("发表评论")
    @PostMapping
    @OperationLog(module = "知识库", operation = "发表评论")
    public Result<CommentResponse> add(@Validated @RequestBody CommentSaveRequest request) {
        return Result.success(knowledgeBaseService.addComment(request));
    }

    /**
     * 删除评论
     */
    @ApiOperation("删除评论")
    @DeleteMapping("/{id}")
    @OperationLog(module = "知识库", operation = "删除评论")
    public Result<Void> delete(@PathVariable Long id) {
        knowledgeBaseService.deleteComment(id);
        return Result.success();
    }
}
