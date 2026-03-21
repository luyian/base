package com.base.stock.client;

import com.base.stock.dto.StockQuote;

import java.util.List;
import java.util.Map;

/**
 * 股票报价提供者接口
 *
 * @author base
 */
public interface QuoteProvider {

    /**
     * 提供者名称
     */
    String getName();

    /**
     * 批量获取股票报价
     *
     * @param codes 股票代码列表
     * @return 股票代码 -> 报价
     */
    Map<String, StockQuote> getQuotes(List<String> codes);

    /**
     * 获取单只股票报价
     *
     * @param code  股票代码
     * @param market 市场（SH/SZ）
     * @return 报价
     */
    default StockQuote getQuote(String code, String market) {
        List<String> codes = java.util.Collections.singletonList(code);
        Map<String, StockQuote> result = getQuotes(codes);
        return result.get(code);
    }
}