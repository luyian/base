package com.base.stock.client.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.base.common.util.HttpClientUtil;
import com.base.stock.client.QuoteProvider;
import com.base.stock.dto.StockQuote;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 东方财富报价提供者
 * 使用东方财富免费接口获取实时股票报价
 *
 * @author base
 */
@Slf4j
@Component
public class EastMoneyQuoteProvider implements QuoteProvider {

    private static final String BASE_URL = "https://push2.eastmoney.com/api/qt/ulist.np/get";

    @Override
    public String getName() {
        return "eastmoney";
    }

    @Override
    public Map<String, StockQuote> getQuotes(List<String> codes) {
        Map<String, StockQuote> result = new HashMap<>();

        if (codes == null || codes.isEmpty()) {
            return result;
        }

        // 按市场分组
        Map<String, List<String>> marketGroups = groupByMarket(codes);

        for (Map.Entry<String, List<String>> entry : marketGroups.entrySet()) {
            String market = entry.getKey();
            List<String> marketCodes = entry.getValue();

            // 分批处理，每批最多50只
            for (int i = 0; i < marketCodes.size(); i += 50) {
                List<String> batch = marketCodes.subList(i, Math.min(i + 50, marketCodes.size()));
                try {
                    Map<String, StockQuote> batchResult = fetchBatchQuotes(market, batch);
                    result.putAll(batchResult);
                } catch (Exception e) {
                    log.error("东方财富批量获取报价失败，market: {}, codes: {}", market, batch, e);
                    // 为失败的股票创建空报价
                    for (String code : batch) {
                        StockQuote quote = new StockQuote();
                        quote.setStockCode(code);
                        quote.setMarket(market);
                        quote.setSuccess(false);
                        quote.setErrorMsg(e.getMessage());
                        result.put(code, quote);
                    }
                }
            }
        }

        return result;
    }

    /**
     * 按市场分组股票代码
     */
    private Map<String, List<String>> groupByMarket(List<String> codes) {
        Map<String, List<String>> groups = new HashMap<>();
        for (String code : codes) {
            String market = inferMarket(code);
            groups.computeIfAbsent(market, k -> new java.util.ArrayList<>()).add(code);
        }
        return groups;
    }

    /**
     * 根据股票代码推断市场
     */
    private String inferMarket(String code) {
        if (code == null || code.isEmpty()) {
            return "SZ";
        }
        if (code.startsWith("60") || code.startsWith("68")) {
            return "SH";
        }
        if (code.startsWith("00") || code.startsWith("30")) {
            return "SZ";
        }
        return "SZ";
    }

    /**
     * 批量获取报价
     */
    private Map<String, StockQuote> fetchBatchQuotes(String market, List<String> codes) {
        Map<String, StockQuote> result = new HashMap<>();

        // 构建 secids 参数：市场代码.股票代码
        // 1=上海，0=深圳
        String secidPrefix = "SH".equalsIgnoreCase(market) ? "1." : "0.";
        String secids = codes.stream()
                .map(code -> secidPrefix + code)
                .reduce((a, b) -> a + "," + b)
                .orElse("");

        String url = BASE_URL + "?fltt=2&fields=f1,f2,f3,f4,f5,f6,f7,f12,f13,f14&secids=" + secids;

        log.debug("东方财富报价接口: {}", url);

        // 超时时间增加到 10 秒
        int timeout = 10000;
        int maxRetries = 3;
        String lastError = null;

        for (int retry = 0; retry < maxRetries; retry++) {
            try {
                String response = HttpClientUtil.get(url, null, timeout);
                log.debug("东方财富报价响应: {}", response);

                JSONObject jsonResponse = JSON.parseObject(response);
                int dataCode = jsonResponse.getIntValue("dataCode");

                if (dataCode == 0) {
                    // 正确解析 data.diff 数组
                    JSONObject dataObj = jsonResponse.getJSONObject("data");
                    if (dataObj != null) {
                        JSONArray diff = dataObj.getJSONArray("diff");
                        if (diff != null) {
                            for (int i = 0; i < diff.size(); i++) {
                                JSONObject item = diff.getJSONObject(i);
                                StockQuote quote = parseQuote(item);
                                if (quote != null && quote.getStockCode() != null) {
                                    result.put(quote.getStockCode(), quote);
                                }
                            }
                        }
                    }
                    // 成功获取，直接返回
                    break;
                } else {
                    log.error("东方财富接口返回错误: {}", response);
                    // 为所有股票创建失败报价
                    for (String code : codes) {
                        StockQuote quote = new StockQuote();
                        quote.setStockCode(code);
                        quote.setMarket(market);
                        quote.setSuccess(false);
                        quote.setErrorMsg("API返回错误码: " + dataCode);
                        result.put(code, quote);
                    }
                }
            } catch (Exception e) {
                lastError = e.getMessage();
                log.warn("东方财富接口调用异常，第 {} 次重试: {}", retry + 1, e.getMessage());
                if (retry < maxRetries - 1) {
                    try {
                        // 重试间隔
                        Thread.sleep(1000 * (retry + 1));
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }

        // 如果重试都失败了，抛出异常
        if (result.isEmpty() && !codes.isEmpty()) {
            log.error("东方财富接口重试 {} 次后仍失败，market: {}, codes: {}", maxRetries, market, codes);
            throw new RuntimeException("东方财富接口调用异常: " + lastError);
        }

        // 确保所有请求的股票都有返回值
        for (String code : codes) {
            if (!result.containsKey(code)) {
                StockQuote quote = new StockQuote();
                quote.setStockCode(code);
                quote.setMarket(market);
                quote.setSuccess(false);
                quote.setErrorMsg("未获取到报价");
                result.put(code, quote);
            }
        }

        return result;
    }

    /**
     * 解析报价数据
     */
    private StockQuote parseQuote(JSONObject item) {
        if (item == null) {
            return null;
        }

        StockQuote quote = new StockQuote();

        // 股票代码
        String stockCode = item.getString("f12");
        if (stockCode == null || stockCode.isEmpty()) {
            return null;
        }
        quote.setStockCode(stockCode);

        // 股票名称
        quote.setStockName(item.getString("f14"));

        // 市场代码：1=上海，0=深圳
        String marketCode = item.getString("f13");
        if ("1".equals(marketCode)) {
            quote.setMarket("SH");
        } else if ("0".equals(marketCode)) {
            quote.setMarket("SZ");
        }

        // f2: 最新价（-表示停牌或无成交）
        // f3: 涨跌幅（%）
        // f4: 涨跌额
        // f5: 成交量（手）
        // f6: 成交额（元）
        // f7: 振幅（%）
        String priceStr = item.getString("f2");
        if (priceStr != null && !"-".equals(priceStr) && !priceStr.isEmpty()) {
            quote.setPrice(new BigDecimal(priceStr));
        }

        String changePctStr = item.getString("f3");
        if (changePctStr != null && !"-".equals(changePctStr) && !changePctStr.isEmpty()) {
            quote.setChangePercent(new BigDecimal(changePctStr));
        }

        String changeStr = item.getString("f4");
        if (changeStr != null && !"-".equals(changeStr) && !changeStr.isEmpty()) {
            quote.setChange(new BigDecimal(changeStr));
        }

        quote.setSuccess(true);
        return quote;
    }
}