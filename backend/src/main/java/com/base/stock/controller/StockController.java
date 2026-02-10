package com.base.stock.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.common.result.Result;
import com.base.stock.dto.StockQueryRequest;
import com.base.stock.entity.StockInfo;
import com.base.stock.entity.StockKline;
import com.base.stock.service.StockService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 股票查询控制器
 *
 * @author base
 */
@Api(tags = "股票查询")
@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    /**
     * 分页查询股票列表
     */
    @ApiOperation("分页查询股票列表")
    @PostMapping("/list")
    @PreAuthorize("hasAuthority('stock:info:list')")
    public Result<Page<StockInfo>> list(@RequestBody StockQueryRequest request) {
        Page<StockInfo> result = stockService.pageStocks(request);
        return Result.success(result);
    }

    /**
     * 根据股票代码获取股票信息
     */
    @ApiOperation("根据股票代码获取股票信息")
    @GetMapping("/{stockCode}")
    @PreAuthorize("hasAuthority('stock:info:query')")
    public Result<StockInfo> getByCode(@PathVariable String stockCode) {
        StockInfo stockInfo = stockService.getByStockCode(stockCode);
        return Result.success(stockInfo);
    }

    /**
     * 查询股票 K 线数据
     */
    @ApiOperation("查询股票K线数据")
    @GetMapping("/{stockCode}/kline")
    @PreAuthorize("hasAuthority('stock:kline:query')")
    public Result<List<StockKline>> getKline(
            @PathVariable String stockCode,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<StockKline> klineList = stockService.listKlineData(stockCode, startDate, endDate);
        return Result.success(klineList);
    }

    /**
     * 查询行业选项列表
     */
    @ApiOperation("查询行业选项列表")
    @GetMapping("/industry/options")
    @PreAuthorize("hasAuthority('stock:info:list')")
    public Result<List<Map<String, String>>> industryOptions() {
        return Result.success(stockService.listIndustryOptions());
    }
}
