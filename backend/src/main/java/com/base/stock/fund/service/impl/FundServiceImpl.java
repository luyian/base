package com.base.stock.fund.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
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
import com.base.stock.fund.mapper.FundConfigMapper;
import com.base.stock.fund.mapper.FundHoldingMapper;
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
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    /**
     * 基金估值缓存Key前缀
     */
    private static final String FUND_VALUATION_CACHE_KEY = "fund:valuation:";
    /**
     * 缓存过期时间（1小时）
     */
    private static final long CACHE_EXPIRE_SECONDS = 3600;

    private final FundConfigMapper fundConfigMapper;
    private final FundHoldingMapper fundHoldingMapper;
    private final StockInfoMapper stockInfoMapper;
    private final TokenManagerService tokenManagerService;
    private final ITickConfig iTickConfig;
    private final RedisUtil redisUtil;

    @Override
    public List<FundConfig> listFunds() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }

        LambdaQueryWrapper<FundConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FundConfig::getUserId, userId)
                .orderByDesc(FundConfig::getCreateTime);

        List<FundConfig> funds = fundConfigMapper.selectList(wrapper);

        // 查询每个基金的持仓数量
        for (FundConfig fund : funds) {
            LambdaQueryWrapper<FundHolding> holdingWrapper = new LambdaQueryWrapper<>();
            holdingWrapper.eq(FundHolding::getFundId, fund.getId());
            long count = fundHoldingMapper.selectCount(holdingWrapper);
            fund.setHoldings(Collections.emptyList());
        }

        return funds;
    }

    @Override
    public FundConfig getFundById(Long id) {
        FundConfig fund = fundConfigMapper.selectById(id);
        if (fund == null) {
            throw new RuntimeException("基金不存在");
        }

        // 校验权限
        Long userId = SecurityUtils.getCurrentUserId();
        if (!fund.getUserId().equals(userId)) {
            throw new RuntimeException("无权访问该基金");
        }

        // 查询持仓列表（关联股票信息）
        List<FundHolding> holdings = fundHoldingMapper.selectHoldingsWithStockInfo(id);
        fund.setHoldings(holdings);

        return fund;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createFund(FundConfigRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }

        // 创建基金配置
        FundConfig fund = new FundConfig();
        fund.setUserId(userId);
        fund.setFundName(request.getFundName());
        fund.setFundCode(request.getFundCode());
        fund.setDescription(request.getDescription());
        fund.setStatus(request.getStatus() != null ? request.getStatus() : 1);

        fundConfigMapper.insert(fund);

        // 创建持仓明细
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

        // 校验权限
        Long userId = SecurityUtils.getCurrentUserId();
        if (!fund.getUserId().equals(userId)) {
            throw new RuntimeException("无权修改该基金");
        }

        // 更新基金配置
        fund.setFundName(request.getFundName());
        fund.setFundCode(request.getFundCode());
        fund.setDescription(request.getDescription());
        if (request.getStatus() != null) {
            fund.setStatus(request.getStatus());
        }

        fundConfigMapper.updateById(fund);

        // 删除原有持仓
        LambdaQueryWrapper<FundHolding> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FundHolding::getFundId, id);
        fundHoldingMapper.delete(wrapper);

        // 创建新持仓
        saveHoldings(id, request.getHoldings());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFund(Long id) {
        FundConfig fund = fundConfigMapper.selectById(id);
        if (fund == null) {
            throw new RuntimeException("基金不存在");
        }

        // 校验权限
        Long userId = SecurityUtils.getCurrentUserId();
        if (!fund.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除该基金");
        }

        // 删除持仓
        LambdaQueryWrapper<FundHolding> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FundHolding::getFundId, id);
        fundHoldingMapper.delete(wrapper);

        // 删除基金配置
        fundConfigMapper.deleteById(id);
    }

    @Override
    public FundValuationResponse getValuation(Long fundId) {
        FundConfig fund = getFundById(fundId);
        FundValuationResponse response = calculateValuation(fund);

        // 缓存估值结果到Redis（1小时过期）
        response.setCacheTime(System.currentTimeMillis());
        String cacheKey = FUND_VALUATION_CACHE_KEY + fundId;
        redisUtil.set(cacheKey, response, CACHE_EXPIRE_SECONDS);
        log.info("基金估值已缓存，fundId: {}, cacheKey: {}", fundId, cacheKey);

        return response;
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
    public List<FundValuationResponse> listFundsWithCachedValuation() {
        List<FundConfig> funds = listFunds();
        if (funds.isEmpty()) {
            return Collections.emptyList();
        }

        List<FundValuationResponse> responses = new ArrayList<>();
        for (FundConfig fund : funds) {
            // 尝试从缓存获取估值
            FundValuationResponse cached = getCachedValuation(fund.getId());
            if (cached != null) {
                // 使用缓存数据，但更新基金基本信息（可能已修改）
                cached.setFundName(fund.getFundName());
                cached.setFundCode(fund.getFundCode());
                cached.setDescription(fund.getDescription());
                responses.add(cached);
            } else {
                // 无缓存，返回基本信息（不含估值）
                FundValuationResponse response = new FundValuationResponse();
                response.setFundId(fund.getId());
                response.setFundName(fund.getFundName());
                response.setFundCode(fund.getFundCode());
                response.setDescription(fund.getDescription());
                response.setCacheTime(null);
                responses.add(response);
            }
        }

        return responses;
    }

    @Override
    public List<FundValuationResponse> batchGetValuation(List<Long> fundIds) {
        if (fundIds == null || fundIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<FundValuationResponse> responses = new ArrayList<>();
        for (Long fundId : fundIds) {
            try {
                FundValuationResponse response = getValuation(fundId);
                responses.add(response);
            } catch (Exception e) {
                log.error("获取基金估值失败，fundId: {}", fundId, e);
                FundValuationResponse errorResponse = new FundValuationResponse();
                errorResponse.setFundId(fundId);
                errorResponse.setAllSuccess(false);
                errorResponse.setErrorMsg(e.getMessage());
                responses.add(errorResponse);
            }
        }

        return responses;
    }

    @Override
    public List<FundValuationResponse> getAllValuation() {
        List<FundConfig> funds = listFunds();
        if (funds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> fundIds = funds.stream()
                .map(FundConfig::getId)
                .collect(Collectors.toList());

        return batchGetValuation(fundIds);
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
                    // 计算加权涨跌幅
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

        for (FundHolding holding : holdings) {
            String market = holding.getMarket();
            if (market == null || market.isEmpty()) {
                // 从数据库查询市场信息
                market = getMarketByStockCode(holding.getStockCode());
                holding.setMarket(market);
            }

            marketGroups.computeIfAbsent(market.toUpperCase(), k -> new ArrayList<>()).add(holding);
        }

        return marketGroups;
    }

    /**
     * 根据股票代码获取市场
     */
    private String getMarketByStockCode(String stockCode) {
        LambdaQueryWrapper<StockInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StockInfo::getStockCode, stockCode)
                .select(StockInfo::getMarket);
        StockInfo stockInfo = stockInfoMapper.selectOne(wrapper);

        if (stockInfo != null && stockInfo.getMarket() != null) {
            return stockInfo.getMarket();
        }

        // 根据代码规则推断
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

        // 如果股票数量少，直接单线程处理
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

        // 多线程处理
        List<CompletableFuture<Map<String, StockQuote>>> futures = new ArrayList<>();

        for (Map.Entry<String, List<FundHolding>> entry : marketGroups.entrySet()) {
            String market = entry.getKey();
            List<FundHolding> holdings = entry.getValue();

            // 按3个一批分割
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

        // 等待所有任务完成并收集结果
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

        // 构建URL
        String codesParam = String.join(",", codes);
        String url = iTickConfig.getBaseUrl() + "/stock/quotes?region=" + market + "&codes=" + codesParam;

        // 获取Token
        ApiToken token = tokenManagerService.getNextTokenEntity(PROVIDER);
        Map<String, String> headers = new HashMap<>();
        headers.put("accept", "application/json");
        headers.put("Content-Type", "application/json");
        headers.put("token", token.getTokenValue());

        log.info("获取实时报价，market: {}, codes: {}, url: {}", market, codes, url);

        try {
            String response = HttpClientUtil.getWithRetry(url, headers, iTickConfig.getRetry());
            log.info("报价响应: {}", response);

            // 解析响应
            JSONObject jsonResponse = JSON.parseObject(response);
            int code = jsonResponse.getIntValue("code");

            if (code == 0) {
                // data 是对象格式：{"000858":{...},"000568":{...}}
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
                // 标记所有股票失败
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
            // 标记所有股票失败
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

        // 股票代码（symbol格式：00700.HK）
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

        // ld = 最新价
        BigDecimal price = item.getBigDecimal("ld");
        quote.setPrice(price);

        // p = 前日收盘价
        BigDecimal lastClose = item.getBigDecimal("p");
        quote.setLastClose(lastClose);

        // 涨跌额
        BigDecimal change = item.getBigDecimal("ch");
        quote.setChange(change);

        // 涨跌幅百分比
        BigDecimal changePercent = item.getBigDecimal("chp");
        quote.setChangePercent(changePercent);

        quote.setSuccess(true);

        return quote;
    }
}
