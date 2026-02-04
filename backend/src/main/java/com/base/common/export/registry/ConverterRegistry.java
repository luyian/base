package com.base.common.export.registry;

import com.base.common.export.converter.DataConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * 转换器注册表
 *
 * @author base
 * @since 2026-02-04
 */
@Component
public class ConverterRegistry {

    @Autowired
    private ApplicationContext applicationContext;

    private final Map<String, DataConverter> converterMap = new HashMap<>();

    @PostConstruct
    public void init() {
        // 自动注册所有 DataConverter 实现
        Map<String, DataConverter> beans = applicationContext.getBeansOfType(DataConverter.class);
        for (DataConverter converter : beans.values()) {
            converterMap.put(converter.getName(), converter);
        }
    }

    /**
     * 获取转换器
     *
     * @param name 转换器名称
     * @return 转换器
     */
    public DataConverter getConverter(String name) {
        if (!StringUtils.hasText(name)) {
            return null;
        }
        // 先从注册表获取
        DataConverter converter = converterMap.get(name);
        if (converter != null) {
            return converter;
        }
        // 尝试从 Spring 容器获取
        try {
            return applicationContext.getBean(name, DataConverter.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 注册转换器
     *
     * @param name      转换器名称
     * @param converter 转换器
     */
    public void register(String name, DataConverter converter) {
        converterMap.put(name, converter);
    }
}
