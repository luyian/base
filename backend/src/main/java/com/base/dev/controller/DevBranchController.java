package com.base.dev.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.dev.dto.BranchQueryRequest;
import com.base.dev.dto.BranchResponse;
import com.base.dev.dto.BranchSaveRequest;
import com.base.dev.dto.BranchStatsResponse;
import com.base.dev.service.DevBranchService;
import com.base.system.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 代码分支管理控制器
 *
 * @author base
 */
@Api(tags = "代码分支管理")
@RestController
@RequestMapping("/dev/branch")
public class DevBranchController {

    @Autowired
    private DevBranchService devBranchService;

    /**
     * 分页查询分支列表
     */
    @ApiOperation("分页查询分支列表")
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('dev:branch:list')")
    public Result<Page<BranchResponse>> pageBranches(@RequestBody BranchQueryRequest request) {
        Page<BranchResponse> page = devBranchService.pageBranches(request);
        return Result.success(page);
    }

    /**
     * 新增分支记录
     */
    @ApiOperation("新增分支记录")
    @PostMapping
    @PreAuthorize("hasAuthority('dev:branch:add')")
    public Result<Void> addBranch(@Validated @RequestBody BranchSaveRequest request) {
        devBranchService.addBranch(request);
        return Result.success();
    }

    /**
     * 编辑分支记录
     */
    @ApiOperation("编辑分支记录")
    @PutMapping
    @PreAuthorize("hasAuthority('dev:branch:edit')")
    public Result<Void> updateBranch(@Validated @RequestBody BranchSaveRequest request) {
        devBranchService.updateBranch(request);
        return Result.success();
    }

    /**
     * 删除分支记录
     */
    @ApiOperation("删除分支记录")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('dev:branch:delete')")
    public Result<Void> deleteBranch(@PathVariable Long id) {
        devBranchService.deleteBranch(id);
        return Result.success();
    }

    /**
     * 获取当前生产分支
     */
    @ApiOperation("获取当前生产分支")
    @GetMapping("/current-prod")
    public Result<String> getCurrentProdBranch() {
        String prodBranch = devBranchService.getCurrentProdBranch();
        return Result.success(prodBranch);
    }

    /**
     * 更新当前生产分支
     */
    @ApiOperation("更新当前生产分支")
    @PutMapping("/current-prod")
    @PreAuthorize("hasAuthority('dev:branch:edit')")
    public Result<Void> updateCurrentProdBranch(@RequestBody java.util.Map<String, String> body) {
        String prodBranch = body.get("prodBranch");
        devBranchService.updateCurrentProdBranch(prodBranch);
        return Result.success();
    }

    /**
     * 获取分支统计信息
     */
    @ApiOperation("获取分支统计信息")
    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('dev:branch:list')")
    public Result<BranchStatsResponse> getStats() {
        BranchStatsResponse stats = devBranchService.getStats();
        return Result.success(stats);
    }

    /**
     * 完成分支记录
     */
    @ApiOperation("完成分支记录")
    @PutMapping("/complete/{id}")
    @PreAuthorize("hasAuthority('dev:branch:edit')")
    public Result<Void> completeBranch(@PathVariable Long id) {
        devBranchService.completeBranch(id);
        return Result.success();
    }
}
