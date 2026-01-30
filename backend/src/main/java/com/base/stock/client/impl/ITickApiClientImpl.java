package com.base.stock.client.impl;

import com.base.common.util.HttpClientUtil;
import com.base.stock.client.ITickApiClient;
import com.base.stock.config.ITickConfig;
import com.base.stock.service.TokenManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * iTick API 客户端实现类
 *
 * @author base
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ITickApiClientImpl implements ITickApiClient {

    private static final String PROVIDER = "itick";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final ITickConfig iTickConfig;
    private final TokenManagerService tokenManagerService;

    @Override
    public String fetchStockList(String market) {
        String url = iTickConfig.getBaseUrl() + "/stock/symbols?region=" + market;
        Map<String, String> headers = buildHeaders();

        log.info("拉取股票列表，market: {}, url: {}", market, url);

        return HttpClientUtil.getWithRetry(url, headers, iTickConfig.getRetry());
    }

    @Override
    public String fetchKlineData(String stockCode, String period, LocalDate startDate, LocalDate endDate) {
        StringBuilder urlBuilder = new StringBuilder(iTickConfig.getBaseUrl());
        urlBuilder.append("/stock/kline");
        urlBuilder.append("?symbol=").append(stockCode);
        urlBuilder.append("&period=").append(period != null ? period : "day");

        if (startDate != null) {
            urlBuilder.append("&start=").append(startDate.format(DATE_FORMATTER));
        }
        if (endDate != null) {
            urlBuilder.append("&end=").append(endDate.format(DATE_FORMATTER));
        }

        String url = urlBuilder.toString();
        Map<String, String> headers = buildHeaders();

        log.info("拉取K线数据，stockCode: {}, period: {}, url: {}", stockCode, period, url);

        return HttpClientUtil.getWithRetry(url, headers, iTickConfig.getRetry());
    }

    /**
     * 构建请求头（包含 Token）
     */
    private Map<String, String> buildHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        // 从 Toke器获取可用 Token
        String token = tokenManagerService.getNextToken(PROVIDER);
        headers.put("token", token);

        return headers;
    }
}
