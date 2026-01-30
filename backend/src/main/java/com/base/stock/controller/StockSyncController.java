package com.base.stock.controller;

import com.base.common.result.Result;
import com.base.stock.service.StockSyncService;
import com.base.system.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
}
