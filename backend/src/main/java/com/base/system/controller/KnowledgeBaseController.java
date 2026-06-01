package com.base.system.controller;

import com.base.system.annotation.OperationLog;
import com.base.system.common.Result;
import com.base.system.dto.knowledge.KnowledgeBaseResponse;
import com.base.system.dto.knowledge.KnowledgeBaseSaveRequest;
import com.base.system.service.KnowledgeBaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 知识库控制器
 */
@Api(tags = "知识库管理")
@RestController
@RequestMapping("/knowledge-base")
@RequiredArgsConstructor
public class KnowledgeBaseController {

    private final KnowledgeBaseService knowledgeBaseService;

    /**
     * 获取知识库列表
     */
    @ApiOperation("获取知识库列表")
    @GetMapping
    public Result<List<KnowledgeBaseResponse>> list() {
        return Result.success(knowledgeBaseService.listKnowledgeBases());
    }

    /**
     * 获取知识库详情
     */
    @ApiOperation("获取知识库详情")
    @GetMapping("/{id}")
    public Result<KnowledgeBaseResponse> getById(@PathVariable Long id) {
        return Result.success(knowledgeBaseService.getKnowledgeBaseById(id));
    }

    /**
     * 创建知识库
     */
    @ApiOperation("创建知识库")
    @PostMapping
    @OperationLog(module = "知识库", operation = "创建知识库")
    public Result<Void> create(@Validated @RequestBody KnowledgeBaseSaveRequest request) {
        knowledgeBaseService.createKnowledgeBase(request);
        return Result.success();
    }

    /**
     * 更新知识库
     */
    @ApiOperation("更新知识库")
    @PutMapping
    @OperationLog(module = "知识库", operation = "更新知识库")
    public Result<Void> update(@Validated @RequestBody KnowledgeBaseSaveRequest request) {
        knowledgeBaseService.updateKnowledgeBase(request);
        return Result.success();
    }

    /**
     * 删除知识库
     */
    @ApiOperation("删除知识库")
    @DeleteMapping("/{id}")
    @OperationLog(module = "知识库", operation = "删除知识库")
    public Result<Void> delete(@PathVariable Long id) {
        knowledgeBaseService.deleteKnowledgeBase(id);
        return Result.success();
    }
}
