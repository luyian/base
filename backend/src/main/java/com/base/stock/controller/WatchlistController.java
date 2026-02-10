package com.base.stock.controller;

import com.base.common.result.Result;
import com.base.stock.dto.MinuteKlineResponse;
import com.base.stock.entity.Watchlist;
import com.base.stock.recommend.service.ScoreService;
import com.base.stock.service.WatchlistService;
import com.base.system.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 自选股票控制器
 *
 * @author base
 */
@Api(tags = "自选股票")
@RestController
@RequestMapping("/stock/watchlist")
@RequiredArgsConstructor
public class WatchlistController {

    private final WatchlistService watchlistService;
    private final ScoreService scoreService;

    /**
     * 查询自选股票列表
     */
    @ApiOperation("查询自选股票列表")
    @GetMapping
    @PreAuthorize("hasAuthority('stock:watchlist:list')")
    public Result<List<Watchlist>> list() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<Watchlist> list = watchlistService.listByUserId(userId);
        return Result.success(list);
    }

    /**
     * 添加自选股票
     */
    @ApiOperation("添加自选股票")
    @PostMapping
    @PreAuthorize("hasAuthority('stock:watchlist:add')")
    public Result<Long> add(@RequestParam String stockCode,
                            @RequestParam(required = false) String remark) {
        Long userId = SecurityUtils.getCurrentUserId();
        Long id = watchlistService.addWatchlist(userId, stockCode, remark);
        return Result.success(id);
    }

    /**
     * 删除自选股票
     */
    @ApiOperation("删除自选股票")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('stock:watchlist:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        watchlistService.deleteWatchlist(id);
        return Result.success();
    }

    /**
     * 批量删除自选股票
     */
    @ApiOperation("批量删除自选股票")
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('stock:watchlist:delete')")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        watchlistService.batchDeleteWatchlist(ids);
        return Result.success();
    }

    /**
     * 调整排序
     */
    @ApiOperation("调整排序")
    @PutMapping("/{id}/sort")
    @PreAuthorize("hasAuthority('stock:watchlist:edit')")
    public Result<Void> updateSort(@PathVariable Long id, @RequestParam Integer sortOrder) {
        watchlistService.updateSortOrder(id, sortOrder);
        return Result.success();
    }

    /**
     * 检查是否已添加自选
     */
    @ApiOperation("检查是否已添加自选")
    @GetMapping("/check")
    public Result<Boolean> check(@RequestParam String stockCode) {
        Long userId = SecurityUtils.getCurrentUserId();
        boolean inWatchlist = watchlistService.isInWatchlist(userId, stockCode);
        return Result.success(inWatchlist);
    }

    /**
     * 获取分钟K线数据
     */
    @ApiOperation("获取分钟K线数据")
    @GetMapping("/minute-kline")
    @PreAuthorize("hasAuthority('stock:watchlist:list')")
    public Result<MinuteKlineResponse> getMinuteKline(
            @ApiParam("股票代码") @RequestParam String stockCode,
            @ApiParam("K线类型：1=1分钟，5=5分钟") @RequestParam(defaultValue = "1") int kType,
            @ApiParam("结束时间戳（毫秒），用于分页加载历史数据") @RequestParam(required = false) Long et,
            @ApiParam("返回条数") @RequestParam(defaultValue = "100") int limit) {
        MinuteKlineResponse response = watchlistService.getMinuteKline(stockCode, kType, et, limit);
        return Result.success(response);
    }

    /**
     * 对自选股票执行打分
     */
    @ApiOperation("对自选股票执行打分")
    @PostMapping("/score")
    @PreAuthorize("hasAuthority('stock:watchlist:score')")
    public Result<Void> scoreStock(
            @ApiParam("股票代码") @RequestParam String stockCode,
            @ApiParam("打分日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate scoreDate) {
        if (scoreDate == null) {
            scoreDate = LocalDate.now();
        }
        scoreService.executeStockScore(stockCode, scoreDate);
        return Result.success();
    }
}
