package com.base.stock.factory;

import com.base.stock.client.QuoteProvider;
import com.base.stock.client.impl.EastMoneyQuoteProvider;
import com.base.stock.client.impl.ITickQuoteProvider;
import com.base.stock.config.QuoteSourceConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 报价提供者工厂
 * 支持主数据源和降级逻辑
 *
 * @author base
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class QuoteProviderFactory {

    private final QuoteSourceConfig config;
    private final EastMoneyQuoteProvider eastMoneyQuoteProvider;
    private final ITickQuoteProvider itickQuoteProvider;

    private final Map<String, QuoteProvider> providerMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        providerMap.put("eastmoney", eastMoneyQuoteProvider);
        providerMap.put("itick", itickQuoteProvider);
        log.info("报价提供者工厂初始化完成，支持的数据源: {}", providerMap.keySet());
    }

    /**
     * 获取主数据源报价提供者
     */
    public QuoteProvider getPrimaryProvider() {
        String source = config.getSource();
        QuoteProvider provider = providerMap.get(source);
        if (provider == null) {
            log.warn("未找到报价提供者: {}, 使用默认 eastmoney", source);
            provider = eastMoneyQuoteProvider;
        }
        log.info("使用主数据源: {}", provider.getName());
        return provider;
    }

    /**
     * 获取降级数据源报价提供者
     */
    public QuoteProvider getFallbackProvider() {
        if (!config.isFallbackEnabled()) {
            return null;
        }
        String source = config.getFallbackSource();
        QuoteProvider provider = providerMap.get(source);
        if (provider == null) {
            log.warn("未找到降级报价提供者: {}", source);
            return null;
        }
        log.info("使用降级数据源: {}", provider.getName());
        return provider;
    }

    /**
     * 获取所有可用的提供者
     */
    public List<QuoteProvider> getAllProviders() {
        return new java.util.ArrayList<>(providerMap.values());
    }
}