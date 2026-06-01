package com.base.system.controller;

import com.base.system.annotation.OperationLog;
import com.base.system.common.Result;
import com.base.system.dto.knowledge.DirectoryResponse;
import com.base.system.dto.knowledge.DirectorySaveRequest;
import com.base.system.service.KnowledgeBaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 知识库目录控制器
 */
@Api(tags = "知识库目录管理")
@RestController
@RequestMapping("/directories")
@RequiredArgsConstructor
public class DirectoryController {

    private final KnowledgeBaseService knowledgeBaseService;

    /**
     * 获取目录树
     */
    @ApiOperation("获取目录树")
    @GetMapping
    public Result<List<DirectoryResponse>> getTree(@RequestParam Long knowledgeBaseId) {
        return Result.success(knowledgeBaseService.getDirectoryTree(knowledgeBaseId));
    }

    /**
     * 创建目录
     */
    @ApiOperation("创建目录")
    @PostMapping
    @OperationLog(module = "知识库", operation = "创建目录")
    public Result<Void> create(@Validated @RequestBody DirectorySaveRequest request) {
        knowledgeBaseService.createDirectory(request);
        return Result.success();
    }

    /**
     * 更新目录
     */
    @ApiOperation("更新目录")
    @PutMapping
    @OperationLog(module = "知识库", operation = "更新目录")
    public Result<Void> update(@Validated @RequestBody DirectorySaveRequest request) {
        knowledgeBaseService.updateDirectory(request);
        return Result.success();
    }

    /**
     * 删除目录
     */
    @ApiOperation("删除目录")
    @DeleteMapping("/{id}")
    @OperationLog(module = "知识库", operation = "删除目录")
    public Result<Void> delete(@PathVariable Long id) {
        knowledgeBaseService.deleteDirectory(id);
        return Result.success();
    }
}
