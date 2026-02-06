package com.base.stock.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.base.stock.client.ITickApiClient;
import com.base.stock.entity.StockInfo;
import com.base.stock.entity.StockKline;
import com.base.stock.factory.DataFactory;
import com.base.stock.mapper.StockInfoMapper;
import com.base.stock.mapper.StockKlineMapper;
import com.base.stock.service.StockSyncService;
import com.base.stock.service.WatchlistService;
import com.base.system.service.ConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        int count = 0;
        for (StockInfo stock : stockList) {
            stock.setMarket(market);
            stock.setStatus(1);

            // 检查是否已存在
            LambdaQueryWrapper<StockInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(StockInfo::getStockCode, stock.getStockCode());
            StockInfo existing = stockInfoMapper.selectOne(wrapper);

            if (existing != null) {
                // 更新
                stock.setId(existing.getId());
                stockInfoMapper.updateById(stock);
            } else {
                // 新增
                stockInfoMapper.insert(stock);
            }
            count++;
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

        int count = 0;
        for (StockKline kline : klineList) {
            kline.setStockCode(stockCode);

            // 检查是否已存在
            LambdaQueryWrapper<StockKline> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(StockKline::getStockCode, kline.getStockCode())
                    .eq(StockKline::getTradeDate, kline.getTradeDate());
            StockKline existing = stockKlineMapper.selectOne(wrapper);

            if (existing != null) {
                // 更新
                kline.setId(existing.getId());
                stockKlineMapper.updateById(kline);
            } else {
                // 新增
                stockKlineMapper.insert(kline);
            }
            count++;
        }

        log.info("同步K线数据完成，stockCode: {}, count: {}", stockCode, count);
        return count;
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
     * 保存K线数据到数据库
     */
    private int saveKlineData(String stockCode, List<StockKline> klineList) {
        int count = 0;
        for (StockKline kline : klineList) {
            // 检查是否已存在
            LambdaQueryWrapper<StockKline> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(StockKline::getStockCode, kline.getStockCode())
                    .eq(StockKline::getTradeDate, kline.getTradeDate());
            StockKline existing = stockKlineMapper.selectOne(wrapper);

            if (existing != null) {
                // 更新
                kline.setId(existing.getId());
                stockKlineMapper.updateById(kline);
            } else {
                // 新增
                stockKlineMapper.insert(kline);
            }
            count++;
        }
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
}
