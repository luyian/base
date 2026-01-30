package com.base.stock.factory;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 字段映射处理器
 * 负责字段值的类型转换
 *
 * @author base
 */
@Slf4j
public class FieldMappingProcessor {

    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    private static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 执行字段映射转换
     *
     * @param sourceValue 源值
     * @param mapping     映射配置
     * @return 转换后的值
     */
    public static Object mapField(Object sourceValue, FieldMapping mapping) {
        if (sourceValue == null) {
            return parseDefaultValue(mapping);
        }

        String type = mapping.getType();
        if (type == null || type.isEmpty()) {
            type = "string";
        }

        try {
            switch (type.toLowerCase()) {
                case "string":
                    return convertToString(sourceValue);
                case "int":
                case "integer":
                    return convertToInteger(sourceValue);
                case "long":
                    return convertToLong(sourceValue);
                case "decimal":
                case "bigdecimal":
                    return convertToDecimal(sourceValue, mapping.getScale());
                case "date":
                    return convertToDate(sourceValue, mapping.getDateFormat());
                case "datetime":
                    return convertToDateTime(sourceValue, mapping.getDateFormat());
                case "boolean":
                    return convertToBoolean(sourceValue);
                default:
                    return sourceValue.toString();
            }
        } catch (Exception e) {
            log.warn("字段转换失败，source: {}, type: {}, error: {}", sourceValue, type, e.getMessage());
            return parseDefaultValue(mapping);
        }
    }

    private static String convertToString(Object value) {
        return value.toString();
    }

    private static Integer convertToInteger(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return Integer.parseInt(value.toString().trim());
    }

    private static Long convertToLong(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return Long.parseLong(value.toString().trim());
    }

    private static BigDecimal convertToDecimal(Object value, Integer scale) {
        BigDecimal decimal;
        if (value instanceof BigDecimal) {
            decimal = (BigDecimal) value;
        } else if (value instanceof Number) {
            decimal = BigDecimal.valueOf(((Number) value).doubleValue());
        } else {
            decimal = new BigDecimal(value.toString().trim());
        }
        if (scale != null && scale >= 0) {
            decimal = decimal.setScale(scale, RoundingMode.HALF_UP);
        }
        return decimal;
    }

    private static LocalDate convertToDate(Object value, String dateFormat) {
        if (value instanceof LocalDate) {
            return (LocalDate) value;
        }
        if (value instanceof LocalDateTime) {
            return ((LocalDateTime) value).toLocalDate();
        }
        // 处理时间戳（毫秒）
        if (value instanceof Number) {
            long timestamp = ((Number) value).longValue();
            // 如果是秒级时间戳，转换为毫秒
            if (timestamp < 10000000000L) {
                timestamp *= 1000;
            }
            return LocalDateTime.ofInstant(
                    java.time.Instant.ofEpochMilli(timestamp),
                    java.time.ZoneId.systemDefault()
            ).toLocalDate();
        }
        String format = dateFormat != null ? dateFormat : DEFAULT_DATE_FORMAT;
        return LocalDate.parse(value.toString().trim(), DateTimeFormatter.ofPattern(format));
    }

    private static LocalDateTime convertToDateTime(Object value, String dateFormat) {
        if (value instanceof LocalDateTime) {
            return (LocalDateTime) value;
        }
        if (value instanceof LocalDate) {
            return ((LocalDate) value).atStartOfDay();
        }
        // 处理时间戳（毫秒）
        if (value instanceof Number) {
            long timestamp = ((Number) value).longValue();
            // 如果是秒级时间戳，转换为毫秒
            if (timestamp < 10000000000L) {
                timestamp *= 1000;
            }
            return LocalDateTime.ofInstant(
                    java.time.Instant.ofEpochMilli(timestamp),
                    java.time.ZoneId.systemDefault()
            );
        }
        String format = dateFormat != null ? dateFormat : DEFAULT_DATETIME_FORMAT;
        return LocalDateTime.parse(value.toString().trim(), DateTimeFormatter.ofPattern(format));
    }

    private static Boolean convertToBoolean(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        String str = value.toString().trim().toLowerCase();
        return "true".equals(str) || "1".equals(str) || "yes".equals(str);
    }

    private static Object parseDefaultValue(FieldMapping mapping) {
        if (mapping.getDefaultValue() == null || mapping.getDefaultValue().isEmpty()) {
            return null;
        }
        String defaultValue = mapping.getDefaultValue();
        String type = mapping.getType();
        if (type == null || type.isEmpty()) {
            return defaultValue;
        }
        try {
            switch (type.toLowerCase()) {
                case "int":
                case "integer":
                    return Integer.parseInt(defaultValue);
                case "long":
                    return Long.parseLong(defaultValue);
                case "decimal":
                case "bigdecimal":
                    return new BigDecimal(defaultValue);
                case "boolean":
                    return Boolean.parseBoolean(defaultValue);
                default:
                    return defaultValue;
            }
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
