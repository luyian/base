package com.base.stock.fund.controller;

import com.base.common.result.Result;
import com.base.stock.fund.dto.FundConfigRequest;
import com.base.stock.fund.dto.FundValuationResponse;
import com.base.stock.fund.entity.FundConfig;
import com.base.stock.fund.service.FundService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
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

    /**
     * 查询基金列表（带缓存估值）
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
     * 创建基金
     */
    @ApiOperation("创建基金")
    @PostMapping
    public Result<Long> create(@Validated @RequestBody FundConfigRequest request) {
        Long fundId = fundService.createFund(request);
        return Result.success(fundId);
    }

    /**
     * 更新基金
     */
    @ApiOperation("更新基金")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Validated @RequestBody FundConfigRequest request) {
        fundService.updateFund(id, request);
        return Result.success();
    }

    /**
     * 删除基金
     */
    @ApiOperation("删除基金")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        fundService.deleteFund(id);
        return Result.success();
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

    /**
     * 批量获取基金实时估值
     */
    @ApiOperation("批量获取基金实时估值")
    @PostMapping("/valuation/batch")
    public Result<List<FundValuationResponse>> batchGetValuation(@RequestBody List<Long> fundIds) {
        List<FundValuationResponse> responses = fundService.batchGetValuation(fundIds);
        return Result.success(responses);
    }

    /**
     * 获取所有基金实时估值
     */
    @ApiOperation("获取所有基金实时估值")
    @GetMapping("/valuation/all")
    public Result<List<FundValuationResponse>> getAllValuation() {
        List<FundValuationResponse> responses = fundService.getAllValuation();
        return Result.success(responses);
    }
}
