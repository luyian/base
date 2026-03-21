package com.base.stock.client.impl;

import com.base.common.util.HttpClientUtil;
import com.base.stock.client.QuoteProvider;
import com.base.stock.dto.StockQuote;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 腾讯财经报价提供者
 * 使用腾讯财经免费接口获取实时股票报价
 * 接口: https://qt.gtimg.cn/q=sh600000
 *
 * @author base
 */
@Slf4j
@Component
public class TencentQuoteProvider implements QuoteProvider {

    private static final String BASE_URL = "https://qt.gtimg.cn/q=";

    @Override
    public String getName() {
        return "tencent";
    }

    @Override
    public Map<String, StockQuote> getQuotes(List<String> codes) {
        Map<String, StockQuote> result = new ConcurrentHashMap<>();

        if (codes == null || codes.isEmpty()) {
            return result;
        }

        // 按市场分组
        Map<String, List<String>> marketGroups = groupByMarket(codes);

        for (Map.Entry<String, List<String>> entry : marketGroups.entrySet()) {
            String market = entry.getKey();
            List<String> marketCodes = entry.getValue();

            // 腾讯每次最多获取60只，分批处理
            for (int i = 0; i < marketCodes.size(); i += 60) {
                List<String> batch = marketCodes.subList(i, Math.min(i + 60, marketCodes.size()));
                try {
                    Map<String, StockQuote> batchResult = fetchBatchQuotes(market, batch);
                    result.putAll(batchResult);
                } catch (Exception e) {
                    log.error("腾讯财经批量获取报价失败，market: {}, codes: {}", market, batch, e);
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
            groups.computeIfAbsent(market, k -> new ArrayList<>()).add(code);
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
        if (code.startsWith("HK") || code.startsWith("9")) {
            return "HK";
        }
        return "SZ";
    }

    /**
     * 批量获取报价
     * 腾讯API格式: https://qt.gtimg.cn/q=sh600000,sz000001
     */
    private Map<String, StockQuote> fetchBatchQuotes(String market, List<String> codes) {
        Map<String, StockQuote> result = new HashMap<>();

        // 构建腾讯API的股票代码格式: sh600000, sz000001
        String prefix = "SH".equalsIgnoreCase(market) ? "sh" : "sz";
        String codesParam = codes.stream()
                .map(code -> prefix + code)
                .reduce((a, b) -> a + "," + b)
                .orElse("");

        String url = BASE_URL + codesParam;

        log.info("腾讯财经报价接口: {}", url);

        try {
            String response = HttpClientUtil.get(url, null, 10000);
            
            if (response == null || response.isEmpty()) {
                log.warn("腾讯财经接口返回空");
                throw new RuntimeException("接口返回空");
            }
            
            log.info("腾讯财经原始响应: {}", response.substring(0, Math.min(200, response.length())));

            // 解析响应 - 腾讯返回格式: v_sh600000="data..."
            String[] lines = response.split(";");
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                
                // 解析 key 和 value
                int eqIndex = line.indexOf('=');
                if (eqIndex <= 0) continue;
                
                String key = line.substring(0, eqIndex).trim();
                String value = line.substring(eqIndex + 1).trim();
                
                // 去掉首尾的引号
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }
                
                // 提取股票代码: v_sh600000 -> 600000, v_sz000001 -> 000001
                String stockCode = key;
                if (stockCode.startsWith("v_sh")) {
                    stockCode = stockCode.substring(4);  // 去掉 "v_sh" 得到 "600000"
                } else if (stockCode.startsWith("v_sz")) {
                    stockCode = stockCode.substring(4);  // 去掉 "v_sz" 得到 "000001"
                } else {
                    continue;
                }
                
                StockQuote quote = parseQuote(value, market);
                if (quote != null) {
                    quote.setStockCode(stockCode);
                    result.put(stockCode, quote);
                }
            }

            log.info("腾讯财经报价解析成功: {} 条", result.size());

        } catch (Exception e) {
            log.error("腾讯财经接口调用异常: {}", e.getMessage());
            throw new RuntimeException("腾讯财经接口异常: " + e.getMessage());
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
     * 解析腾讯财经报价数据
     * 格式: 0~股票名称~股票代码~当前价~开盘价~...~涨跌额~涨跌幅
     */
    private StockQuote parseQuote(String data, String defaultMarket) {
        if (data == null || data.isEmpty()) {
            return null;
        }

        try {
            // 腾讯财经返回GBK编码，直接使用UTF-8解码（HttpClientUtil已处理编码）
            String decoded = data;
            String[] fields = decoded.split("~");
            
            if (fields.length < 10) {
                log.warn("腾讯财经数据格式错误: {}", decoded.substring(0, Math.min(100, decoded.length())));
                return null;
            }

            StockQuote quote = new StockQuote();

            // 0: 市场代码 0=深圳, 1=上海
            String marketCode = fields[0];
            if ("1".equals(marketCode)) {
                quote.setMarket("SH");
            } else if ("0".equals(marketCode)) {
                quote.setMarket("SZ");
            } else {
                quote.setMarket(defaultMarket);
            }

            // 1: 股票名称
            quote.setStockName(fields[1]);

            // 2: 股票代码
            // quote.setStockCode(fields[2]); // 已经在外层设置

            // 3: 当前价格
            if (fields.length > 3 && !fields[3].isEmpty()) {
                try {
                    quote.setPrice(new BigDecimal(fields[3]));
                } catch (NumberFormatException e) {
                    // 忽略
                }
            }

            // 4: 开盘价
            // 5: 成交量(手)
            // 6: 外盘
            // 7: 内盘
            // 8: 最近成交价
            // 9: 委比
            // 10: 昨收
            if (fields.length > 10 && !fields[10].isEmpty()) {
                try {
                    quote.setLastClose(new BigDecimal(fields[10]));
                } catch (NumberFormatException e) {
                    // 忽略
                }
            }

            // 31: 涨跌额, 32: 涨跌幅 (腾讯财经API的真实字段位置)
            if (fields.length > 32 && !fields[32].isEmpty()) {
                try {
                    quote.setChangePercent(new BigDecimal(fields[32]));
                } catch (NumberFormatException e) {
                    // 忽略
                }
            }

            if (fields.length > 31 && !fields[31].isEmpty()) {
                try {
                    quote.setChange(new BigDecimal(fields[31]));
                } catch (NumberFormatException e) {
                    // 忽略
                }
            }

            quote.setSuccess(true);
            return quote;

        } catch (Exception e) {
            log.error("解析腾讯财经数据失败: {}", e.getMessage());
            return null;
        }
    }
}