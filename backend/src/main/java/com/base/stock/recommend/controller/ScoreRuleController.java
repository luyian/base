package com.base.stock.recommend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.common.result.Result;
import com.base.stock.recommend.entity.ScoreRule;
import com.base.stock.recommend.service.ScoreRuleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 打分规则配置控制器
 *
 * @author base
 */
@Api(tags = "打分规则配置")
@RestController
@RequestMapping("/stock/recommend/rule")
@RequiredArgsConstructor
public class ScoreRuleController {

    private final ScoreRuleService scoreRuleService;

    /**
     * 查询规则列表
     */
    @ApiOperation("查询规则列表")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('stock:rule:list')")
    public Result<List<ScoreRule>> list() {
        List<ScoreRule> rules = scoreRuleService.list();
        return Result.success(rules);
    }

    /**
     * 分页查询规则列表
     */
    @ApiOperation("分页查询规则列表")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('stock:rule:list')")
    public Result<Page<ScoreRule>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ScoreRule> pageParam = new Page<>(page, size);
        Page<ScoreRule> result = scoreRuleService.page(pageParam);
        return Result.success(result);
    }

    /**
     * 根据ID查询规则
     */
    @ApiOperation("根据ID查询规则")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('stock:rule:list')")
    public Result<ScoreRule> getById(@PathVariable Long id) {
        ScoreRule rule = scoreRuleService.getById(id);
        return Result.success(rule);
    }

    /**
     * 更新规则配置
     */
    @ApiOperation("更新规则配置")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('stock:rule:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody ScoreRule rule) {
        rule.setId(id);
        scoreRuleService.updateById(rule);
        return Result.success();
    }

    /**
     * 启用规则
     */
    @ApiOperation("启用规则")
    @PostMapping("/{id}/enable")
    @PreAuthorize("hasAuthority('stock:rule:edit')")
    public Result<Void> enable(@PathVariable Long id) {
        scoreRuleService.enableRule(id);
        return Result.success();
    }

    /**
     * 禁用规则
     */
    @ApiOperation("禁用规则")
    @PostMapping("/{id}/disable")
    @PreAuthorize("hasAuthority('stock:rule:edit')")
    public Result<Void> disable(@PathVariable Long id) {
        scoreRuleService.disableRule(id);
        return Result.success();
    }
}
