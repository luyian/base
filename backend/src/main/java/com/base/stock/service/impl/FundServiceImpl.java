package com.base.stock.fund.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.base.common.util.HttpClientUtil;
import com.base.common.util.SecurityUtils;
import com.base.stock.config.ITickConfig;
import com.base.stock.entity.ApiToken;
import com.base.stock.entity.StockInfo;
import com.base.stock.fund.dto.FundConfigRequest;
import com.base.stock.fund.dto.FundValuationResponse;
import com.base.stock.fund.dto.StockQuote;
import com.base.stock.fund.entity.FundConfig;
import com.base.stock.fund.entity.FundHolding;
import com.base.stock.fund.entity.FundValuationRecord;
import com.base.stock.fund.entity.FundWatchlist;
import com.base.stock.fund.mapper.FundConfigMapper;
import com.base.stock.fund.mapper.FundHoldingMapper;
import com.base.stock.fund.mapper.FundValuationRecordMapper;
import com.base.stock.fund.mapper.FundWatchlistMapper;
import com.base.stock.fund.service.FundService;
import com.base.stock.mapper.StockInfoMapper;
import com.base.stock.service.TokenManagerService;
import com.base.system.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 基金服务实现类
 *
 * @author base
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FundServiceImpl implements FundService {

    private static final String PROVIDER = "itick";
    private static final int BATCH_SIZE = 3;
    /** 基金估值缓存Key前缀 */
    private static final String FUND_VALUATION_CACHE_KEY = "fund:valuation:";
    /** 缓存过期时间（1小时） */
    private static final long CACHE_EXPIRE_SECONDS = 3600;
    /** 收盘时间：15:30 */
    private static final LocalTime MARKET_CLOSE_TIME = LocalTime.of(15, 30);

    private final FundConfigMapper fundConfigMapper;
    private final FundHoldingMapper fundHoldingMapper;
    private final FundWatchlistMapper fundWatchlistMapper;
    private final FundValuationRecordMapper fundValuationRecordMapper;
    private final StockInfoMapper stockInfoMapper;
    private final TokenManagerService tokenManagerService;
    private final ITickConfig iTickConfig;
    private final RedisUtil redisUtil;

    // ========== 基金管理（管理员） ==========

    @Override
    public List<FundConfig> listAllFunds() {
        LambdaQueryWrapper<FundConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(FundConfig::getCreateTime);
        return fundConfigMapper.selectList(wrapper);
    }

    @Override
    public FundConfig getFundById(Long id) {
        FundConfig fund = fundConfigMapper.selectById(id);
        if (fund == null) {
            throw new RuntimeException("基金不存在");
        }
        // 查询持仓列表（关联股票信息）
        List<FundHolding> holdings = fundHoldingMapper.selectHoldingsWithStockInfo(id);
        fund.setHoldings(holdings);
        return fund;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createFund(FundConfigRequest request) {
        FundConfig fund = new FundConfig();
        fund.setFundName(request.getFundName());
        fund.setFundCode(request.getFundCode());
        fund.setDescription(request.getDescription());
        fund.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        fundConfigMapper.insert(fund);
        saveHoldings(fund.getId(), request.getHoldings());
        return fund.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateFund(Long id, FundConfigRequest request) {
        FundConfig fund = fundConfigMapper.selectById(id);
        if (fund == null) {
            throw new RuntimeException("基金不存在");
        }
        fund.setFundName(request.getFundName());
        fund.setFundCode(request.getFundCode());
        fund.setDescription(request.getDescription());
        if (request.getStatus() != null) {
            fund.setStatus(request.getStatus());
        }
        fundConfigMapper.updateById(fund);

        // 删除原有持仓，创建新持仓
        LambdaQueryWrapper<FundHolding> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FundHolding::getFundId, id);
        fundHoldingMapper.delete(wrapper);
        saveHoldings(id, request.getHoldings());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFund(Long id) {
        FundConfig fund = fundConfigMapper.selectById(id);
        if (fund == null) {
            throw new RuntimeException("基金不存在");
        }
        // 删除持仓
        LambdaQueryWrapper<FundHolding> holdingWrapper = new LambdaQueryWrapper<>();
        holdingWrapper.eq(FundHolding::getFundId, id);
        fundHoldingMapper.delete(holdingWrapper);

        // 删除所有用户的自选关联
        LambdaQueryWrapper<FundWatchlist> watchlistWrapper = new LambdaQueryWrapper<>();
        watchlistWrapper.eq(FundWatchlist::getFundId, id);
        fundWatchlistMapper.delete(watchlistWrapper);

        // 删除基金配置
        fundConfigMapper.deleteById(id);
    }

    // ========== 用户自选 ==========

    @Override
    public void addWatchlist(Long fundId) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }
        // 校验基金存在
        FundConfig fund = fundConfigMapper.selectById(fundId);
        if (fund == null) {
            throw new RuntimeException("基金不存在");
        }
        // 检查是否已自选
        LambdaQueryWrapper<FundWatchlist> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FundWatchlist::getFundId, fundId)
                .eq(FundWatchlist::getUserId, userId);
        if (fundWatchlistMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("已在自选列表中");
        }
        FundWatchlist watchlist = new FundWatchlist();
        watchlist.setFundId(fundId);
        watchlist.setUserId(userId);
        fundWatchlistMapper.insert(watchlist);
    }

    @Override
    public void removeWatchlist(Long fundId) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }
        LambdaQueryWrapper<FundWatchlist> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FundWatchlist::getFundId, fundId)
                .eq(FundWatchlist::getUserId, userId);
        fundWatchlistMapper.delete(wrapper);
    }

    @Override
    public List<FundConfig> listMyWatchlistFunds() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }
        return listWatchlistFundsByUserId(userId);
    }

    // ========== 估值 ==========

    @Override
    public FundValuationResponse getValuation(Long fundId) {
        FundConfig fund = getFundById(fundId);
        FundValuationResponse response = calculateValuation(fund);

        // 缓存估值结果到Redis（1小时过期）
        response.setCacheTime(System.currentTimeMillis());
        String cacheKey = FUND_VALUATION_CACHE_KEY + fundId;
        redisUtil.set(cacheKey, response, CACHE_EXPIRE_SECONDS);
        log.info("基金估值已缓存，fundId: {}, cacheKey: {}", fundId, cacheKey);

        // 15:30 之后将估值持久化到数据库
        if (LocalTime.now().isAfter(MARKET_CLOSE_TIME)) {
            saveValuationRecord(fundId, response);
        }
        return response;
    }

    /**
     * 将估值结果持久化到数据库（upsert）
     */
    private void saveValuationRecord(Long fundId, FundValuationResponse response) {
        try {
            LocalDate today = LocalDate.now();
            FundValuationRecord existing = fundValuationRecordMapper.selectByFundIdAndDate(fundId, today);
            FundValuationRecord record = existing != null ? existing : new FundValuationRecord();
            record.setFundId(fundId);
            record.setTradeDate(today);
            record.setEstimatedChangePercent(response.getEstimatedChangePercent());
            record.setHoldingCount(response.getHoldingCount());
            record.setSuccessCount(response.getSuccessCount());
            record.setFailCount(response.getFailCount());
            record.setTotalWeight(response.getTotalWeight());
            record.setQuotesJson(JSON.toJSONString(response.getQuotes()));
            if (existing != null) {
                fundValuationRecordMapper.updateById(record);
            } else {
                fundValuationRecordMapper.insert(record);
            }
            log.info("基金估值记录已保存，fundId: {}, tradeDate: {}", fundId, today);
        } catch (Exception e) {
            log.error("保存基金估值记录失败，fundId: {}", fundId, e);
        }
    }

    @Override
    public FundValuationResponse getCachedValuation(Long fundId) {
        String cacheKey = FUND_VALUATION_CACHE_KEY + fundId;
        Object cached = redisUtil.get(cacheKey);
        if (cached != null) {
            return (FundValuationResponse) cached;
        }
        return null;
    }

    @Override
    public List<FundValuationResponse> getMyWatchlistValuation() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }
        return getAllValuationByUserId(userId);
    }

    @Override
    public List<FundValuationResponse> getAllValuationByUserId(Long userId) {
        List<FundConfig> funds = listWatchlistFundsByUserId(userId);
        if (funds.isEmpty()) {
            return Collections.emptyList();
        }

        List<FundValuationResponse> responses = new ArrayList<>();
        for (FundConfig fund : funds) {
            try {
                // 查询持仓
                List<FundHolding> holdings = fundHoldingMapper.selectHoldingsWithStockInfo(fund.getId());
                fund.setHoldings(holdings);
                FundValuationResponse response = calculateValuation(fund);
                // 缓存
                response.setCacheTime(System.currentTimeMillis());
                String cacheKey = FUND_VALUATION_CACHE_KEY + fund.getId();
                redisUtil.set(cacheKey, response, CACHE_EXPIRE_SECONDS);
                // 15:30 之后持久化到数据库
                if (LocalTime.now().isAfter(MARKET_CLOSE_TIME)) {
                    saveValuationRecord(fund.getId(), response);
                }
                responses.add(response);
            } catch (Exception e) {
                log.error("获取基金估值失败，fundId: {}", fund.getId(), e);
                FundValuationResponse errorResponse = new FundValuationResponse();
                errorResponse.setFundId(fund.getId());
                errorResponse.setFundName(fund.getFundName());
                errorResponse.setFundCode(fund.getFundCode());
                errorResponse.setAllSuccess(false);
                errorResponse.setErrorMsg(e.getMessage());
                responses.add(errorResponse);
            }
        }
        return responses;
    }

    @Override
    public List<FundValuationResponse> listFundsWithCachedValuation() {
        List<FundConfig> funds = listAllFunds();
        if (funds.isEmpty()) {
            return Collections.emptyList();
        }

        // 查询当前用户的自选列表
        Set<Long> watchlistFundIds = Collections.emptySet();
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId != null) {
            LambdaQueryWrapper<FundWatchlist> wWrapper = new LambdaQueryWrapper<>();
            wWrapper.eq(FundWatchlist::getUserId, userId);
            List<FundWatchlist> watchlist = fundWatchlistMapper.selectList(wWrapper);
            watchlistFundIds = watchlist.stream()
                    .map(FundWatchlist::getFundId)
                    .collect(Collectors.toSet());
        }

        List<FundValuationResponse> responses = new ArrayList<>();
        for (FundConfig fund : funds) {
            // 优先从当日数据库记录读取（15:30后的数据）
            FundValuationResponse dbRecord = getTodayDbValuation(fund.getId());
            if (dbRecord != null) {
                dbRecord.setFundName(fund.getFundName());
                dbRecord.setFundCode(fund.getFundCode());
                dbRecord.setDescription(fund.getDescription());
                dbRecord.setInWatchlist(watchlistFundIds.contains(fund.getId()));
                responses.add(dbRecord);
                continue;
            }
            FundValuationResponse cached = getCachedValuation(fund.getId());
            if (cached != null) {
                cached.setFundName(fund.getFundName());
                cached.setFundCode(fund.getFundCode());
                cached.setDescription(fund.getDescription());
                cached.setInWatchlist(watchlistFundIds.contains(fund.getId()));
                responses.add(cached);
            } else {
                FundValuationResponse response = new FundValuationResponse();
                response.setFundId(fund.getId());
                response.setFundName(fund.getFundName());
                response.setFundCode(fund.getFundCode());
                response.setDescription(fund.getDescription());
                response.setCacheTime(null);
                response.setInWatchlist(watchlistFundIds.contains(fund.getId()));
                responses.add(response);
            }
        }
        return responses;
    }

    // ========== 私有方法 ==========

    /**
     * 从数据库读取当日估值记录（15:30后才有数据）
     */
    private FundValuationResponse getTodayDbValuation(Long fundId) {
        if (LocalTime.now().isBefore(MARKET_CLOSE_TIME)) {
            return null;
        }
        FundValuationRecord record = fundValuationRecordMapper.selectByFundIdAndDate(fundId, LocalDate.now());
        if (record == null) {
            return null;
        }
        FundValuationResponse response = new FundValuationResponse();
        response.setFundId(record.getFundId());
        response.setEstimatedChangePercent(record.getEstimatedChangePercent());
        response.setHoldingCount(record.getHoldingCount());
        response.setSuccessCount(record.getSuccessCount());
        response.setFailCount(record.getFailCount());
        response.setTotalWeight(record.getTotalWeight());
        response.setAllSuccess(record.getFailCount() == null || record.getFailCount() == 0);
        response.setCacheTime(record.getUpdateTime() != null
                ? record.getUpdateTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
                : record.getCreateTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
        if (record.getQuotesJson() != null) {
            response.setQuotes(JSON.parseArray(record.getQuotesJson(), StockQuote.class));
        }
        return response;
    }

    /**
     * 按用户ID查询自选基金列表
     */
    private List<FundConfig> listWatchlistFundsByUserId(Long userId) {
        LambdaQueryWrapper<FundWatchlist> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FundWatchlist::getUserId, userId);
        List<FundWatchlist> watchlist = fundWatchlistMapper.selectList(wrapper);
        if (watchlist.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> fundIds = watchlist.stream()
                .map(FundWatchlist::getFundId)
                .collect(Collectors.toList());
        LambdaQueryWrapper<FundConfig> fundWrapper = new LambdaQueryWrapper<>();
        fundWrapper.in(FundConfig::getId, fundIds)
                .orderByDesc(FundConfig::getCreateTime);
        return fundConfigMapper.selectList(fundWrapper);
    }

    /**
     * 保存持仓明细
     */
    private void saveHoldings(Long fundId, List<FundConfigRequest.HoldingItem> holdings) {
        if (holdings == null || holdings.isEmpty()) {
            return;
        }
        for (FundConfigRequest.HoldingItem item : holdings) {
            FundHolding holding = new FundHolding();
            holding.setFundId(fundId);
            holding.setStockCode(item.getStockCode());
            holding.setWeight(item.getWeight());
            fundHoldingMapper.insert(holding);
        }
    }

    /**
     * 计算基金估值
     */
    private FundValuationResponse calculateValuation(FundConfig fund) {
        FundValuationResponse response = new FundValuationResponse();
        response.setFundId(fund.getId());
        response.setFundName(fund.getFundName());
        response.setFundCode(fund.getFundCode());
        response.setDescription(fund.getDescription());
        response.setFundConfig(fund);

        List<FundHolding> holdings = fund.getHoldings();
        if (holdings == null || holdings.isEmpty()) {
            response.setHoldingCount(0);
            response.setEstimatedChangePercent(BigDecimal.ZERO);
            response.setSuccessCount(0);
            response.setFailCount(0);
            response.setTotalWeight(BigDecimal.ZERO);
            response.setQuotes(Collections.emptyList());
            response.setAllSuccess(true);
            return response;
        }

        response.setHoldingCount(holdings.size());

        // 按市场分组
        Map<String, List<FundHolding>> marketGroups = groupByMarket(holdings);

        // 多线程获取报价
        Map<String, StockQuote> quoteMap = fetchQuotesConcurrently(marketGroups);

        // 计算估值
        List<StockQuote> quotes = new ArrayList<>();
        BigDecimal totalWeightedChange = BigDecimal.ZERO;
        BigDecimal totalWeight = BigDecimal.ZERO;
        int successCount = 0;
        int failCount = 0;

        for (FundHolding holding : holdings) {
            StockQuote quote = quoteMap.get(holding.getStockCode());
            if (quote == null) {
                quote = new StockQuote();
                quote.setStockCode(holding.getStockCode());
                quote.setStockName(holding.getStockName());
                quote.setMarket(holding.getMarket());
                quote.setWeight(holding.getWeight());
                quote.setSuccess(false);
                quote.setErrorMsg("未获取到报价");
                failCount++;
            } else {
                quote.setWeight(holding.getWeight());
                quote.setStockName(holding.getStockName());
                quote.setMarket(holding.getMarket());

                if (quote.getSuccess() && quote.getChangePercent() != null) {
                    BigDecimal weightedChange = quote.getChangePercent()
                            .multiply(holding.getWeight())
                            .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
                    quote.setWeightedChangePercent(weightedChange);
                    totalWeightedChange = totalWeightedChange.add(weightedChange);
                    totalWeight = totalWeight.add(holding.getWeight());
                    successCount++;
                } else {
                    failCount++;
                }
            }
            quotes.add(quote);
        }

        response.setQuotes(quotes);
        response.setSuccessCount(successCount);
        response.setFailCount(failCount);
        response.setTotalWeight(totalWeight);
        response.setEstimatedChangePercent(totalWeightedChange.setScale(2, RoundingMode.HALF_UP));
        response.setAllSuccess(failCount == 0);
        return response;
    }

    /**
     * 按市场分组持仓
     */
    private Map<String, List<FundHolding>> groupByMarket(List<FundHolding> holdings) {
        Map<String, List<FundHolding>> marketGroups = new HashMap<>();

        List<String> needQueryCodes = holdings.stream()
                .filter(h -> h.getMarket() == null || h.getMarket().isEmpty())
                .map(FundHolding::getStockCode)
                .distinct()
                .collect(Collectors.toList());

        Map<String, String> marketMap = new HashMap<>();
        if (!needQueryCodes.isEmpty()) {
            LambdaQueryWrapper<StockInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(StockInfo::getStockCode, needQueryCodes)
                    .select(StockInfo::getStockCode, StockInfo::getMarket);
            List<StockInfo> stockInfoList = stockInfoMapper.selectList(wrapper);
            marketMap = stockInfoList.stream()
                    .filter(s -> s.getMarket() != null)
                    .collect(Collectors.toMap(StockInfo::getStockCode, StockInfo::getMarket));
        }

        for (FundHolding holding : holdings) {
            String market = holding.getMarket();
            if (market == null || market.isEmpty()) {
                market = marketMap.get(holding.getStockCode());
                if (market == null) {
                    market = inferMarketByStockCode(holding.getStockCode());
                }
                holding.setMarket(market);
            }
            marketGroups.computeIfAbsent(market.toUpperCase(), k -> new ArrayList<>()).add(holding);
        }
        return marketGroups;
    }

    /**
     * 根据股票代码推断市场
     */
    private String inferMarketByStockCode(String stockCode) {
        if (stockCode.startsWith("60") || stockCode.startsWith("68")) {
            return "SH";
        }
        if (stockCode.startsWith("00") || stockCode.startsWith("30")) {
            return "SZ";
        }
        return "HK";
    }

    /**
     * 多线程获取报价
     */
    private Map<String, StockQuote> fetchQuotesConcurrently(Map<String, List<FundHolding>> marketGroups) {
        Map<String, StockQuote> quoteMap = new HashMap<>();

        int totalStocks = marketGroups.values().stream().mapToInt(List::size).sum();
        if (totalStocks <= BATCH_SIZE) {
            for (Map.Entry<String, List<FundHolding>> entry : marketGroups.entrySet()) {
                String market = entry.getKey();
                List<String> codes = entry.getValue().stream()
                        .map(FundHolding::getStockCode)
                        .collect(Collectors.toList());
                try {
                    Map<String, StockQuote> batchQuotes = fetchBatchQuotes(market, codes);
                    quoteMap.putAll(batchQuotes);
                } catch (Exception e) {
                    log.error("获取报价失败，market: {}, codes: {}", market, codes, e);
                    for (String code : codes) {
                        StockQuote quote = new StockQuote();
                        quote.setStockCode(code);
                        quote.setSuccess(false);
                        quote.setErrorMsg(e.getMessage());
                        quoteMap.put(code, quote);
                    }
                }
            }
            return quoteMap;
        }

        List<CompletableFuture<Map<String, StockQuote>>> futures = new ArrayList<>();
        for (Map.Entry<String, List<FundHolding>> entry : marketGroups.entrySet()) {
            String market = entry.getKey();
            List<FundHolding> holdings = entry.getValue();
            List<List<String>> batches = splitIntoBatches(
                    holdings.stream().map(FundHolding::getStockCode).collect(Collectors.toList()),
                    BATCH_SIZE
            );
            for (List<String> batch : batches) {
                CompletableFuture<Map<String, StockQuote>> future = CompletableFuture.supplyAsync(() -> {
                    Map<String, StockQuote> result = new HashMap<>();
                    try {
                        result = fetchBatchQuotes(market, batch);
                    } catch (Exception e) {
                        log.error("获取批量报价失败，market: {}, codes: {}", market, batch, e);
                        for (String code : batch) {
                            StockQuote quote = new StockQuote();
                            quote.setStockCode(code);
                            quote.setSuccess(false);
                            quote.setErrorMsg(e.getMessage());
                            result.put(code, quote);
                        }
                    }
                    return result;
                });
                futures.add(future);
            }
        }

        for (CompletableFuture<Map<String, StockQuote>> future : futures) {
            try {
                Map<String, StockQuote> result = future.get();
                quoteMap.putAll(result);
            } catch (Exception e) {
                log.error("获取报价结果失败", e);
            }
        }
        return quoteMap;
    }

    /**
     * 分批
     */
    private List<List<String>> splitIntoBatches(List<String> list, int batchSize) {
        List<List<String>> batches = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            batches.add(list.subList(i, Math.min(i + batchSize, list.size())));
        }
        return batches;
    }

    /**
     * 获取批量报价
     */
    private Map<String, StockQuote> fetchBatchQuotes(String market, List<String> codes) {
        Map<String, StockQuote> quotes = new HashMap<>();

        String codesParam = String.join(",", codes);
        String url = iTickConfig.getBaseUrl() + "/stock/quotes?region=" + market + "&codes=" + codesParam;

        ApiToken token = tokenManagerService.getNextTokenEntity(PROVIDER);
        Map<String, String> headers = new HashMap<>();
        headers.put("accept", "application/json");
        headers.put("Content-Type", "application/json");
        headers.put("token", token.getTokenValue());

        log.info("获取实时报价，market: {}, codes: {}, url: {}", market, codes, url);

        try {
            String response = HttpClientUtil.getWithRetry(url, headers, iTickConfig.getRetry());
            log.info("报价响应: {}", response);

            JSONObject jsonResponse = JSON.parseObject(response);
            int code = jsonResponse.getIntValue("code");

            if (code == 0) {
                JSONObject data = jsonResponse.getJSONObject("data");
                if (data != null) {
                    for (String stockCode : data.keySet()) {
                        JSONObject item = data.getJSONObject(stockCode);
                        StockQuote quote = parseQuote(item);
                        if (quote != null) {
                            quote.setStockCode(stockCode);
                            quotes.put(stockCode, quote);
                        }
                    }
                }
                tokenManagerService.resetTokenFailure(token.getId());
            } else {
                log.error("获取报价失败，code: {}, msg: {}", code, jsonResponse.getString("msg"));
                tokenManagerService.recordTokenFailure(token.getId());
                for (String stockCode : codes) {
                    StockQuote quote = new StockQuote();
                    quote.setStockCode(stockCode);
                    quote.setSuccess(false);
                    quote.setErrorMsg(jsonResponse.getString("msg"));
                    quotes.put(stockCode, quote);
                }
            }
        } catch (Exception e) {
            log.error("获取报价异常，market: {}, codes: {}", market, codes, e);
            tokenManagerService.recordTokenFailure(token.getId());
            for (String stockCode : codes) {
                StockQuote quote = new StockQuote();
                quote.setStockCode(stockCode);
                quote.setSuccess(false);
                quote.setErrorMsg(e.getMessage());
                quotes.put(stockCode, quote);
            }
        }
        return quotes;
    }

    /**
     * 解析报价数据
     */
    private StockQuote parseQuote(JSONObject item) {
        if (item == null) {
            return null;
        }
        StockQuote quote = new StockQuote();

        String symbol = item.getString("s");
        if (symbol != null && symbol.contains(".")) {
            String[] parts = symbol.split("\\.");
            quote.setStockCode(parts[0]);
            if (parts.length > 1) {
                quote.setMarket(parts[1]);
            }
        } else {
            quote.setStockCode(symbol);
        }

        quote.setPrice(item.getBigDecimal("ld"));
        quote.setLastClose(item.getBigDecimal("p"));
        quote.setChange(item.getBigDecimal("ch"));
        quote.setChangePercent(item.getBigDecimal("chp"));
        quote.setSuccess(true);
        return quote;
    }
}
