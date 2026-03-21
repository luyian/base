package com.base.stock.client.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.base.common.util.HttpClientUtil;
import com.base.stock.client.QuoteProvider;
import com.base.stock.config.ITickConfig;
import com.base.stock.dto.StockQuote;
import com.base.stock.entity.ApiToken;
import com.base.stock.service.TokenManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * iTick 报价提供者（保留现有实现）
 *
 * @author base
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ITickQuoteProvider implements QuoteProvider {

    private static final String PROVIDER = "itick";

    private final ITickConfig iTickConfig;
    private final TokenManagerService tokenManagerService;

    @Override
    public String getName() {
        return "itick";
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

            // iTick 单次最多 100 只
            for (int i = 0; i < marketCodes.size(); i += 100) {
                List<String> batch = marketCodes.subList(i, Math.min(i + 100, marketCodes.size()));
                try {
                    Map<String, StockQuote> batchResult = fetchBatchQuotes(market, batch);
                    result.putAll(batchResult);
                } catch (Exception e) {
                    log.error("iTick 批量获取报价失败，market: {}, codes: {}", market, batch, e);
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

    private Map<String, List<String>> groupByMarket(List<String> codes) {
        Map<String, List<String>> groups = new HashMap<>();
        for (String code : codes) {
            String market = inferMarket(code);
            groups.computeIfAbsent(market, k -> new java.util.ArrayList<>()).add(code);
        }
        return groups;
    }

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

    private Map<String, StockQuote> fetchBatchQuotes(String market, List<String> codes) {
        Map<String, StockQuote> quotes = new HashMap<>();

        String codesParam = String.join(",", codes);
        String url = iTickConfig.getBaseUrl() + "/stock/quotes?region=" + market + "&codes=" + codesParam;

        ApiToken token = tokenManagerService.getNextTokenEntity(PROVIDER);
        Map<String, String> headers = new HashMap<>();
        headers.put("accept", "application/json");
        headers.put("Content-Type", "application/json");
        headers.put("token", token.getTokenValue());

        log.debug("iTick 报价接口: {}", url);

        try {
            String response = HttpClientUtil.getWithRetry(url, headers, iTickConfig.getRetry());
            log.debug("iTick 报价响应: {}", response);

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
                log.error("iTick 接口返回错误: code={}, msg={}", code, jsonResponse.getString("msg"));
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
            log.error("iTick 接口调用异常", e);
            tokenManagerService.recordTokenFailure(token.getId());
            for (String stockCode : codes) {
                StockQuote quote = new StockQuote();
                quote.setStockCode(stockCode);
                quote.setSuccess(false);
                quote.setErrorMsg(e.getMessage());
                quotes.put(stockCode, quote);
            }
        }

        // 确保所有股票都有返回值
        for (String code : codes) {
            if (!quotes.containsKey(code)) {
                StockQuote quote = new StockQuote();
                quote.setStockCode(code);
                quote.setSuccess(false);
                quote.setErrorMsg("未获取到报价");
                quotes.put(code, quote);
            }
        }

        return quotes;
    }

    private StockQuote parseQuote(JSONObject item) {
        if (item == null) {
            return null;
        }
        StockQuote quote = new StockQuote();

        String symbol = item.getString("s");
        if (symbol != null && symbol.contains(".")) {
            String[] parts = symbol.split("\\.");
            if (parts.length > 1) {
                quote.setMarket(parts[1]);
            }
        }

        quote.setPrice(item.getBigDecimal("ld"));
        quote.setLastClose(item.getBigDecimal("p"));
        quote.setChange(item.getBigDecimal("ch"));
        quote.setChangePercent(item.getBigDecimal("chp"));
        quote.setSuccess(true);
        return quote;
    }
}