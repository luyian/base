package com.base.common.export.converter;

import com.base.common.export.engine.ExportContext;
import com.base.system.dto.dict.DictDataResponse;
import com.base.system.export.entity.ExportField;
import com.base.system.service.DictDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 字典转换器
 * 将字典键值转换为字典标签
 *
 * @author base
 * @since 2026-02-04
 */
@Slf4j
@Component("dictConverter")
public class DictConverter implements DataConverter {

    @Autowired
    private DictDataService dictDataService;

    /**
     * 字典缓存
     */
    private final Map<String, Map<String, String>> dictCache = new ConcurrentHashMap<>();

    @Override
    public Object convert(Object value, ExportField field, ExportContext context) {
        if (value == null || !StringUtils.hasText(field.getDictType())) {
            return value;
        }
        String dictType = field.getDictType();
        String valueStr = String.valueOf(value);

        // 从缓存获取字典映射
        Map<String, String> dictMap = dictCache.computeIfAbsent(dictType, this::loadDictMap);

        return dictMap.getOrDefault(valueStr, valueStr);
    }

    @Override
    public String getName() {
        return "dictConverter";
    }

    /**
     * 加载字典映射
     *
     * @param dictType 字典类型
     * @return 字典映射（键值 -> 标签）
     */
    private Map<String, String> loadDictMap(String dictType) {
        Map<String, String> map = new ConcurrentHashMap<>();
        try {
            List<DictDataResponse> dataList = dictDataService.listByDictType(dictType);
            if (dataList != null) {
                for (DictDataResponse data : dataList) {
                    // dictValue 是键值（如 0、1），dictLabel 是显示标签（如 失败、成功）
                    map.put(data.getDictValue(), data.getDictLabel());
                }
            }
        } catch (Exception e) {
            log.warn("加载字典数据失败: dictType={}, error={}", dictType, e.getMessage());
        }
        return map;
    }

    /**
     * 清除缓存
     */
    public void clearCache() {
        dictCache.clear();
    }
}
