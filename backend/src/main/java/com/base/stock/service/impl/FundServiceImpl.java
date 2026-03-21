package com.base.stock.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.base.common.util.SecurityUtils;
import com.base.stock.entity.StockInfo;
import com.base.stock.dto.FundConfigRequest;
import com.base.stock.dto.FundValuationResponse;
import com.base.stock.dto.StockQuote;
import com.base.stock.entity.FundConfig;
import com.base.stock.entity.FundHolding;
import com.base.stock.entity.FundValuationRecord;
import com.base.stock.entity.FundWatchlist;
import com.base.stock.client.QuoteProvider;
import com.base.stock.factory.QuoteProviderFactory;
import com.base.stock.mapper.FundConfigMapper;
import com.base.stock.mapper.FundHoldingMapper;
import com.base.stock.mapper.FundValuationRecordMapper;
import com.base.stock.mapper.FundWatchlistMapper;
import com.base.stock.service.FundService;
import com.base.stock.mapper.StockInfoMapper;
import com.base.system.service.ConfigService;
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
    private final RedisUtil redisUtil;
    private final ConfigService configService;
    private final QuoteProviderFactory quoteProviderFactory;

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

        List<Long> fundIds = funds.stream().map(FundConfig::getId).collect(Collectors.toList());
        boolean afterClose = LocalTime.now().isAfter(MARKET_CLOSE_TIME);

        Map<Long, FundValuationResponse> dbValuationMap = Collections.emptyMap();
        if (afterClose) {
            List<FundValuationRecord> dbRecords = fundValuationRecordMapper.selectByFundIdsAndDate(fundIds, LocalDate.now());
            if (!dbRecords.isEmpty()) {
                dbValuationMap = new HashMap<>();
                for (FundValuationRecord record : dbRecords) {
                    FundValuationResponse response = convertDbRecordToResponse(record);
                    dbValuationMap.put(record.getFundId(), response);
                }
            }
        }

        List<Long> needRefreshFundIds = new ArrayList<>();
        Map<Long, FundValuationResponse> cachedValuationMap = new HashMap<>();

        for (FundConfig fund : funds) {
            if (afterClose && dbValuationMap.containsKey(fund.getId())) {
                FundValuationResponse response = dbValuationMap.get(fund.getId());
                response.setFundName(fund.getFundName());
                response.setFundCode(fund.getFundCode());
                cachedValuationMap.put(fund.getId(), response);
            } else {
                FundValuationResponse cached = getCachedValuation(fund.getId());
                if (cached != null) {
                    cached.setFundName(fund.getFundName());
                    cached.setFundCode(fund.getFundCode());
                    cachedValuationMap.put(fund.getId(), cached);
                } else {
                    needRefreshFundIds.add(fund.getId());
                }
            }
        }

        if (!needRefreshFundIds.isEmpty()) {
            List<FundConfig> needRefreshFunds = funds.stream()
                    .filter(f -> needRefreshFundIds.contains(f.getId()))
                    .collect(Collectors.toList());
            batchRefreshValuations(needRefreshFunds, cachedValuationMap, afterClose);
        }

        List<FundValuationResponse> responses = new ArrayList<>();
        for (FundConfig fund : funds) {
            FundValuationResponse response = cachedValuationMap.get(fund.getId());
            if (response == null) {
                response = new FundValuationResponse();
                response.setFundId(fund.getId());
                response.setFundName(fund.getFundName());
                response.setFundCode(fund.getFundCode());
            }
            responses.add(response);
        }
        return responses;
    }

    /**
     * 批量刷新估值
     */
    private void batchRefreshValuations(List<FundConfig> funds, Map<Long, FundValuationResponse> resultMap, boolean afterClose) {
        if (funds.isEmpty()) {
            return;
        }

        Set<String> allStockCodes = new LinkedHashSet<>();
        for (FundConfig fund : funds) {
            List<FundHolding> holdings = fundHoldingMapper.selectHoldingsWithStockInfo(fund.getId());
            fund.setHoldings(holdings);
            for (FundHolding h : holdings) {
                allStockCodes.add(h.getStockCode());
            }
        }

        List<String> distinctCodes = new ArrayList<>(allStockCodes);
        if (distinctCodes.isEmpty()) {
            return;
        }

        Map<String, List<String>> marketCodeGroups = groupStockCodesByMarket(distinctCodes);
        Map<String, StockQuote> globalQuoteMap = fetchAllQuotesConcurrently(marketCodeGroups);

        for (FundConfig fund : funds) {
            try {
                FundValuationResponse response = buildValuationResponse(fund, globalQuoteMap);
                response.setCacheTime(System.currentTimeMillis());
                String cacheKey = FUND_VALUATION_CACHE_KEY + fund.getId();
                redisUtil.set(cacheKey, response, CACHE_EXPIRE_SECONDS);

                if (afterClose) {
                    saveValuationRecord(fund.getId(), response);
                }
                resultMap.put(fund.getId(), response);
            } catch (Exception e) {
                log.error("基金 {}({}) 估值失败", fund.getFundName(), fund.getId(), e);
            }
        }
    }

    /**
     * 将数据库记录转换为响应对象
     */
    private FundValuationResponse convertDbRecordToResponse(FundValuationRecord record) {
        FundValuationResponse response = new FundValuationResponse();
        response.setFundId(record.getFundId());
        response.setEstimatedChangePercent(record.getEstimatedChangePercent());
        response.setHoldingCount(record.getHoldingCount());
        response.setSuccessCount(record.getSuccessCount());
        response.setFailCount(record.getFailCount());
        response.setTotalWeight(record.getTotalWeight());
        response.setAllSuccess(record.getFailCount() == null || record.getFailCount() == 0);
        response.setCacheTime(record.getUpdateTime() != null
                ? record.getUpdateTime().atZone(java.time.ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli()
                : record.getCreateTime().atZone(java.time.ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli());
        if (record.getQuotesJson() != null) {
            response.setQuotes(JSON.parseArray(record.getQuotesJson(), StockQuote.class));
        }
        return response;
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
                ? record.getUpdateTime().atZone(java.time.ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli()
                : record.getCreateTime().atZone(java.time.ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli());
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
     * 计算基金估值（在线接口用，内部自行拉取报价）
     */
    private FundValuationResponse calculateValuation(FundConfig fund) {
        List<FundHolding> holdings = fund.getHoldings();
        if (holdings == null || holdings.isEmpty()) {
            return buildValuationResponse(fund, Collections.emptyMap());
        }
        Map<String, List<FundHolding>> marketGroups = groupByMarket(holdings);
        Map<String, StockQuote> quoteMap = fetchQuotesConcurrently(marketGroups);
        return buildValuationResponse(fund, quoteMap);
    }

    /**
     * 根据已有的 quoteMap 计算基金估值（批量刷新用，报价已统一拉取）
     */
    private FundValuationResponse buildValuationResponse(FundConfig fund, Map<String, StockQuote> quoteMap) {
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
                // 复制一份，避免多基金共享同一 StockQuote 对象导致 weight 互相覆盖
                StockQuote copy = new StockQuote();
                copy.setStockCode(quote.getStockCode());
                copy.setPrice(quote.getPrice());
                copy.setLastClose(quote.getLastClose());
                copy.setChange(quote.getChange());
                copy.setChangePercent(quote.getChangePercent());
                copy.setSuccess(quote.getSuccess());
                copy.setErrorMsg(quote.getErrorMsg());
                copy.setWeight(holding.getWeight());
                copy.setStockName(holding.getStockName());
                copy.setMarket(holding.getMarket());
                quote = copy;

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

    private static final String CONFIG_KEY_QUOTE_THREAD_COUNT = "fund.quote.thread.count";
    private static final int DEFAULT_QUOTE_THREAD_COUNT = 6;

    private ExecutorService createQuoteExecutor() {
        int threadCount = DEFAULT_QUOTE_THREAD_COUNT;
        try {
            String val = configService.getConfigValueByKey(CONFIG_KEY_QUOTE_THREAD_COUNT);
            if (val != null && !val.isEmpty()) {
                threadCount = Integer.parseInt(val.trim());
                if (threadCount < 1) {
                    threadCount = DEFAULT_QUOTE_THREAD_COUNT;
                }
            }
        } catch (Exception e) {
            log.warn("读取基金报价线程数配置失败，使用默认值: {}", DEFAULT_QUOTE_THREAD_COUNT);
        }
        log.info("创建报价拉取线程池, 线程数: {}", threadCount);
        return Executors.newFixedThreadPool(threadCount, r -> {
            Thread t = new Thread(r, "fund-quote-fetch");
            t.setDaemon(true);
            return t;
        });
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

        ExecutorService executor = createQuoteExecutor();
        try {
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
                    }, executor);
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
        } finally {
            executor.shutdown();
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
        log.info("获取实时报价，market: {}, codes: {}", market, codes);

        try {
            // 使用 QuoteProviderFactory 获取报价
            Map<String, StockQuote> quotes = quoteProviderFactory.getPrimaryProvider().getQuotes(codes);
            
            // 确保所有股票都有返回值
            Map<String, StockQuote> result = new HashMap<>();
            for (String code : codes) {
                StockQuote quote = quotes.get(code);
                if (quote == null) {
                    quote = new StockQuote();
                    quote.setStockCode(code);
                    quote.setMarket(market);
                    quote.setSuccess(false);
                    quote.setErrorMsg("未获取到报价");
                }
                result.put(code, quote);
            }
            
            return result;
        } catch (Exception e) {
            log.error("获取报价异常，market: {}, codes: {}", market, codes, e);
            
            // 尝试使用降级数据源
            if (tryFallback(market, codes)) {
                try {
                    Map<String, StockQuote> fallbackQuotes = quoteProviderFactory.getFallbackProvider().getQuotes(codes);
                    Map<String, StockQuote> result = new HashMap<>();
                    for (String code : codes) {
                        StockQuote quote = fallbackQuotes.get(code);
                        if (quote == null) {
                            quote = new StockQuote();
                            quote.setStockCode(code);
                            quote.setMarket(market);
                            quote.setSuccess(false);
                            quote.setErrorMsg("降级数据源未获取到报价");
                        }
                        result.put(code, quote);
                    }
                    log.info("降级数据源报价获取成功");
                    return result;
                } catch (Exception fallbackEx) {
                    log.error("降级数据源也失败", fallbackEx);
                }
            }
            
            // 返回失败报价
            Map<String, StockQuote> errorQuotes = new HashMap<>();
            for (String code : codes) {
                StockQuote quote = new StockQuote();
                quote.setStockCode(code);
                quote.setMarket(market);
                quote.setSuccess(false);
                quote.setErrorMsg(e.getMessage());
                errorQuotes.put(code, quote);
            }
            return errorQuotes;
        }
    }

    /**
     * 尝试降级数据源
     */
    private boolean tryFallback(String market, List<String> codes) {
        QuoteProvider fallbackProvider = quoteProviderFactory.getFallbackProvider();
        if (fallbackProvider == null) {
            return false;
        }
        
        // 简单测试：获取一只股票的报价
        try {
            if (!codes.isEmpty()) {
                fallbackProvider.getQuote(codes.get(0), market);
                return true;
            }
        } catch (Exception e) {
            log.warn("降级数据源测试失败: {}", e.getMessage());
        }
        return false;
    }

    /**
     * 将去重后的股票代码按市场分组
     */
    private Map<String, List<String>> groupStockCodesByMarket(List<String> stockCodes) {
        List<String> needQueryCodes = new ArrayList<>(stockCodes);
        Map<String, String> marketMap = new HashMap<>();
        if (!needQueryCodes.isEmpty()) {
            LambdaQueryWrapper<StockInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(StockInfo::getStockCode, needQueryCodes)
                    .select(StockInfo::getStockCode, StockInfo::getMarket);
            List<StockInfo> stockInfoList = stockInfoMapper.selectList(wrapper);
            marketMap = stockInfoList.stream()
                    .filter(s -> s.getMarket() != null)
                    .collect(Collectors.toMap(StockInfo::getStockCode, StockInfo::getMarket, (a, b) -> a));
        }

        Map<String, List<String>> result = new HashMap<>();
        for (String code : stockCodes) {
            String market = marketMap.getOrDefault(code, inferMarketByStockCode(code));
            result.computeIfAbsent(market.toUpperCase(), k -> new ArrayList<>()).add(code);
        }
        return result;
    }

    /**
     * 根据按市场分组的股票代码，多线程批量拉取所有报价
     */
    private Map<String, StockQuote> fetchAllQuotesConcurrently(Map<String, List<String>> marketCodeGroups) {
        Map<String, StockQuote> quoteMap = new HashMap<>();

        int totalStocks = marketCodeGroups.values().stream().mapToInt(List::size).sum();
        if (totalStocks <= BATCH_SIZE) {
            for (Map.Entry<String, List<String>> entry : marketCodeGroups.entrySet()) {
                try {
                    Map<String, StockQuote> batchQuotes = fetchBatchQuotes(entry.getKey(), entry.getValue());
                    quoteMap.putAll(batchQuotes);
                } catch (Exception e) {
                    log.error("获取报价失败，market: {}, codes: {}", entry.getKey(), entry.getValue(), e);
                    for (String code : entry.getValue()) {
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

        ExecutorService executor = createQuoteExecutor();
        try {
            List<CompletableFuture<Map<String, StockQuote>>> futures = new ArrayList<>();
            for (Map.Entry<String, List<String>> entry : marketCodeGroups.entrySet()) {
                String market = entry.getKey();
                List<List<String>> batches = splitIntoBatches(entry.getValue(), BATCH_SIZE);
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
                    }, executor);
                    futures.add(future);
                }
            }

            for (CompletableFuture<Map<String, StockQuote>> future : futures) {
                try {
                    quoteMap.putAll(future.get());
                } catch (Exception e) {
                    log.error("获取报价结果失败", e);
                }
            }
        } finally {
            executor.shutdown();
        }
        return quoteMap;
    }

    // ========== 批量刷新（定时任务） ==========

    @Override
    public void refreshAllFundValuation() {
        List<FundConfig> funds = listAllFunds();
        if (funds.isEmpty()) {
            log.info("没有需要刷新估值的基金");
            return;
        }

        // Step 1: 加载每个基金的持仓，并收集所有股票代码去重
        Set<String> allStockCodes = new LinkedHashSet<>();
        int totalHoldingCount = 0;
        for (FundConfig fund : funds) {
            List<FundHolding> holdings = fundHoldingMapper.selectHoldingsWithStockInfo(fund.getId());
            fund.setHoldings(holdings);
            for (FundHolding h : holdings) {
                allStockCodes.add(h.getStockCode());
            }
            totalHoldingCount += holdings.size();
        }

        List<String> distinctCodes = new ArrayList<>(allStockCodes);
        log.info("基金估值批量刷新: 共 {} 个基金, {} 只股票(去重前 {}), 分批拉取报价",
                funds.size(), distinctCodes.size(), totalHoldingCount);

        if (distinctCodes.isEmpty()) {
            log.info("所有基金均无持仓，跳过报价拉取");
            return;
        }

        // Step 2: 去重股票按市场分组，多线程批量拉取报价
        Map<String, List<String>> marketCodeGroups = groupStockCodesByMarket(distinctCodes);
        int batchCount = marketCodeGroups.values().stream()
                .mapToInt(codes -> (codes.size() + BATCH_SIZE - 1) / BATCH_SIZE)
                .sum();
        log.info("按市场分组: {}, 共 {} 批次", marketCodeGroups.keySet(), batchCount);

        Map<String, StockQuote> globalQuoteMap = fetchAllQuotesConcurrently(marketCodeGroups);
        log.info("报价拉取完成, 获取 {} 只股票报价", globalQuoteMap.size());

        // Step 3: 逐基金计算估值，缓存/持久化
        boolean afterClose = LocalTime.now().isAfter(MARKET_CLOSE_TIME);
        int successFundCount = 0;
        int failFundCount = 0;

        for (FundConfig fund : funds) {
            try {
                FundValuationResponse response = buildValuationResponse(fund, globalQuoteMap);
                response.setCacheTime(System.currentTimeMillis());
                String cacheKey = FUND_VALUATION_CACHE_KEY + fund.getId();
                redisUtil.set(cacheKey, response, CACHE_EXPIRE_SECONDS);

                if (afterClose) {
                    saveValuationRecord(fund.getId(), response);
                }
                successFundCount++;
                log.info("基金 {}({}) 估值完成, 涨跌幅: {}%",
                        fund.getFundName(), fund.getId(), response.getEstimatedChangePercent());
            } catch (Exception e) {
                failFundCount++;
                log.error("基金 {}({}) 估值失败", fund.getFundName(), fund.getId(), e);
            }
        }
        log.info("基金估值批量刷新完成: 成功 {}, 失败 {}", successFundCount, failFundCount);
    }
}
