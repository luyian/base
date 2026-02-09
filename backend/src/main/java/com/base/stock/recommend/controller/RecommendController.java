package com.base.stock.recommend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.common.result.Result;
import com.base.stock.entity.StockInfo;
import com.base.stock.mapper.StockInfoMapper;
import com.base.stock.recommend.entity.RecommendStock;
import com.base.stock.recommend.entity.ScoreRecord;
import com.base.stock.recommend.mapper.ScoreRecordMapper;
import com.base.stock.recommend.service.RecommendService;
import com.base.stock.recommend.service.ScoreService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    /**
     * 分页查询推荐股票列表
     */
    @ApiOperation("分页查询推荐股票列表")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('stock:recommend:list')")
    public Result<Page<Map<String, Object>>> list(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate recommendDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {

        // 如果未指定日期，使用最新推荐日期
        if (recommendDate == null) {
            recommendDate = recommendService.getLatestRecommendDate();
            if (recommendDate == null) {
                return Result.success(new Page<>());
            }
        }

        Page<RecommendStock> recommendPage = recommendService.pageRecommend(recommendDate, page, size);

        // 关联股票信息
        Page<Map<String, Object>> resultPage = new Page<>(page, size);
        resultPage.setTotal(recommendPage.getTotal());

        List<Map<String, Object>> records = recommendPage.getRecords().stream().map(recommend -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", recommend.getId());
            map.put("stockCode", recommend.getStockCode());
            map.put("recommendDate", recommend.getRecommendDate());
            map.put("totalScore", recommend.getTotalScore());
            map.put("hitRuleCount", recommend.getHitRuleCount());
            map.put("totalRuleCount", recommend.getTotalRuleCount());
            map.put("hitRate", recommend.getHitRate());

            // 查询股票信息
            StockInfo stockInfo = stockInfoMapper.selectOne(
                    new LambdaQueryWrapper<StockInfo>()
                            .eq(StockInfo::getStockCode, recommend.getStockCode())
                            .eq(StockInfo::getDeleted, 0)
            );
            if (stockInfo != null) {
                map.put("stockName", stockInfo.getStockName());
                map.put("market", stockInfo.getMarket());
            }

            return map;
        }).collect(Collectors.toList());

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
     * 手动触发打分
     */
    @ApiOperation("手动触发打分")
    @PostMapping("/execute")
    @PreAuthorize("hasAuthority('stock:recommend:execute')")
    public Result<Void> executeScore(
            @RequestParam(required = false) String stockCode,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate scoreDate) {

        if (scoreDate == null) {
            scoreDate = LocalDate.now();
        }

        if (stockCode != null && !stockCode.isEmpty()) {
            // 对单只股票打分
            scoreService.executeStockScore(stockCode, scoreDate);
        } else {
            // 对所有股票打分
            scoreService.executeAllStockScore(scoreDate);
        }

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
