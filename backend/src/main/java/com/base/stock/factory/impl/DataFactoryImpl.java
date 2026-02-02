package com.base.stock.factory.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.base.stock.entity.DataMapping;
import com.base.stock.factory.DataFactory;
import com.base.stock.factory.FieldMapping;
import com.base.stock.factory.FieldMappingProcessor;
import com.base.stock.factory.MappingConfig;
import com.base.stock.mapper.DataMappingMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据工厂实现类
 *
 * @author base
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataFactoryImpl implements DataFactory {

    private final DataMappingMapper dataMappingMapper;

    @Override
    public <T> List<T> transform(String sourceJson, String mappingCode, Class<T> targetClass) {
        if (sourceJson == null || sourceJson.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取映射配置
        MappingConfig config = getMappingConfig(mappingCode);
        if (config == null || config.getMappings() == null || config.getMappings().isEmpty()) {
            log.error("映射配置不存在或为空，mappingCode: {}", mappingCode);
            throw new RuntimeException("映射配置不存在: " + mappingCode);
        }

        List<T> result = new ArrayList<>();

        // 判断源数据是数组还是对象
        String trimmed = sourceJson.trim();
        JSONArray dataArray = null;

        if (trimmed.startsWith("{")) {
            // iTick API 返回格式: {"code":0,"msg":"ok","data":[...]}
            JSONObject responseObj = JSON.parseObject(sourceJson);
            if (responseObj.containsKey("data") && responseObj.get("data") != null) {
                Object data = responseObj.get("data");
                if (data instanceof JSONArray) {
                    dataArray = (JSONArray) data;
                } else if (data instanceof JSONObject) {
                    // 单个对象包装成数组
                    dataArray = new JSONArray();
                    dataArray.add(data);
                }
            } else {
                // 普通对象，直接处理
                T target = transformObject(responseObj, config, targetClass);
                if (target != null) {
                    result.add(target);
                }
                return result;
            }
        } else if (trimmed.startsWith("[")) {
            // 直接是数组
            dataArray = JSON.parseArray(sourceJson);
        }

        if (dataArray != null) {
            for (int i = 0; i < dataArray.size(); i++) {
                JSONObject obj = dataArray.getJSONObject(i);
                T target = transformObject(obj, config, targetClass);
                if (target != null) {
                    result.add(target);
                }
            }
        }

        return result;
    }

    @Override
    public <T> T transformOne(String sourceJson, String mappingCode, Class<T> targetClass) {
        List<T> list = transform(sourceJson, mappingCode, targetClass);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 获取映射配置
     */
    private MappingConfig getMappingConfig(String mappingCode) {
        LambdaQueryWrapper<DataMapping> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataMapping::getMappingCode, mappingCode)
                .eq(DataMapping::getStatus, 1)
                .eq(DataMapping::getDeleted, 0);
        DataMapping dataMapping = dataMappingMapper.selectOne(wrapper);

        if (dataMapping == null) {
            return null;
        }

        return JSON.parseObject(dataMapping.getFieldMapping(), MappingConfig.class);
    }

    /**
     * 转换单个对象
     */
    private <T> T transformObject(JSONObject source, MappingConfig config, Class<T> targetClass) {
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            Map<String, Field> fieldMap = getFieldMap(targetClass);

            for (FieldMapping mapping : config.getMappings()) {
                String sourceField = mapping.getSource();
                String targetField = mapping.getTarget();

                // 获取源值
                Object sourceValue = getNestedValue(source, sourceField);

                // 转换值
                Object convertedValue = FieldMappingProcessor.mapField(sourceValue, mapping);

                // 设置目标值
                Field field = fieldMap.get(targetField);
                if (field != null && convertedValue != null) {
                    field.setAccessible(true);
                    field.set(target, convertedValue);
                }
            }

            return target;
        } catch (Exception e) {
            log.error("对象转换失败", e);
            return null;
        }
    }

    /**
     * 获取嵌套值（支持 a.b.c 格式）
     */
    private Object getNestedValue(JSONObject source, String path) {
        if (path == null || path.isEmpty()) {
            return null;
        }

        String[] parts = path.split("\\.");
        Object current = source;

        for (String part : parts) {
            if (current == null) {
                return null;
            }
            if (current instanceof JSONObject) {
                current = ((JSONObject) current).get(part);
            } else {
                return null;
            }
        }

        return current;
    }

    /**
     * 获取类的字段映射（包括父类）
     */
    private Map<String, Field> getFieldMap(Class<?> clazz) {
        Map<String, Field> fieldMap = new HashMap<>();
        Class<?> current = clazz;

        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                if (!fieldMap.containsKey(field.getName())) {
                    fieldMap.put(field.getName(), field);
                }
            }
            current = current.getSuperclass();
        }

        return fieldMap;
    }
}
