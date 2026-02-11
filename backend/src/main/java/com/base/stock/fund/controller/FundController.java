package com.base.stock.fund.controller;

import com.base.common.result.Result;
import com.base.stock.fund.dto.FundConfigRequest;
import com.base.stock.fund.dto.FundValuationResponse;
import com.base.stock.fund.entity.FundConfig;
import com.base.stock.fund.service.FundService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 基金控制器
 *
 * @author base
 */
@Api(tags = "基金估值管理")
@RestController
@RequestMapping("/stock/fund")
@RequiredArgsConstructor
public class FundController {

    private final FundService fundService;

    // ========== 所有用户接口 ==========

    /**
     * 查询所有基金列表（带缓存估值和自选状态）
     */
    @ApiOperation("查询基金列表（带缓存估值）")
    @GetMapping("/list")
    public Result<List<FundValuationResponse>> list() {
        List<FundValuationResponse> funds = fundService.listFundsWithCachedValuation();
        return Result.success(funds);
    }

    /**
     * 查询基金详情
     */
    @ApiOperation("查询基金详情")
    @GetMapping("/{id}")
    public Result<FundConfig> getById(@PathVariable Long id) {
        FundConfig fund = fundService.getFundById(id);
        return Result.success(fund);
    }

    /**
     * 获取单个基金实时估值
     */
    @ApiOperation("获取单个基金实时估值")
    @GetMapping("/{id}/valuation")
    public Result<FundValuationResponse> getValuation(@PathVariable Long id) {
        FundValuationResponse response = fundService.getValuation(id);
        return Result.success(response);
    }

    // ========== 自选接口（所有用户） ==========

    /**
     * 加自选
     */
    @ApiOperation("加自选")
    @PostMapping("/watchlist/{fundId}")
    public Result<Void> addWatchlist(@PathVariable Long fundId) {
        fundService.addWatchlist(fundId);
        return Result.success();
    }

    /**
     * 取消自选
     */
    @ApiOperation("取消自选")
    @DeleteMapping("/watchlist/{fundId}")
    public Result<Void> removeWatchlist(@PathVariable Long fundId) {
        fundService.removeWatchlist(fundId);
        return Result.success();
    }

    /**
     * 我的自选基金列表
     */
    @ApiOperation("我的自选基金列表")
    @GetMapping("/watchlist/list")
    public Result<List<FundConfig>> myWatchlist() {
        List<FundConfig> funds = fundService.listMyWatchlistFunds();
        return Result.success(funds);
    }

    /**
     * 我的自选基金估值
     */
    @ApiOperation("我的自选基金估值")
    @GetMapping("/watchlist/valuation")
    public Result<List<FundValuationResponse>> myWatchlistValuation() {
        List<FundValuationResponse> responses = fundService.getMyWatchlistValuation();
        return Result.success(responses);
    }

    // ========== 管理员接口 ==========

    /**
     * 创建基金
     */
    @ApiOperation("创建基金")
    @PreAuthorize("hasAuthority('stock:fund:add')")
    @PostMapping
    public Result<Long> create(@Validated @RequestBody FundConfigRequest request) {
        Long fundId = fundService.createFund(request);
        return Result.success(fundId);
    }

    /**
     * 更新基金
     */
    @ApiOperation("更新基金")
    @PreAuthorize("hasAuthority('stock:fund:edit')")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Validated @RequestBody FundConfigRequest request) {
        fundService.updateFund(id, request);
        return Result.success();
    }

    /**
     * 删除基金
     */
    @ApiOperation("删除基金")
    @PreAuthorize("hasAuthority('stock:fund:delete')")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        fundService.deleteFund(id);
        return Result.success();
    }
}
