package com.base.system.controller;

import com.base.system.annotation.OperationLog;
import com.base.system.common.Result;
import com.base.system.dto.knowledge.TagResponse;
import com.base.system.dto.knowledge.TagSaveRequest;
import com.base.system.service.KnowledgeBaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 知识库标签控制器
 */
@Api(tags = "知识库标签管理")
@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {

    private final KnowledgeBaseService knowledgeBaseService;

    /**
     * 获取标签列表
     */
    @ApiOperation("获取标签列表")
    @GetMapping
    public Result<List<TagResponse>> list(@RequestParam Long knowledgeBaseId) {
        return Result.success(knowledgeBaseService.listTags(knowledgeBaseId));
    }

    /**
     * 创建标签
     */
    @ApiOperation("创建标签")
    @PostMapping
    @OperationLog(module = "知识库", operation = "创建标签")
    public Result<Void> create(@Validated @RequestBody TagSaveRequest request) {
        knowledgeBaseService.createTag(request);
        return Result.success();
    }

    /**
     * 更新标签
     */
    @ApiOperation("更新标签")
    @PutMapping
    @OperationLog(module = "知识库", operation = "更新标签")
    public Result<Void> update(@Validated @RequestBody TagSaveRequest request) {
        knowledgeBaseService.updateTag(request);
        return Result.success();
    }

    /**
     * 删除标签
     */
    @ApiOperation("删除标签")
    @DeleteMapping("/{id}")
    @OperationLog(module = "知识库", operation = "删除标签")
    public Result<Void> delete(@PathVariable Long id) {
        knowledgeBaseService.deleteTag(id);
        return Result.success();
    }
}
