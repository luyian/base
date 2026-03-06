package com.base.common.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 枚举工具类
 *
 * @author base
 */
public class EnumUtil {

    /**
     * 获取枚举的所有选项，用于前端Select组件
     *
     * @param enumClass 枚举类
     * @return 包含 code 和 desc 的 Map 列表
     */
    public static List<Map<String, String>> getOptions(Class<? extends Enum<?>> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .map(e -> {
                    try {
                        String code = (String) enumClass.getMethod("getCode").invoke(e);
                        String desc = (String) enumClass.getMethod("getDesc").invoke(e);
                        return Map.of("value", code, "label", desc);
                    } catch (Exception ex) {
                        return null;
                    }
                })
                .filter(item -> item != null)
                .collect(Collectors.toList());
    }

    /**
     * 根据 code 获取枚举的 desc
     *
     * @param enumClass 枚举类
     * @param code      枚举 code
     * @return desc，如果未找到返回 null
     */
    public static String getDescByCode(Class<? extends Enum<?>> enumClass, String code) {
        if (code == null) {
            return null;
        }
        return Arrays.stream(enumClass.getEnumConstants())
                .map(e -> {
                    try {
                        String enumCode = (String) enumClass.getMethod("getCode").invoke(e);
                        if (code.equals(enumCode)) {
                            return (String) enumClass.getMethod("getDesc").invoke(e);
                        }
                    } catch (Exception ex) {
                        return null;
                    }
                    return null;
                })
                .filter(desc -> desc != null)
                .findFirst()
                .orElse(null);
    }

    /**
     * 根据 code 获取枚举实例
     *
     * @param enumClass 枚举类
     * @param code      枚举 code
     * @return 枚举实例，如果未找到返回 null
     */
    public static <T extends Enum<T>> T getEnumByCode(Class<T> enumClass, String code) {
        if (code == null) {
            return null;
        }
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(e -> {
                    try {
                        String enumCode = (String) enumClass.getMethod("getCode").invoke(e);
                        return code.equals(enumCode);
                    } catch (Exception ex) {
                        return false;
                    }
                })
                .findFirst()
                .orElse(null);
    }
}
