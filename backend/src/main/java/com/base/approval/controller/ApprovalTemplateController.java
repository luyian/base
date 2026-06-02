package com.base.approval.controller;

import com.base.approval.entity.ApprovalTemplate;
import com.base.approval.service.ApprovalTemplateService;
import com.base.common.result.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 审批模板控制器
 *
 * @author base
 */
@RestController
@RequestMapping("/approval/template")
@RequiredArgsConstructor
@Api(tags = "审批模板管理")
public class ApprovalTemplateController {

    private final ApprovalTemplateService approvalTemplateService;

    @GetMapping("/list")
    @ApiOperation("模板列表")
    public Result<IPage<ApprovalTemplate>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") int pageNum,
            @ApiParam("每页条数") @RequestParam(defaultValue = "10") int pageSize,
            @ApiParam("平台") @RequestParam(required = false) String platform,
            @ApiParam("状态") @RequestParam(required = false) Integer status) {
        return Result.success(approvalTemplateService.pageList(pageNum, pageSize, platform, status));
    }

    @GetMapping("/{id}")
    @ApiOperation("模板详情")
    public Result<ApprovalTemplate> detail(@PathVariable Long id) {
        return Result.success(approvalTemplateService.getById(id));
    }

    @PostMapping
    @ApiOperation("新增模板")
    public Result<Void> save(@RequestBody ApprovalTemplate template) {
        approvalTemplateService.save(template);
        return Result.success();
    }

    @PutMapping("/{id}")
    @ApiOperation("更新模板")
    public Result<Void> update(@PathVariable Long id, @RequestBody ApprovalTemplate template) {
        template.setId(id);
        approvalTemplateService.update(template);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除模板")
    public Result<Void> delete(@PathVariable Long id) {
        approvalTemplateService.deleteById(id);
        return Result.success();
    }
}
