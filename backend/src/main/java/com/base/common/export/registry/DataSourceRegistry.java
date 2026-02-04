package com.base.common.export.registry;

import com.base.common.export.provider.ExportDataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据源注册表
 *
 * @author base
 * @since 2026-02-04
 */
@Component
public class DataSourceRegistry {

    @Autowired
    private List<ExportDataProvider> providers;

    private final Map<String, ExportDataProvider> providerMap = new HashMap<>();

    @PostConstruct
    public void init() {
        for (ExportDataProvider provider : providers) {
            // 根据 supports 方法注册
            if (provider.supports("SERVICE")) {
                providerMap.put("SERVICE", provider);
            }
            if (provider.supports("SQL")) {
                providerMap.put("SQL", provider);
            }
        }
    }

    /**
     * 获取数据提供者
     *
     * @param dataSourceType 数据源类型
     * @return 数据提供者
     */
    public ExportDataProvider getProvider(String dataSourceType) {
        return providerMap.get(dataSourceType);
    }

    /**
     * 注册数据提供者
     *
     * @param dataSourceType 数据源类型
     * @param provider       数据提供者
     */
    public void register(String dataSourceType, ExportDataProvider provider) {
        providerMap.put(dataSourceType, provider);
    }
}
