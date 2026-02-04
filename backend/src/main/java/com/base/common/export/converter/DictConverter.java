package com.base.common.export.converter;

import com.base.common.export.engine.ExportContext;
import com.base.system.dto.enums.EnumResponse;
import com.base.system.export.entity.ExportField;
import com.base.system.service.EnumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 字典转换器
 * 将字典值转换为字典标签
 *
 * @author base
 * @since 2026-02-04
 */
@Component("dictConverter")
public class DictConverter implements DataConverter {

    @Autowired
    private EnumService enumService;

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
     * @return 字典映射（编码 -> 显示值）
     */
    private Map<String, String> loadDictMap(String dictType) {
        Map<String, String> map = new ConcurrentHashMap<>();
        try {
            List<EnumResponse> enumList = enumService.listByType(dictType);
            if (enumList != null) {
                for (EnumResponse enumResponse : enumList) {
                    // enumCode 是编码（如 0、1），enumValue 是显示值（如 失败、成功）
                    map.put(enumResponse.getEnumCode(), enumResponse.getEnumValue());
                }
            }
        } catch (Exception e) {
            // 忽略异常，返回空映射
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
