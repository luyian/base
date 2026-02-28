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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 打分服务实现类
 *
 * 性能优化策略：
 * 1. 规则只查询一次，传递给所有任务
 * 2. 股票信息批量查询，构建Map
 * 3. 多线程并行计算打分（CPU密集型）
 * 4. 收集所有结果后批量写入数据库（减少事务开销）
 *
 * @author base
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScoreServiceImpl implements ScoreService {

    /**
     * 并行处理线程数
     */
    private static final int THREAD_POOL_SIZE = 8;
    /**
     * 批量操作大小
     */
    private static final int BATCH_SIZE = 100;

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

        // 3. 查询K线数据
        List<StockKline> klineData = stockKlineMapper.selectList(
                new LambdaQueryWrapper<StockKline>()
                        .eq(StockKline::getStockCode, stockCode)
                        .le(StockKline::getTradeDate, scoreDate)
                        .orderByAsc(StockKline::getTradeDate)
                        .last("LIMIT 100")
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
        long startTime = System.currentTimeMillis();

        // 1. 预先查询规则（只查一次）
        List<ScoreRule> rules = scoreRuleService.listEnabledRules();
        if (rules.isEmpty()) {
            log.warn("没有启用的打分规则");
            return;
        }
        log.info("加载规则完成，规则数量：{}", rules.size());

        // 2. 批量查询股票信息（只查一次）
        Map<String, StockInfo> stockInfoMap = stockInfoMapper.selectList(
                new LambdaQueryWrapper<StockInfo>()
                        .in(StockInfo::getStockCode, stockCodes)
                        .eq(StockInfo::getDeleted, 0)
        ).stream().collect(Collectors.toMap(StockInfo::getStockCode, s -> s));
        log.info("加载股票信息完成，数量：{}", stockInfoMap.size());

        // 3. 用于收集打分结果（线程安全队列）
        ConcurrentLinkedQueue<ScoreRecord> allScoreRecords = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<RecommendStock> allRecommendRecords = new ConcurrentLinkedQueue<>();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // 4. 创建线程池并行计算打分
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        try {
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for (String stockCode : stockCodes) {
                StockInfo stockInfo = stockInfoMap.get(stockCode);
                if (stockInfo == null) {
                    failCount.incrementAndGet();
                    continue;
                }

                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        // 查询K线数据（这是必须的IO操作）
                        List<StockKline> klineData = stockKlineMapper.selectList(
                                new LambdaQueryWrapper<StockKline>()
                                        .eq(StockKline::getStockCode, stockCode)
                                        .le(StockKline::getTradeDate, scoreDate)
                                        .orderByAsc(StockKline::getTradeDate)
                                        .last("LIMIT 100")
                        );

                        if (klineData.size() < 60) {
                            failCount.incrementAndGet();
                            return;
                        }

                        // 执行打分（纯CPU计算）
                        Map<String, ScoreResult> resultMap = scoreEngine.executeScore(stockInfo, klineData, rules, scoreDate);

                        // 构建打分记录（不写库，先收集）
                        List<ScoreRecord> scoreRecords = buildScoreRecords(stockCode, scoreDate, rules, resultMap);
                        allScoreRecords.addAll(scoreRecords);

                        // 构建推荐记录
                        RecommendStock recommend = buildRecommendRecord(stockCode, scoreDate, rules, resultMap);
                        if (recommend != null) {
                            allRecommendRecords.add(recommend);
                        }

                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        log.error("股票 {} 打分计算失败: {}", stockCode, e.getMessage());
                        failCount.incrementAndGet();
                    }
                }, executor);

                futures.add(future);
            }

            // 等待所有计算完成
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        } finally {
            executor.shutdown();
            try {
                executor.awaitTermination(30, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        long calcTime = System.currentTimeMillis() - startTime;
        log.info("打分计算完成，耗时：{}ms，开始批量写入数据库...", calcTime);

        // 5. 批量写入数据库（单线程，减少锁竞争）
        batchSaveResults(new ArrayList<>(allScoreRecords), new ArrayList<>(allRecommendRecords), scoreDate);

        long totalTime = System.currentTimeMillis() - startTime;
        log.info("批量打分完成，成功：{}，失败：{}，总耗时：{}ms", successCount.get(), failCount.get(), totalTime);
    }

    @Override
    public void executeAllStockScore(LocalDate scoreDate) {
        log.info("开始对所有股票执行打分，日期：{}", scoreDate);

        // 查询所有正常状态的股票代码
        List<String> stockCodes = stockInfoMapper.selectList(
                new LambdaQueryWrapper<StockInfo>()
                        .eq(StockInfo::getStatus, 1)
                        .eq(StockInfo::getDeleted, 0)
                        .select(StockInfo::getStockCode)
        ).stream().map(StockInfo::getStockCode).collect(Collectors.toList());

        log.info("共有 {} 只股票需要打分", stockCodes.size());

        // 使用优化后的批量打分
        executeBatchScore(stockCodes, scoreDate);
    }

    /**
     * 构建打分记录（不写库）
     */
    private List<ScoreRecord> buildScoreRecords(String stockCode, LocalDate scoreDate,
                                                 List<ScoreRule> rules, Map<String, ScoreResult> resultMap) {
        List<ScoreRecord> records = new ArrayList<>();

        for (ScoreRule rule : rules) {
            ScoreResult result = resultMap.get(rule.getRuleCode());
            if (result == null) {
                continue;
            }

            try {
                BigDecimal score = BigDecimal.valueOf(result.getScore());
                BigDecimal weightedScore = score.multiply(rule.getWeight()).setScale(2, RoundingMode.HALF_UP);

                ScoreRecord record = new ScoreRecord();
                record.setStockCode(stockCode);
                record.setRuleCode(rule.getRuleCode());
                record.setScoreDate(scoreDate);
                record.setScore(score);
                record.setWeightedScore(weightedScore);
                record.setHitFlag(result.isHit() ? 1 : 0);
                record.setDetailJson(objectMapper.writeValueAsString(result.getDetail()));

                records.add(record);
            } catch (Exception e) {
                log.error("构建打分记录失败，股票：{}，规则：{}", stockCode, rule.getRuleCode(), e);
            }
        }

        return records;
    }

    /**
     * 构建推荐记录（不写库）
     */
    private RecommendStock buildRecommendRecord(String stockCode, LocalDate scoreDate,
                                                 List<ScoreRule> rules, Map<String, ScoreResult> resultMap) {
        try {
            BigDecimal totalScore = BigDecimal.ZERO;
            int hitCount = 0;
            Map<String, Object> scoreDetail = new HashMap<>();

            for (ScoreRule rule : rules) {
                ScoreResult result = resultMap.get(rule.getRuleCode());
                if (result != null && result.isHit()) {
                    hitCount++;
                    BigDecimal weightedScore = BigDecimal.valueOf(result.getScore())
                            .multiply(rule.getWeight())
                            .setScale(2, RoundingMode.HALF_UP);
                    totalScore = totalScore.add(weightedScore);

                    Map<String, Object> ruleDetail = new HashMap<>();
                    ruleDetail.put("ruleName", rule.getRuleName());
                    ruleDetail.put("score", result.getScore());
                    ruleDetail.put("weight", rule.getWeight());
                    ruleDetail.put("weightedScore", weightedScore);
                    scoreDetail.put(rule.getRuleCode(), ruleDetail);
                }
            }

            BigDecimal hitRate = BigDecimal.ZERO;
            if (!rules.isEmpty()) {
                hitRate = BigDecimal.valueOf(hitCount)
                        .divide(BigDecimal.valueOf(rules.size()), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.HALF_UP);
            }

            RecommendStock recommend = new RecommendStock();
            recommend.setStockCode(stockCode);
            recommend.setRecommendDate(scoreDate);
            recommend.setTotalScore(totalScore);
            recommend.setHitRuleCount(hitCount);
            recommend.setTotalRuleCount(rules.size());
            recommend.setHitRate(hitRate);
            recommend.setScoreDetail(objectMapper.writeValueAsString(scoreDetail));

            return recommend;
        } catch (Exception e) {
            log.error("构建推荐记录失败，股票：{}", stockCode, e);
            return null;
        }
    }

    /**
     * 批量保存结果到数据库
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchSaveResults(List<ScoreRecord> scoreRecords, List<RecommendStock> recommendRecords, LocalDate scoreDate) {
        if (scoreRecords.isEmpty() && recommendRecords.isEmpty()) {
            return;
        }

        long startTime = System.currentTimeMillis();

        // 1. 收集所有股票代码
        List<String> stockCodes = recommendRecords.stream()
                .map(RecommendStock::getStockCode)
                .distinct()
                .collect(Collectors.toList());

        // 2. 分批删除旧数据
        if (!stockCodes.isEmpty()) {
            for (int i = 0; i < stockCodes.size(); i += BATCH_SIZE) {
                List<String> batch = stockCodes.subList(i, Math.min(i + BATCH_SIZE, stockCodes.size()));

                scoreRecordMapper.delete(
                        new LambdaQueryWrapper<ScoreRecord>()
                                .in(ScoreRecord::getStockCode, batch)
                                .eq(ScoreRecord::getScoreDate, scoreDate)
                );

                recommendStockMapper.delete(
                        new LambdaQueryWrapper<RecommendStock>()
                                .in(RecommendStock::getStockCode, batch)
                                .eq(RecommendStock::getRecommendDate, scoreDate)
                );
            }
        }

        long deleteTime = System.currentTimeMillis() - startTime;
        log.info("删除旧数据完成，耗时：{}ms", deleteTime);

        // 3. 批量插入打分记录（分批处理，每批500条）
        long insertStart = System.currentTimeMillis();
        if (!scoreRecords.isEmpty()) {
            for (int i = 0; i < scoreRecords.size(); i += BATCH_SIZE) {
                List<ScoreRecord> batch = scoreRecords.subList(i, Math.min(i + BATCH_SIZE, scoreRecords.size()));
                scoreRecordMapper.batchInsert(batch);
            }
        }
        log.info("插入打分记录完成，数量：{}，耗时：{}ms", scoreRecords.size(), System.currentTimeMillis() - insertStart);

        // 4. 批量插入推荐记录
        insertStart = System.currentTimeMillis();
        if (!recommendRecords.isEmpty()) {
            for (int i = 0; i < recommendRecords.size(); i += BATCH_SIZE) {
                List<RecommendStock> batch = recommendRecords.subList(i, Math.min(i + BATCH_SIZE, recommendRecords.size()));
                recommendStockMapper.batchInsert(batch);
            }
        }
        log.info("插入推荐记录完成，数量：{}，耗时：{}ms", recommendRecords.size(), System.currentTimeMillis() - insertStart);

        log.info("批量保存完成，总耗时：{}ms", System.currentTimeMillis() - startTime);
    }

    /**
     * 保存打分记录（单条模式使用）
     */
    private void saveScoreRecords(String stockCode, LocalDate scoreDate, List<ScoreRule> rules, Map<String, ScoreResult> resultMap) {
        for (ScoreRule rule : rules) {
            ScoreResult result = resultMap.get(rule.getRuleCode());
            if (result == null) {
                continue;
            }

            try {
                BigDecimal score = BigDecimal.valueOf(result.getScore());
                BigDecimal weightedScore = score.multiply(rule.getWeight()).setScale(2, RoundingMode.HALF_UP);

                ScoreRecord record = new ScoreRecord();
                record.setStockCode(stockCode);
                record.setRuleCode(rule.getRuleCode());
                record.setScoreDate(scoreDate);
                record.setScore(score);
                record.setWeightedScore(weightedScore);
                record.setHitFlag(result.isHit() ? 1 : 0);
                record.setDetailJson(objectMapper.writeValueAsString(result.getDetail()));

                // 删除旧记录
                scoreRecordMapper.delete(
                        new LambdaQueryWrapper<ScoreRecord>()
                                .eq(ScoreRecord::getStockCode, stockCode)
                                .eq(ScoreRecord::getRuleCode, rule.getRuleCode())
                                .eq(ScoreRecord::getScoreDate, scoreDate)
                );

                scoreRecordMapper.insert(record);

            } catch (Exception e) {
                log.error("保存打分记录失败，股票：{}，规则：{}", stockCode, rule.getRuleCode(), e);
            }
        }
    }

    /**
     * 保存推荐记录（单条模式使用）
     */
    private void saveRecommendRecord(String stockCode, LocalDate scoreDate, List<ScoreRule> rules, Map<String, ScoreResult> resultMap) {
        try {
            BigDecimal totalScore = BigDecimal.ZERO;
            int hitCount = 0;
            Map<String, Object> scoreDetail = new HashMap<>();

            for (ScoreRule rule : rules) {
                ScoreResult result = resultMap.get(rule.getRuleCode());
                if (result != null && result.isHit()) {
                    hitCount++;
                    BigDecimal weightedScore = BigDecimal.valueOf(result.getScore())
                            .multiply(rule.getWeight())
                            .setScale(2, RoundingMode.HALF_UP);
                    totalScore = totalScore.add(weightedScore);

                    Map<String, Object> ruleDetail = new HashMap<>();
                    ruleDetail.put("ruleName", rule.getRuleName());
                    ruleDetail.put("score", result.getScore());
                    ruleDetail.put("weight", rule.getWeight());
                    ruleDetail.put("weightedScore", weightedScore);
                    scoreDetail.put(rule.getRuleCode(), ruleDetail);
                }
            }

            BigDecimal hitRate = BigDecimal.ZERO;
            if (!rules.isEmpty()) {
                hitRate = BigDecimal.valueOf(hitCount)
                        .divide(BigDecimal.valueOf(rules.size()), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.HALF_UP);
            }

            RecommendStock recommend = new RecommendStock();
            recommend.setStockCode(stockCode);
            recommend.setRecommendDate(scoreDate);
            recommend.setTotalScore(totalScore);
            recommend.setHitRuleCount(hitCount);
            recommend.setTotalRuleCount(rules.size());
            recommend.setHitRate(hitRate);
            recommend.setScoreDetail(objectMapper.writeValueAsString(scoreDetail));

            // 删除旧记录
            recommendStockMapper.delete(
                    new LambdaQueryWrapper<RecommendStock>()
                            .eq(RecommendStock::getStockCode, stockCode)
                            .eq(RecommendStock::getRecommendDate, scoreDate)
            );

            recommendStockMapper.insert(recommend);

        } catch (Exception e) {
            log.error("保存推荐记录失败，股票：{}", stockCode, e);
        }
    }
}
