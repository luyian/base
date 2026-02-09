package com.base.stock.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.base.stock.client.ITickApiClient;
import com.base.stock.client.impl.ITickApiClientImpl;
import com.base.stock.config.StockSyncConfig;
import com.base.stock.entity.StockInfo;
import com.base.stock.entity.StockKline;
import com.base.stock.factory.DataFactory;
import com.base.stock.http.ConcurrentHttpExecutor;
import com.base.stock.http.ConcurrentHttpRequest;
import com.base.stock.http.ConcurrentHttpResponse;
import com.base.stock.mapper.StockInfoMapper;
import com.base.stock.mapper.StockKlineMapper;
import com.base.stock.service.StockSyncService;
import com.base.stock.service.SyncFailureService;
import com.base.stock.service.WatchlistService;
import com.base.system.service.ConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 股票同步服务实现类
 *
 * @author base
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StockSyncServiceImpl implements StockSyncService {

    private final ITickApiClient iTickApiClient;
    private final DataFactory dataFactory;
    private final StockInfoMapper stockInfoMapper;
    private final StockKlineMapper stockKlineMapper;
    private final WatchlistService watchlistService;
    private final ConfigService configService;
    private final SyncFailureService syncFailureService;
    private final StockSyncConfig stockSyncConfig;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int syncStockList(String market) {
        log.info("开始同步股票列表，market: {}", market);

        // 拉取数据
        String json = iTickApiClient.fetchStockList(market);

        // 检查 API 返回是否有错误
        if (json != null && json.contains("\"code\":1")) {
            log.error("iTick API 返回错误: {}", json);
            throw new RuntimeException("iTick API 调用失败: " + json);
        }

        // 转换数据
        List<StockInfo> stockList = dataFactory.transform(json, "itick_stock_list", StockInfo.class);

        if (stockList.isEmpty()) {
            log.warn("未获取到股票数据，market: {}", market);
            return 0;
        }

        // 设置市场和状态
        for (StockInfo stock : stockList) {
            stock.setMarket(market);
            stock.setStatus(1);
        }

        // 批量插入或更新（使用 upsert）
        int batchSize = 500;
        int count = 0;
        List<List<StockInfo>> batches = partitionList(stockList, batchSize);
        for (List<StockInfo> batch : batches) {
            stockInfoMapper.batchUpsert(batch);
            count += batch.size();
        }

        log.info("同步股票列表完成，market: {}, count: {}", market, count);
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int syncKlineData(String stockCode, LocalDate startDate, LocalDate endDate) {
        log.info("开始同步K线数据，stockCode: {}, startDate: {}, endDate: {}", stockCode, startDate, endDate);

        // 如果没有指定开始日期，查询本地最新日期
        if (startDate == null) {
            LocalDate latestDate = stockKlineMapper.selectLatestTradeDate(stockCode);
            if (latestDate != null) {
                startDate = latestDate.plusDays(1);
            } else {
                // 默认拉取近1个月数据
                startDate = LocalDate.now().minusMonths(1);
            }
        }

        if (endDate == null) {
            endDate = LocalDate.now();
        }

        // 如果开始日期大于结束日期，无需同步
        if (startDate.isAfter(endDate)) {
            log.info("无需同步，startDate > endDate");
            return 0;
        }

        // 拉取数据
        String json = iTickApiClient.fetchKlineData(stockCode, "day", startDate, endDate);

        // 转换数据
        List<StockKline> klineList = dataFactory.transform(json, "itick_kline_daily", StockKline.class);

        if (klineList.isEmpty()) {
            log.warn("未获取到K线数据，stockCode: {}", stockCode);
            return 0;
        }

        // 设置股票代码
        for (StockKline kline : klineList) {
            kline.setStockCode(stockCode);
        }

        // 批量插入或更新
        return batchSaveKlineData(klineList);
    }

    @Override
    public int batchSyncKlineData(Long userId, LocalDate startDate, LocalDate endDate) {
        log.info("开始批量同步K线数据，userId: {}", userId);

        // 获取用户自选股票列表
        List<String> stockCodes = watchlistService.listStockCodesByUserId(userId);

        if (stockCodes.isEmpty()) {
            log.warn("用户没有自选股票，userId: {}", userId);
            return 0;
        }

        int totalCount = 0;
        for (String stockCode : stockCodes) {
            try {
                int count = syncKlineData(stockCode, startDate, endDate);
                totalCount += count;
            } catch (Exception e) {
                log.error("同步K线数据失败，stockCode: {}", stockCode, e);
            }
        }

        log.info("批量同步K线数据完成，userId: {}, totalCount: {}", userId, totalCount);
        return totalCount;
    }

    @Override
    public int batchSyncAllKlineData(String market, LocalDate startDate, LocalDate endDate) {
        log.info("开始批量同步所有股票K线数据，market: {}, startDate: {}, endDate: {}",
                 market, startDate, endDate);

        // 1. 查询股票列表
        LambdaQueryWrapper<StockInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StockInfo::getStatus, 1)
                .eq(StockInfo::getDeleted, 0);
        if (market != null && !market.isEmpty()) {
            wrapper.eq(StockInfo::getMarket, market);
        }

        List<StockInfo> stockList = stockInfoMapper.selectList(wrapper);

        if (stockList.isEmpty()) {
            log.warn("没有找到股票数据，market: {}", market);
            return 0;
        }

        log.info("共找到 {} 只股票需要同步", stockList.size());

        // 2. 从配置中获取阈值和批量大小
        int batchThreshold = getBatchThreshold();
        int batchSize = getBatchSize();

        log.info("批量同步配置 - 阈值: {}, 批量大小: {}", batchThreshold, batchSize);

        // 3. 判断是否使用批量接口
        boolean useBatchApi = stockList.size() > batchThreshold;

        if (!useBatchApi) {
            // 使用单个接口逐个同步（保持原有逻辑）
            log.info("股票数量({})未超过阈值({})，使用单个接口同步", stockList.size(), batchThreshold);
            return syncOneByOne(stockList, startDate, endDate);
        }

        // 4. 使用批量接口：按市场分组
        log.info("股票数量({})超过阈值({})，使用批量接口同步", stockList.size(), batchThreshold);
        Map<String, List<StockInfo>> marketGroups = stockList.stream()
                .collect(Collectors.groupingBy(StockInfo::getMarket));

        log.info("按市场分组完成，共 {} 个市场", marketGroups.size());

        int totalCount = 0;
        int successCount = 0;
        int failCount = 0;

        // 5. 遍历每个市场
        for (Map.Entry<String, List<StockInfo>> entry : marketGroups.entrySet()) {
            String region = entry.getKey();
            List<StockInfo> stocks = entry.getValue();

            log.info("开始同步市场 {} 的股票，数量: {}", region, stocks.size());

            // 6. 分批处理（使用配置的批量大小）
            List<List<StockInfo>> batches = partitionList(stocks, batchSize);

            for (int i = 0; i < batches.size(); i++) {
                List<StockInfo> batch = batches.get(i);

                try {
                    // 提取纯股票代码
                    List<String> codes = batch.stream()
                       .map(StockInfo::getStockCode)
                            .collect(Collectors.toList());

                    log.info("同步市场 {} 第 {}/{} 批，股票数量: {}",
                             region, i + 1, batches.size(), codes.size());

                    // 7. 调用批量接口
                    String json = iTickApiClient.fetchBatchKlineData(
                        region, codes, "day", startDate, endDate
                    );

                    // 8. 解析数据
                    Map<String, List<StockKline>> klineMap = parseBatchKlineJson(json);

                    // 9. 保存数据
                    for (Map.Entry<String, List<StockKline>> klineEntry : klineMap.entrySet()) {
                        String stockCode = klineEntry.getKey();
                        List<StockKline> klineList = klineEntry.getValue();

                        try {
                            int count = saveKlineData(stockCode, klineList);
                            totalCount += count;
                            successCount++;
                            log.debug("保存成功，stockCode: {}, count: {}", stockCode, count);
                        } catch (Exception e) {
                            failCount++;
                            log.error("保存K线数据失败，stockCode: {}", stockCode, e);
                        }
                    }

                } catch (Exception e) {
                    failCount += batch.size();
                    log.error("批量同步失败，market: {}, batch: {}/{}", region, i + 1, batches.size(), e);

                    // Token错误则停止同步
                    if (e.getMessage() != null &&
                        (e.getMessage().contains("没有可用的 Token") ||
                         e.getMessage().contains("Token 认证失败"))) {
                        log.error("Token不可用，停止同步");
                        break;
                    }
                }
            }
        }

        log.info("批量同步所有股票K线数据完成，成功: {}, 失败: {}, 总记录数: {}",
                 successCount, failCount, totalCount);
        return totalCount;
    }

    /**
     * 解析批量K线接口返回的JSON数据
     *
     * @param json 批量接口返回的JSON
     * @return Map<股票代码, K线列表>
     */
    private Map<String, List<StockKline>> parseBatchKlineJson(String json) {
        Map<String, List<StockKline>> result = new HashMap<>();

        try {
            JSONObject responseObj = JSON.parseObject(json);

            // 检查返回码
            Integer code = responseObj.getInteger("code");
            if (code == null || code != 0) {
                log.error("批量K线接口返回错误: {}", json);
                throw new RuntimeException("批量K线接口调用失败: " + json);
            }

            // 获取data对象（Map结构）
            JSONObject dataObj = responseObj.getJSONObject("data");
            if (dataObj == null || dataObj.isEmpty()) {
                log.warn("批量K线接口返回空数据");
                return result;
            }

            // 遍历每只股票的K线数据
            for (String stockCode : dataObj.keySet()) {
                JSONArray klineArray = dataObj.getJSONArray(stockCode);
                if (klineArray == null || klineArray.isEmpty()) {
                    continue;
                }

                // 将JSONArray包装成标准格式，复用现有映射规则
                Map<String, Object> wrappedMap = new HashMap<>();
                wrappedMap.put("code", 0);
                wrappedMap.put("data", klineArray);
                String wrappedJson = JSON.toJSONString(wrappedMap);

                // 使用现有的 itick_kline_daily 映射规则转换
                List<StockKline> klineList = dataFactory.transform(
                    wrappedJson, "itick_kline_daily", StockKline.class
                );

                // 设置股票代码
                for (StockKline kline : klineList) {
                    kline.setStockCode(stockCode);
                }

                result.put(stockCode, klineList);
            }

        } catch (Exception e) {
            log.error("解析批量K线数据失败", e);
            throw new RuntimeException("解析批量K线数据失败: " + e.getMessage(), e);
        }

        return result;
    }

    /**
     * 使用单个接口逐个同步（原有逻辑）
     */
    private int syncOneByOne(List<StockInfo> stockList, LocalDate startDate, LocalDate endDate) {
        int totalCount = 0;
        int successCount = 0;
        int failCount = 0;

        for (StockInfo stock : stockList) {
            try {
                int count = syncKlineData(stock.getStockCode(), startDate, endDate);
                totalCount += count;
                successCount++;
                log.debug("同步成功，stockCode: {}, count: {}", stock.getStockCode(), count);
            } catch (Exception e) {
                failCount++;
                log.error("同步K线数据失败，stockCode: {}", stock.getStockCode(), e);

                if (e.getMessage() != null && e.getMessage().contains("没有可用的 Token")) {
                    log.error("没有可用的 Token，停止同步");
                    break;
                }
            }
        }

        log.info("单个接口同步完成，成功: {}, 失败: {}, 总记录数: {}",
                 successCount, failCount, totalCount);
        return totalCount;
    }

    /**
     * 保存K线数据到数据库（批量操作）
     */
    private int saveKlineData(String stockCode, List<StockKline> klineList) {
        if (klineList == null || klineList.isEmpty()) {
            return 0;
        }
        // 设置股票代码
        for (StockKline kline : klineList) {
            kline.setStockCode(stockCode);
        }
        return batchSaveKlineData(klineList);
    }

    /**
     * 批量保存K线数据（使用 upsert）
     *
     * @param klineList K线数据列表
     * @return 保存记录数
     */
    private int batchSaveKlineData(List<StockKline> klineList) {
        if (klineList == null || klineList.isEmpty()) {
            return 0;
        }

        // 过滤掉 tradeDate 为 null 的无效数据
        List<StockKline> validList = klineList.stream()
                .filter(kline -> kline.getTradeDate() != null)
                .collect(Collectors.toList());

        if (validList.isEmpty()) {
            log.warn("过滤后无有效K线数据，原始数量: {}", klineList.size());
            return 0;
        }

        int filteredCount = klineList.size() - validList.size();
        if (filteredCount > 0) {
            log.warn("过滤掉 {} 条 tradeDate 为空的无效数据", filteredCount);
        }

        // 分批处理，每批500条
        int batchSize = 500;
        int count = 0;
        List<List<StockKline>> batches = partitionList(validList, batchSize);
        for (List<StockKline> batch : batches) {
            stockKlineMapper.batchUpsert(batch);
            count += batch.size();
        }

        log.debug("批量保存K线数据完成，count: {}", count);
        return count;
    }

    /**
     * 将列表分批
     */
    private <T> List<List<T>> partitionList(List<T> list, int batchSize) {
        List<List<T>> batches = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            int end = Math.min(i + batchSize, list.size());
            batches.add(list.subList(i, end));
        }
        return batches;
    }

    /**
     * 获取批量同步阈值配置
     */
    private int getBatchThreshold() {
        try {
            String value = configService.getConfigValueByKey("stock.sync.batch.threshold");
            return Integer.parseInt(value);
        } catch (Exception e) {
            log.warn("获取批量同步阈值配置失败，使用默认值100", e);
            return 100;
        }
    }

    /**
     * 获取批量请求大小配置
     */
    private int getBatchSize() {
        try {
            String value = configService.getConfigValueByKey("stock.sync.batch.size");
            return Integer.parseInt(value);
        } catch (Exception e) {
            log.warn("获取批量请求大小配置失败，使用默认值100", e);
            return 100;
        }
    }

    // ==================== 并发执行器相关方法 ====================

    /**
     * 使用并发执行器批量同步K线数据
     * 通过多线程并发发送HTTP请求，提升同步效率
     *
     * @param market    市场（可选）
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 同步记录数
     */
    public int batchSyncAllKlineDataConcurrent(String market, LocalDate startDate, LocalDate endDate) {
        log.info("开始并发批量同步K线数据，market: {}, startDate: {}, endDate: {}",
                market, startDate, endDate);

        // 1. 查询股票列表
        LambdaQueryWrapper<StockInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StockInfo::getStatus, 1)
                .eq(StockInfo::getDeleted, 0);
        if (market != null && !market.isEmpty()) {
            wrapper.eq(StockInfo::getMarket, market);
        }

        List<StockInfo> stockList = stockInfoMapper.selectList(wrapper);

        if (stockList.isEmpty()) {
            log.warn("没有找到股票数据，market: {}", market);
            return 0;
        }

        log.info("共找到 {} 只股票需要同步", stockList.size());

        // 2. 获取并发执行器
        ITickApiClientImpl apiClient = (ITickApiClientImpl) iTickApiClient;
        ConcurrentHttpExecutor executor = apiClient.getConcurrentExecutor();

        log.info("并发执行器Token池大小: {}", executor.getTokenPoolSize());

        // 3. 构建请求列表
        List<ConcurrentHttpRequest> requests = new ArrayList<>();
        for (StockInfo stock : stockList) {
            String url = buildKlineUrl(stock.getStockCode(), stock.getMarket(), "day", startDate, endDate);
            ConcurrentHttpRequest request = ConcurrentHttpRequest.get(url, stock.getStockCode());
            requests.add(request);
        }

        // 4. 提交所有请求（异步执行）
        List<Future<ConcurrentHttpResponse>> futures = executor.executeBatch(requests);

        // 5. 逐个处理响应，边获取边保存
        int successCount = 0;
        int failCount = 0;
        int totalCount = 0;
        int totalStocks = futures.size();

        log.info("========== 开始处理响应，共 {} 只股票 ==========", totalStocks);

        for (int i = 0; i < futures.size(); i++) {
            Future<ConcurrentHttpResponse> future = futures.get(i);
            StockInfo stock = stockList.get(i);
            int currentIndex = i + 1;
            int progress = (currentIndex * 100) / totalStocks;

            try {
                // 等待结果，设置超时时间
                ConcurrentHttpResponse response = future.get(120, TimeUnit.SECONDS);

                if (response.isSuccess()) {
                    // 解析数据
                    List<StockKline> klineList = dataFactory.transform(
                            response.getBody(), "itick_kline_daily", StockKline.class);

                    for (StockKline kline : klineList) {
                        kline.setStockCode(stock.getStockCode());
                    }

                    // 立即保存当前股票的K线数据
                    int savedCount = batchSaveKlineData(klineList);
                    totalCount += savedCount;
                    successCount++;

                    // 每只股票都输出进度
                    log.info("[{}/{}] {}% | {} | 成功 | K线: {}条 | 累计: {}条",
                            currentIndex, totalStocks, progress, stock.getStockCode(), savedCount, totalCount);
                } else {
                    failCount++;
                    log.info("[{}/{}] {}% | {} | 失败 | {}",
                            currentIndex, totalStocks, progress, stock.getStockCode(), response.getErrorMessage());

                    // 记录失败
                    syncFailureService.recordFailure(
                            stock.getStockCode(), startDate, endDate, response.getErrorMessage());
                }

            } catch (Exception e) {
                failCount++;
                log.info("[{}/{}] {}% | {} | 异常 | {}",
                        currentIndex, totalStocks, progress, stock.getStockCode(), e.getMessage());

                // 记录失败
                syncFailureService.recordFailure(
                        stock.getStockCode(), startDate, endDate, e.getMessage());
            }
        }

        log.info("========== 同步完成 ==========");
        log.info("并发批量同步完成，成功股票: {}, 失败股票: {}, 总K线记录数: {}",
                successCount, failCount, totalCount);
        return totalCount;
    }

    /**
     * 构建K线请求URL
     *
     * @param stockCode 股票代码
     * @param region    市场
     * @param period    周期
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return URL
     */
    private String buildKlineUrl(String stockCode, String region, String period,
                                  LocalDate startDate, LocalDate endDate) {
        int kType = 8;
        if ("week".equalsIgnoreCase(period)) {
            kType = 9;
        } else if ("month".equalsIgnoreCase(period)) {
            kType = 10;
        }

        int limit = 100;
        if (startDate != null && endDate != null) {
            long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
            limit = (int) Math.min(days, 100);
        }

        Long et = null;
        if (endDate != null) {
            et = endDate.atTime(23, 59, 59)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli();
        }

        // 从配置获取baseUrl，这里硬编码为itick的地址
        String baseUrl = "https://api.itick.org";

        StringBuilder urlBuilder = new StringBuilder(baseUrl);
        urlBuilder.append("/stock/kline");
        urlBuilder.append("?region=").append(region);
        urlBuilder.append("&code=").append(stockCode);
        urlBuilder.append("&kType=").append(kType);
        urlBuilder.append("&limit=").append(limit);
        if (et != null) {
            urlBuilder.append("&et=").append(et);
        }

        return urlBuilder.toString();
    }

    /**
     * 补拉失败的同步记录
     *
     * @param stockCode     股票代码（可选，为空则补拉所有）
     * @param maxRetryCount 最大重试次数
     * @return 成功补拉的记录数
     */
    public int retryFailedSync(String stockCode, int maxRetryCount) {
        log.info("开始补拉失败数据，stockCode: {}, maxRetryCount: {}", stockCode, maxRetryCount);

        // 1. 查询待重试的失败记录
        List<com.base.stock.entity.SyncFailure> failures =
                syncFailureService.listPendingRetry(stockCode, maxRetryCount);

        if (failures.isEmpty()) {
            log.info("没有需要补拉的失败记录");
            return 0;
        }

        log.info("找到 {} 条待补拉记录", failures.size());

        // 2. 获取并发执行器
        ITickApiClientImpl apiClient = (ITickApiClientImpl) iTickApiClient;
        ConcurrentHttpExecutor executor = apiClient.getConcurrentExecutor();

        // 3. 构建请求
        List<ConcurrentHttpRequest> requests = new ArrayList<>();
        for (com.base.stock.entity.SyncFailure failure : failures) {
            // 查询股票信息获取市场
            LambdaQueryWrapper<StockInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(StockInfo::getStockCode, failure.getStockCode());
            StockInfo stockInfo = stockInfoMapper.selectOne(wrapper);

            if (stockInfo == null) {
                log.warn("股票不存在，跳过补拉，stockCode: {}", failure.getStockCode());
                continue;
            }

            String url = buildKlineUrl(failure.getStockCode(), stockInfo.getMarket(),
                    "day", failure.getStartDate(), failure.getEndDate());
            ConcurrentHttpRequest request = ConcurrentHttpRequest.get(url, failure.getStockCode());
            requests.add(request);
        }

        // 4. 执行请求
        List<Future<ConcurrentHttpResponse>> futures = executor.executeBatch(requests);

        // 5. 处理结果
        int successCount = 0;
        int failCount = 0;
        int totalRecords = futures.size();

        log.info("========== 开始补拉处理，共 {} 条记录 ==========", totalRecords);

        for (int i = 0; i < futures.size(); i++) {
            Future<ConcurrentHttpResponse> future = futures.get(i);
            com.base.stock.entity.SyncFailure failure = failures.get(i);
            int currentIndex = i + 1;

            try {
                ConcurrentHttpResponse response = future.get(120, TimeUnit.SECONDS);

                if (response.isSuccess()) {
                    // 解析并保存数据
                    List<StockKline> klineList = dataFactory.transform(
                            response.getBody(), "itick_kline_daily", StockKline.class);

                    for (StockKline kline : klineList) {
                        kline.setStockCode(failure.getStockCode());
                    }

                    saveKlineData(failure.getStockCode(), klineList);
                    successCount++;

                    // 标记为成功
                    syncFailureService.markSuccess(failure.getId());

                    // 进度提示
                    int progress = (currentIndex * 100) / totalRecords;
                    log.info("[补拉进度 {}/{}] {}% | 成功: {} | 失败: {} | 当前: {} - {}条K线",
                            currentIndex, totalRecords, progress, successCount, failCount,
                            failure.getStockCode(), klineList.size());

                } else {
                    failCount++;
                    int newRetryCount = failure.getRetryCount() + 1;

                    // 进度提示
                    int progress = (currentIndex * 100) / totalRecords;
                    log.warn("[补拉进度 {}/{}] {}% | 失败 | stockCode: {} | error: {}",
                            currentIndex, totalRecords, progress, failure.getStockCode(), response.getErrorMessage());

                    if (newRetryCount >= maxRetryCount) {
                        // 达到最大重试次数，放弃
                        syncFailureService.markAbandoned(failure.getId());
                    } else {
                // 更新重试次数
                        syncFailureService.updateStatus(failure.getId(), 0, newRetryCount);
                    }
                }

            } catch (Exception e) {
                failCount++;

                // 进度提示
                int progress = (currentIndex * 100) / totalRecords;
                log.error("[补拉进度 {}/{}] {}% | 异常 | stockCode: {} | error: {}",
                        currentIndex, totalRecords, progress, failure.getStockCode(), e.getMessage());

                int newRetryCount = failure.getRetryCount() + 1;
                if (newRetryCount >= maxRetryCount) {
                    syncFailureService.markAbandoned(failure.getId());
                } else {
                    syncFailureService.updateStatus(failure.getId(), 0, newRetryCount);
                }
            }
        }

        log.info("========== 补拉处理完成 ==========");
        log.info("补拉完成，成功: {}, 失败: {}", successCount, failCount);
        return successCount;
    }
}
