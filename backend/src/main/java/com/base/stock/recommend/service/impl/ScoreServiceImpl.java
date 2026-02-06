package com.base.stock.recommend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.base.stock.entity.StockInfo;
import com.base.stock.entity.StockKline;
import com.base.stock.mapper.StockInfoMapper;
import com.base.stock.mapper.StockKlineMapper;
import com.base.stock.recommend.engine.ScoreEngine;
import com.base.stock.recommend.entity.RecommendStock;
import com.base.stock.recommend.entity.ScoreRecord;
import com.base.stock.recommend.entity.ScoreRule;
import com.base.stock.recommend.mapper.RecommendStockMapper;
import com.base.stock.recommend.mapper.ScoreRecordMapper;
import com.base.stock.recommend.service.ScoreRuleService;
import com.base.stock.recommend.service.ScoreService;
import com.base.stock.recommend.strategy.ScoreResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 打分服务实现类
 *
 * @author base
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScoreServiceImpl implements ScoreService {

    private final ScoreEngine scoreEngine;
    private final ScoreRuleService scoreRuleService;
    private final StockInfoMapper stockInfoMapper;
    private final StockKlineMapper stockKlineMapper;
    private final ScoreRecordMapper scoreRecordMapper;
    private final RecommendStockMapper recommendStockMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeStockScore(String stockCode, LocalDate scoreDate) {
        log.info("开始对股票 {} 执行打分，日期：{}", stockCode, scoreDate);

        // 1. 查询股票信息
        StockInfo stockInfo = stockInfoMapper.selectOne(
                new LambdaQueryWrapper<StockInfo>()
                        .eq(StockInfo::getStockCode, stockCode)
                        .eq(StockInfo::getDeleted, 0)
        );

        if (stockInfo == null) {
            log.warn("股票 {} 不存在", stockCode);
            return;
        }

        // 2. 查询启用的规则
        List<ScoreRule> rules = scoreRuleService.listEnabledRules();
        if (rules.isEmpty()) {
            log.warn("没有启用的打分规则");
            return;
        }

        // 3. 查询K线数据（至少60条，用于计算MA60）
        List<StockKline> klineData = stockKlineMapper.selectList(
                new LambdaQueryWrapper<StockKline>()
                        .eq(StockKline::getStockCode, stockCode)
                        .le(StockKline::getTradeDate, scoreDate)
                        .orderByAsc(StockKline::getTradeDate)
                        .last("LIMIT 100")  // 取最近100条数据
        );

        if (klineData.size() < 60) {
            log.warn("股票 {} 的K线数据不足60条，无法打分", stockCode);
            return;
        }

        // 4. 执行打分
        Map<String, ScoreResult> resultMap = scoreEngine.executeScore(stockInfo, klineData, rules, scoreDate);

        // 5. 保存打分记录
        saveScoreRecords(stockCode, scoreDate, rules, resultMap);

        // 6. 汇总并保存推荐记录
        saveRecommendRecord(stockCode, scoreDate, rules, resultMap);

        log.info("股票 {} 打分完成", stockCode);
    }

    @Override
    public void executeBatchScore(List<String> stockCodes, LocalDate scoreDate) {
        log.info("开始批量打分，股票数量：{}，日期：{}", stockCodes.size(), scoreDate);
        for (String stockCode : stockCodes) {
            try {
                executeStockScore(stockCode, scoreDate);
            } catch (Exception e) {
                log.error("股票 {} 打分失败", stockCode, e);
            }
        }
        log.info("批量打分完成");
    }

    @Override
    public void executeAllStockScore(LocalDate scoreDate) {
        log.info("开始对所有股票执行打分，日期：{}", scoreDate);

        // 查询所有正常状态的股票
        List<StockInfo> stocks = stockInfoMapper.selectList(
                new LambdaQueryWrapper<StockInfo>()
                        .eq(StockInfo::getStatus, 1)
                        .eq(StockInfo::getDeleted, 0)
        );

        log.info("共有 {} 只股票需要打分", stocks.size());

        int successCount = 0;
        int failCount = 0;

        for (StockInfo stock : stocks) {
            try {
                executeStockScore(stock.getStockCode(), scoreDate);
                successCount++;
            } catch (Exception e) {
                log.error("股票 {} 打分失败", stock.getStockCode(), e);
                failCount++;
            }
        }

        log.info("所有股票打分完成，成功：{}，失败：{}", successCount, failCount);

        // 计算排名
        calculateRank(scoreDate);
    }

    @Override
    public void calculateRank(LocalDate scoreDate) {
        log.info("开始计算排名，日期：{}", scoreDate);
        recommendStockMapper.updateRank(scoreDate);
        log.info("排名计算完成");
    }

    /**
     * 保存打分记录
     */
    private void saveScoreRecords(String stockCode, LocalDate scoreDate, List<ScoreRule> rules, Map<String, ScoreResult> resultMap) {
        for (ScoreRule rule : rules) {
            ScoreResult result = resultMap.get(rule.getRuleCode());
            if (result == null) {
                continue;
            }

            try {
                // 计算加权得分
                BigDecimal score = BigDecimal.valueOf(result.getScore());
                BigDecimal weightedScore = score.multiply(rule.getWeight()).setScale(2, RoundingMode.HALF_UP);

                // 构建打分记录
                ScoreRecord record = new ScoreRecord();
                record.setStockCode(stockCode);
                record.setRuleCode(rule.getRuleCode());
                record.setScoreDate(scoreDate);
                record.setScore(score);
                record.setWeightedScore(weightedScore);
                record.setHitFlag(result.isHit() ? 1 : 0);
                record.setDetailJson(objectMapper.writeValueAsString(result.getDetail()));

                // 删除旧记录（如果存在）
                scoreRecordMapper.delete(
                        new LambdaQueryWrapper<ScoreRecord>()
                                .eq(ScoreRecord::getStockCode, stockCode)
                                .eq(ScoreRecord::getRuleCode, rule.getRuleCode())
                                .eq(ScoreRecord::getScoreDate, scoreDate)
                );

                // 插入新记录
                scoreRecordMapper.insert(record);

            } catch (Exception e) {
                log.error("保存打分记录失败，股票：{}，规则：{}", stockCode, rule.getRuleCode(), e);
            }
        }
    }

    /**
     * 保存推荐记录
     */
    private void saveRecommendRecord(String stockCode, LocalDate scoreDate, List<ScoreRule> rules, Map<String, ScoreResult> resultMap) {
        try {
            // 计算总分和命中数
            BigDecimal totalScore = BigDecimal.ZERO;
            int hitCount = 0;
            Map<String, Object> scoreDetail = new HashMap<>();

            for (ScoreRule rule : rules) {
                ScoreResult result = resultMap.get(rule.getRuleCode());
                if (result != null) {
                    if (result.isHit()) {
                        hitCount++;
                        BigDecimal weightedScore = BigDecimal.valueOf(result.getScore())
                                .multiply(rule.getWeight())
                                .setScale(2, RoundingMode.HALF_UP);
                        totalScore = totalScore.add(weightedScore);

                        // 记录得分明细
                        Map<String, Object> ruleDetail = new HashMap<>();
                        ruleDetail.put("ruleName", rule.getRuleName());
                        ruleDetail.put("score", result.getScore());
                        ruleDetail.put("weight", rule.getWeight());
                        ruleDetail.put("weightedScore", weightedScore);
                        scoreDetail.put(rule.getRuleCode(), ruleDetail);
                    }
                }
            }

            // 计算命中率
            BigDecimal hitRate = BigDecimal.ZERO;
            if (rules.size() > 0) {
                hitRate = BigDecimal.valueOf(hitCount)
                        .divide(BigDecimal.valueOf(rules.size()), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.HALF_UP);
            }

            // 构建推荐记录
            RecommendStock recommend = new RecommendStock();
            recommend.setStockCode(stockCode);
            recommend.setRecommendDate(scoreDate);
            recommend.setTotalScore(totalScore);
            recommend.setHitRuleCount(hitCount);
            recommend.setTotalRuleCount(rules.size());
            recommend.setHitRate(hitRate);
            recommend.setScoreDetail(objectMapper.writeValueAsString(scoreDetail));

            // 删除旧记录（如果存在）
            recommendStockMapper.delete(
                    new LambdaQueryWrapper<RecommendStock>()
                            .eq(RecommendStock::getStockCode, stockCode)
                            .eq(RecommendStock::getRecommendDate, scoreDate)
            );

            // 插入新记录
            recommendStockMapper.insert(recommend);

        } catch (Exception e) {
            log.error("保存推荐记录失败，股票：{}", stockCode, e);
        }
    }
}
