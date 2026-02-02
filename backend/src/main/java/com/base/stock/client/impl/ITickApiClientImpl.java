package com.base.stock.client.impl;

import com.base.common.util.HttpClientUtil;
import com.base.stock.client.ITickApiClient;
import com.base.stock.config.ITickConfig;
import com.base.stock.entity.ApiToken;
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
        String region = market.toLowerCase();
        String url = iTickConfig.getBaseUrl() + "/symbol/list?type=stock&region=" + region;

        log.info("拉取股票列表，market: {}, url: {}", market, url);

        return executeWithTokenFailureHandling(url);
    }

    @Override
    public String fetchKlineData(String stockCode, String period, LocalDate startDate, LocalDate endDate) {
        String code = stockCode;
        String region = "hk";

        if (stockCode.contains(".")) {
            String[] parts = stockCode.split("\\.");
            code = parts[0];
            if (parts.length > 1) {
                region = parts[1].toLowerCase();
            }
        }

        int kType = 8;
        if ("week".equalsIgnoreCase(period)) {
            kType = 9;
        } else if ("month".equalsIgnoreCase(period)) {
            kType = 10;
        }

        StringBuilder urlBuilder = new StringBuilder(iTickConfig.getBaseUrl());
        urlBuilder.append("/stock/kline");
        urlBuilder.append("?region=").append(region);
        urlBuilder.append("&code=").append(code);
        urlBuilder.append("&kType=").append(kType);
        urlBuilder.append("&limit=100");

        String url = urlBuilder.toString();

        log.info("拉取K线数据，stockCode: {}, period: {}, url: {}", stockCode, period, url);

        return executeWithTokenFailureHandling(url);
    }

    /**
     * 执行请求并处理 Token 失败
     *
     * @param url 请求地址
     * @return 响应内容
     */
    private String executeWithTokenFailureHandling(String url) {
        ApiToken token = tokenManagerService.getNextTokenEntity(PROVIDER);
        Map<String, String> headers = buildHeaders(token.getTokenValue());

        log.info("请求头: {}", headers);

        try {
          String response = HttpClientUtil.getWithRetry(url, headers, iTickConfig.getRetry());
            log.info("响应内容: {}", response);

            if (response != null && isTokenError(response)) {
                log.error("Token 认证失败，tokenId: {}, response: {}", token.getId(), response);
                tokenManagerService.recordTokenFailure(token.getId());
                throw new RuntimeException("Token 认证失败: " + response);
            }

            tokenManagerService.resetTokenFailure(token.getId());
            return response;
        } catch (Exception e) {
            if (e.getMessage() == null || !e.getMessage().contains("Token 认证失败")) {
                log.error("请求失败，tokenId: {}, error: {}", token.getId(), e.getMessage());
                tokenManagerService.recordTokenFailure(token.getId());
            }
            throw e;
        }
    }

    /**
     * 判断是否为 Token 相关错误
     *
     * @param response 响应内容
     * @return 是否为 Token 错误
     */
    private boolean isTokenError(String response) {
        return response.contains("\"code\":1") &&
                (response.contains("token") || response.contains("Token") ||
                 response.contains("unauthorized") || response.contains("Unauthorized") ||
                 response.contains("authentication") || response.contains("Authentication"));
    }

    /**
     * 构建请求头（包含 Token）
     *
     * @param tokenValue Token 值
     * @return 请求头
     */
    private Map<String, String> buildHeaders(String tokenValue) {
        Map<String, String> headers = new HashMap<>();
        headers.put("accept", "application/json");
        headers.put("Content-Type", "application/json");
        headers.put("token", tokenValue);
        return headers;
    }
}
