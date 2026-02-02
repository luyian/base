package com.base.stock.client.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.base.common.util.HttpClientUtil;
import com.base.stock.client.ITickApiClient;
import com.base.stock.config.ITickConfig;
import com.base.stock.entity.ApiToken;
import com.base.stock.entity.StockInfo;
import com.base.stock.mapper.StockInfoMapper;
import com.base.stock.service.TokenManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
    private final StockInfoMapper stockInfoMapper;

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
        String region;

        if (stockCode.contains(".")) {
            // 带后缀格式：601778.SH
            String[] parts = stockCode.split("\\.");
            code = parts[0];
            region = parts.length > 1 ? parts[1] : guessRegionByCode(code);
        } else {
            // 不带后缀，根据代码规则判断市场
            region = guessRegionByCode(code);
        }

        int kType = 8;
        if ("week".equalsIgnoreCase(period)) {
            kType = 9;
        } else if ("month".equalsIgnoreCase(period)) {
            kType = 10;
        }

        // 计算 limit：使用自然日天数确保覆盖所有交易日，上限100条
        int limit = 100;
        if (startDate != null && endDate != null) {
            long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
            limit = (int) Math.min(days, 100);
        }

        // 计算 et：结束日期的23:59:59时间戳（毫秒级）
        Long et = null;
        if (endDate != null) {
            et = endDate.atTime(23, 59, 59)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli();
        }

        StringBuilder urlBuilder = new StringBuilder(iTickConfig.getBaseUrl());
        urlBuilder.append("/stock/kline");
        urlBuilder.append("?region=").append(region);
        urlBuilder.append("&code=").append(code);
        urlBuilder.append("&kType=").append(kType);
        urlBuilder.append("&limit=").append(limit);
        if (et != null) {
            urlBuilder.append("&et=").append(et);
        }

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

    @Override
    public String fetchMinuteKlineData(String stockCode, int kType, Long et, int limit) {
        String code = stockCode;
        String region;

        if (stockCode.contains(".")) {
            String[] parts = stockCode.split("\\.");
            code = parts[0];
            region = parts.length > 1 ? parts[1] : guessRegionByCode(code);
        } else {
            region = guessRegionByCode(code);
        }

        StringBuilder urlBuilder = new StringBuilder(iTickConfig.getBaseUrl());
        urlBuilder.append("/stock/kline");
        urlBuilder.append("?region=").append(region);
        urlBuilder.append("&code=").append(code);
        urlBuilder.append("&kType=").append(kType);
        urlBuilder.append("&limit=").append(limit);
        if (et != null) {
            urlBuilder.append("&et=").append(et);
        }

        String url = urlBuilder.toString();

        log.info("拉取分钟K线数据，stockCode: {}, kType: {}, et: {}, limit: {}, url: {}", stockCode, kType, et, limit, url);

        return executeWithTokenFailureHandling(url);
    }

    /**
     * 根据股票代码规则推断市场
     *
     * @param code 股票代码（纯数字）
     * @return 市场代码（sh/sz/hk）
     */
    private String guessRegionByCode(String code) {
        if (code == null || code.isEmpty()) {
            return "hk";
        }
        // 先从数据库查询
        LambdaQueryWrapper<StockInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StockInfo::getStockCode, code)
                .select(StockInfo::getMarket);
        StockInfo stockInfo = stockInfoMapper.selectOne(wrapper);
        if (stockInfo != null && stockInfo.getMarket() != null) {
            return stockInfo.getMarket();
        }

        // 数据库查不到，根据代码规则推断
        // 沪市：60开头（主板）、68开头（科创板）
        if (code.startsWith("60") || code.startsWith("68")) {
            return "SH";
        }
        // 深市：00开头（主板）、30开头（创业板）
        if (code.startsWith("00") || code.startsWith("30")) {
            return "SZ";
        }
        // 默认港股
        return "HK";
    }
}
