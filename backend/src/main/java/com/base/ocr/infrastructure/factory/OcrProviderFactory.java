package com.base.ocr.infrastructure.factory;

import com.base.ocr.domain.service.OcrProvider;
import com.base.ocr.infrastructure.config.OcrSourceConfig;
import com.base.ocr.infrastructure.provider.AliyunOcrProvider;
import com.base.ocr.infrastructure.provider.BaiduOcrProvider;
import com.base.ocr.infrastructure.provider.TencentOcrProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OCR 供应商工厂
 * <p>
 * 支持主数据源和降级逻辑，参考 WeatherProviderFactory 设计。
 * </p>
 *
 * @author base
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OcrProviderFactory {

    private final OcrSourceConfig config;
    private final TencentOcrProvider tencentOcrProvider;
    private final BaiduOcrProvider baiduOcrProvider;
    private final AliyunOcrProvider aliyunOcrProvider;

    private final Map<String, OcrProvider> providerMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        providerMap.put("tencent", tencentOcrProvider);
        providerMap.put("baidu", baiduOcrProvider);
        providerMap.put("aliyun", aliyunOcrProvider);
        log.info("OCR 供应商工厂初始化完成，支持的供应商: {}", providerMap.keySet());
    }

    /**
     * 获取主数据源
     */
    public OcrProvider getPrimaryProvider() {
        String source = config.getSource();
        OcrProvider provider = providerMap.get(source);
        if (provider == null) {
            log.warn("未找到 OCR 供应商: {}, 使用默认 tencent", source);
            provider = tencentOcrProvider;
        }
        return provider;
    }

    /**
     * 获取降级数据源
     */
    public OcrProvider getFallbackProvider() {
        if (!config.isFallbackEnabled()) {
            return null;
        }
        String source = config.getFallbackSource();
        OcrProvider provider = providerMap.get(source);
        if (provider == null) {
            log.warn("未找到降级 OCR 供应商: {}", source);
            return null;
        }
        return provider;
    }

    /**
     * 获取所有可用的供应商列表
     */
    public List<OcrProvider> getAllProviders() {
        return new ArrayList<>(providerMap.values());
    }

    /**
     * 根据名称获取指定供应商
     *
     * @param name 供应商名称
     * @return 供应商实例，不存在返回 null
     */
    public OcrProvider getProvider(String name) {
        return providerMap.get(name);
    }
}
