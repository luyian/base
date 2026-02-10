package com.base.stock.controller;

import com.base.common.result.Result;
import com.base.stock.entity.ApiToken;
import com.base.stock.service.TokenManagerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Token 管理控制器
 *
 * @author base
 */
@Api(tags = "Token管理")
@RestController
@RequestMapping("/stock/token")
@RequiredArgsConstructor
public class TokenController {

    private final TokenManagerService tokenManagerService;

    /**
     * 查询 Token 列表
     */
    @ApiOperation("查询Token列表")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('stock:token:list')")
    public Result<List<ApiToken>> list(
            @RequestParam(required = false) String provider,
            @RequestParam(required = false) Integer status) {
        List<ApiToken> list = tokenManagerService.listTokens(provider, status);
        return Result.success(list);
    }

    /**
     * 根据 ID 获取 Token
     */
    @ApiOperation("根据ID获取Token")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('stock:token:query')")
    public Result<ApiToken> getById(@PathVariable Long id) {
        ApiToken token = tokenManagerService.getTokenById(id);
        return Result.success(token);
    }

    /**
     * 添加 Token
     */
    @ApiOperation("添加Token")
    @PostMapping
    @PreAuthorize("hasAuthority('stock:token:add')")
    public Result<Long> add(@RequestBody ApiToken apiToken) {
        Long id = tokenManagerService.addToken(apiToken);
        return Result.success(id);
    }

    /**
     * 更新 Token
     */
    @ApiOperation("更新Token")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('stock:token:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody ApiToken apiToken) {
        apiToken.setId(id);
        tokenManagerService.updateToken(apiToken);
        return Result.success();
    }

    /**
     * 作废 Token
     */
    @ApiOperation("作废Token")
    @PutMapping("/{id}/disable")
    @PreAuthorize("hasAuthority('stock:token:edit')")
    public Result<Void> disable(@PathVariable Long id) {
        tokenManagerService.disableToken(id);
        return Result.success();
    }

    /**
     * 启用 Token
     */
    @ApiOperation("启用Token")
    @PutMapping("/{id}/enable")
    @PreAuthorize("hasAuthority('stock:token:edit')")
    public Result<Void> enable(@PathVariable Long id) {
        tokenManagerService.enableToken(id);
        return Result.success();
    }

    /**
     * 删除 Token
     */
    @ApiOperation("删除Token")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('stock:token:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        tokenManagerService.deleteToken(id);
        return Result.success();
    }

    /**
     * 批量删除 Token
     */
    @ApiOperation("批量删除Token")
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('stock:token:delete')")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        tokenManagerService.batchDeleteTokens(ids);
        return Result.success();
    }

    /**
     * 重置每日计数
     */
    @ApiOperation("重置每日计数")
    @PostMapping("/reset-daily")
    @PreAuthorize("hasAuthority('stock:token:edit')")
    public Result<Void> resetDaily(@RequestParam(defaultValue = "itick") String provider) {
        tokenManagerService.resetDailyCount(provider);
        return Result.success();
    }
}
