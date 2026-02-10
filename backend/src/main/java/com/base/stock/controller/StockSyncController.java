package com.base.stock.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.common.result.Result;
import com.base.stock.dto.SyncFailureQueryRequest;
import com.base.stock.entity.SyncFailure;
import com.base.stock.service.StockSyncService;
import com.base.stock.service.SyncFailureService;
import com.base.stock.service.impl.StockSyncServiceImpl;
import com.base.system.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 股票同步控制器
 *
 * @author base
 */
@Api(tags = "股票同步")
@RestController
@RequestMapping("/stock/sync")
@RequiredArgsConstructor
public class StockSyncController {

    private final StockSyncService stockSyncService;
    private final SyncFailureService syncFailureService;

    /**
     * 同步股票列表
     */
    @ApiOperation("同步股票列表")
    @PostMapping("/stock-list")
    @PreAuthorize("hasAuthority('stock:sync:execute')")
    public Result<Integer> syncStockList(@RequestParam(defaultValue = "HK") String market) {
        int count = stockSyncService.syncStockList(market);
        return Result.success(count);
    }

    /**
     * 同步单只股票的 K 线数据
     */
    @ApiOperation("同步单只股票的K线数据")
    @PostMapping("/kline/{stockCode}")
    @PreAuthorize("hasAuthority('stock:sync:execute')")
    public Result<Integer> syncKline(
            @PathVariable String stockCode,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        int count = stockSyncService.syncKlineData(stockCode, startDate, endDate);
        return Result.success(count);
    }

    /**
     * 批量同步自选股票的 K 线数据
     */
    @ApiOperation("批量同步自选股票的K线数据")
    @PostMapping("/kline/batch")
    @PreAuthorize("hasAuthority('stock:sync:execute')")
    public Result<Integer> batchSyncKline(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        Long userId = SecurityUtils.getCurrentUserId();
        int count = stockSyncService.batchSyncKlineData(userId, startDate, endDate);
        return Result.success(count);
    }

  /**
     * 批量同步所有股票的 K 线数据
     */
    @ApiOperation("批量同步所有股票的K线数据")
    @PostMapping("/kline/all")
    @PreAuthorize("hasAuthority('stock:sync:execute')")
    public Result<Integer> batchSyncAllKline(
            @ApiParam("市场代码（HK/SH/SZ），为空则同步所有市场")
            @RequestParam(required = false) String market,
            @ApiParam("开始日期")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam("结束日期")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        int count = stockSyncService.batchSyncAllKlineData(market, startDate, endDate);
        return Result.success(count);
    }

    /**
     * 并发批量同步所有股票的 K 线数据
     */
    @ApiOperation("并发批量同步所有股票的K线数据")
    @PostMapping("/kline/all/concurrent")
    @PreAuthorize("hasAuthority('stock:sync:execute')")
    public Result<Integer> batchSyncAllKlineConcurrent(
            @ApiParam("市场代码（HK/SH/SZ），为空则同步所有市场")
            @RequestParam(required = false) String market,
            @ApiParam("开始日期")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam("结束日期")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        StockSyncServiceImpl impl = (StockSyncServiceImpl) stockSyncService;
        int count = impl.batchSyncAllKlineDataConcurrent(market, startDate, endDate);
        return Result.success(count);
    }

    /**
     * 补拉失败数据
     */
    @ApiOperation("补拉失败数据")
    @PostMapping("/retry-failed")
    @PreAuthorize("hasAuthority('stock:sync:failure:retry')")
    public Result<Integer> retryFailedSync(
            @ApiParam("股票代码（可选，为空则补拉所有）")
            @RequestParam(required = false) String stockCode,
            @ApiParam("最大重试次数")
            @RequestParam(defaultValue = "3") int maxRetryCount) {
        StockSyncServiceImpl impl = (StockSyncServiceImpl) stockSyncService;
        int count = impl.retryFailedSync(stockCode, maxRetryCount);
        return Result.success(count);
    }

    /**
     * 查询失败记录列表
     */
    @ApiOperation("查询失败记录列表")
    @PostMapping("/failure/list")
    @PreAuthorize("hasAuthority('stock:sync:failure:query')")
    public Result<Page<SyncFailure>> listFailures(@RequestBody SyncFailureQueryRequest request) {
        Page<SyncFailure> result = syncFailureService.pageFailures(request);
        return Result.success(result);
    }

    /**
     * 同步单只股票详情
     */
    @ApiOperation("同步单只股票详情")
    @PostMapping("/info/{stockCode}")
    @PreAuthorize("hasAuthority('stock:sync:execute')")
    public Result<Boolean> syncStockInfo(@PathVariable String stockCode) {
        boolean success = stockSyncService.syncStockInfo(stockCode);
        return Result.success(success);
    }

    /**
     * 批量同步股票详情
     */
    @ApiOperation("批量同步股票详情")
    @PostMapping("/info/batch")
    @PreAuthorize("hasAuthority('stock:sync:execute')")
    public Result<Integer> batchSyncStockInfo(
            @ApiParam("市场代码（HK/SH/SZ），为空则同步所有市场")
            @RequestParam(required = false) String market) {
        int count = stockSyncService.batchSyncStockInfo(market);
        return Result.success(count);
    }
}
