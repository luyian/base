package com.base.stock.recommend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.common.result.Result;
import com.base.common.util.SecurityUtils;
import com.base.stock.entity.StockInfo;
import com.base.stock.mapper.StockInfoMapper;
import com.base.stock.recommend.dto.RecommendQueryRequest;
import com.base.stock.recommend.entity.RecommendStock;
import com.base.stock.recommend.entity.ScoreRecord;
import com.base.stock.recommend.mapper.ScoreRecordMapper;
import com.base.stock.recommend.service.RecommendService;
import com.base.stock.recommend.service.ScoreService;
import com.base.stock.service.WatchlistService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 推荐股票控制器
 *
 * @author base
 */
@Api(tags = "股票推荐")
@RestController
@RequestMapping("/stock/recommend")
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;
    private final ScoreService scoreService;
    private final ScoreRecordMapper scoreRecordMapper;
    private final StockInfoMapper stockInfoMapper;
    private final WatchlistService watchlistService;

    /**
     * 分页查询推荐股票列表
     * 只显示已打分的推荐股票
     */
    @ApiOperation("分页查询推荐股票列表")
    @PostMapping("/list")
    @PreAuthorize("hasAuthority('stock:recommend:list')")
    public Result<Page<Map<String, Object>>> list(@RequestBody RecommendQueryRequest request) {

        LocalDate recommendDate = request.getRecommendDate();
        // 默认使用当天日期
        if (recommendDate == null) {
            recommendDate = LocalDate.now();
        }

        Long userId = SecurityUtils.getCurrentUserId();

        // 1. 分页查询当天已打分的推荐股票
        Page<RecommendStock> recommendPage = recommendService.pageRecommend(request);
        List<RecommendStock> scoredList = recommendPage.getRecords();

        // 2. 查询用户自选股票（用于标记）
        List<String> watchlistCodes = watchlistService.listStockCodesByUserId(userId);
        Set<String> watchlistCodeSet = new HashSet<>(watchlistCodes);

        // 3. 批量查询股票信息
        Map<String, StockInfo> stockInfoMap = new HashMap<>();
        if (!scoredList.isEmpty()) {
            Set<String> stockCodes = scoredList.stream()
                    .map(RecommendStock::getStockCode)
                    .collect(Collectors.toSet());
            List<StockInfo> stockInfoList = stockInfoMapper.selectList(
                    new LambdaQueryWrapper<StockInfo>()
                            .in(StockInfo::getStockCode, stockCodes)
                            .eq(StockInfo::getDeleted, 0)
            );
            stockInfoMap = stockInfoList.stream()
                    .collect(Collectors.toMap(StockInfo::getStockCode, s -> s));
        }

        // 4. 构建结果数据
        List<Map<String, Object>> records = new ArrayList<>();
        for (RecommendStock recommend : scoredList) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", recommend.getId());
            map.put("stockCode", recommend.getStockCode());
            map.put("recommendDate", recommend.getRecommendDate());
            map.put("totalScore", recommend.getTotalScore());
            map.put("hitRuleCount", recommend.getHitRuleCount());
            map.put("totalRuleCount", recommend.getTotalRuleCount());
            map.put("hitRate", recommend.getHitRate());
            map.put("isWatchlist", watchlistCodeSet.contains(recommend.getStockCode()));

            StockInfo stockInfo = stockInfoMap.get(recommend.getStockCode());
            if (stockInfo != null) {
                map.put("stockName", stockInfo.getStockName());
                map.put("market", stockInfo.getMarket());
            }

            records.add(map);
        }

        // 5. 构建分页结果
        Page<Map<String, Object>> resultPage = new Page<>(request.getCurrent(), request.getSize());
        resultPage.setTotal(recommendPage.getTotal());
        resultPage.setRecords(records);

        return Result.success(resultPage);
    }

    /**
     * 查询打分明细
     */
    @ApiOperation("查询打分明细")
    @GetMapping("/detail")
    @PreAuthorize("hasAuthority('stock:recommend:detail')")
    public Result<List<ScoreRecord>> getScoreDetail(
            @RequestParam String stockCode,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate scoreDate) {

        List<ScoreRecord> records = scoreRecordMapper.selectList(
                new LambdaQueryWrapper<ScoreRecord>()
                        .eq(ScoreRecord::getStockCode, stockCode)
                        .eq(ScoreRecord::getScoreDate, scoreDate)
        );

        return Result.success(records);
    }

    /**
     * 手动触发打分（全量）
     */
    @ApiOperation("手动触发打分")
    @PostMapping("/execute")
    @PreAuthorize("hasAuthority('stock:recommend:execute')")
    public Result<Void> executeScore(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate scoreDate) {

        if (scoreDate == null) {
            scoreDate = LocalDate.now();
        }

        // 对所有股票打分
        scoreService.executeAllStockScore(scoreDate);

        return Result.success();
    }

    /**
     * 单条股票打分
     */
    @ApiOperation("单条股票打分")
    @PostMapping("/score")
    @PreAuthorize("hasAuthority('stock:recommend:score')")
    public Result<Void> scoreSingleStock(
            @RequestParam String stockCode,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate scoreDate) {

        if (scoreDate == null) {
            scoreDate = LocalDate.now();
        }

        scoreService.executeStockScore(stockCode, scoreDate);

        return Result.success();
    }

    /**
     * 查询最新推荐日期
     */
    @ApiOperation("查询最新推荐日期")
    @GetMapping("/latest-date")
    @PreAuthorize("hasAuthority('stock:recommend:list')")
    public Result<LocalDate> getLatestDate() {
        LocalDate latestDate = recommendService.getLatestRecommendDate();
        return Result.success(latestDate);
    }
}
