package com.base.common.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONPath;
import com.alibaba.fastjson2.TypeReference;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * JSON 工具类
 * 基于 FastJson2 封装常用的 JSON 操作方法
 *
 * @author base
 */
@Slf4j
public class JsonUtil {

    /**
     * 对象转 JSON 字符串
     *
     * @param obj 对象
     * @return JSON 字符串
     */
    public static String toJsonString(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return JSON.toJSONString(obj);
        } catch (Exception e) {
            log.error("对象转 JSON 字符串失败", e);
            throw new RuntimeException("对象转 JSON 字符串失败: " + e.getMessage(), e);
        }
    }

    /**
     * 对象转格式化的 JSON 字符串
     *
     * @param obj 对象
     * @return 格式化的 JSON 字符串
     */
    public static String toPrettyJsonString(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return JSON.toJSONString(obj, com.alibaba.fastjson2.JSONWriter.Feature.PrettyFormat);
        } catch (Exception e) {
            log.error("对象转格式化 JSON 字符串失败", e);
            throw new RuntimeException("对象转格式化 JSON 字符串失败: " + e.getMessage(), e);
        }
    }

    /**
     * JSON 字符串转对象
     *
     * @param json  JSON 字符串
     * @param clazz 目标类型
     * @param <T>   泛型类型
     * @return 对象
     */
    public static <T> T parseObject(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return JSON.parseObject(json, clazz);
        } catch (Exception e) {
            log.error("JSON 字符串转对象失败，JSON: {}", json, e);
            throw new RuntimeException("JSON 字符串转对象失败: " + e.getMessage(), e);
        }
    }

    /**
     * JSON 字符串转 JSONObject
     *
     * @param json JSON 字符串
     * @return JSONObject
     */
    public static JSONObject parseObject(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return JSON.parseObject(json);
        } catch (Exception e) {
            log.error("JSON 字符串转 JSONObject 失败，JSON: {}", json, e);
            throw new RuntimeException("JSON 字符串转 JSONObject 失败: " + e.getMessage(), e);
        }
    }

    /**
     * JSON 字符串转 List
     *
     * @param json  JSON 字符串
     * @param clazz 元素类型
     * @param <T>   泛型类型
     * @return List
     */
    public static <T> List<T> parseArray(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return JSON.parseArray(json, clazz);
        } catch (Exception e) {
            log.error("JSON 字符串转 List 失败，JSON: {}", json, e);
            throw new RuntimeException("JSON 字符串转 List 失败: " + e.getMessage(), e);
        }
    }

    /**
     * JSON 字符串转 JSONArray
     *
     * @param json JSON 字符串
     * @return JSONArray
     */
    public static JSONArray parseArray(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return JSON.parseArray(json);
        } catch (Exception e) {
            log.error("JSON 字符串转 JSONArray 失败，JSON: {}", json, e);
            throw new RuntimeException("JSON 字符串转 JSONArray 失败: " + e.getMessage(), e);
        }
    }

    /**
     * JSON 字符串转泛型对象
     *
     * @param json          JSON 字符串
     * @param typeReference 类型引用
     * @param <T>           泛型类型
     * @return 对象
     */
    public static <T> T parseObject(String json, TypeReference<T> typeReference) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return JSON.parseObject(json, typeReference);
        } catch (Exception e) {
            log.error("JSON 字符串转泛型对象失败，JSON: {}", json, e);
            throw new RuntimeException("JSON 字符串转泛型对象失败: " + e.getMessage(), e);
        }
    }

    /**
     * 使用 JSONPath 读取 JSON 节点值
     *
     * @param json JSON 字符串
     * @param path JSONPath 表达式（如：$.data.list）
     * @param <T>  泛型类型
     * @return 节点值
     */
    @SuppressWarnings("unchecked")
    public static <T> T read(String json, String path) {
        if (json == null || json.isEmpty() || path == null || path.isEmpty()) {
            return null;
        }
        try {
            return (T) JSONPath.eval(json, path);
        } catch (Exception e) {
            log.error("JSONPath 读取失败，JSON: {}，Path: {}", json, path, e);
            throw new RuntimeException("JSONPath 读取失败: " + e.getMessage(), e);
        }
    }

    /**
     * 使用 JSONPath 读取 JSON 节点值指定类型
     *
     * @param json  JSON 字符串
     * @param path  JSONPath 表达式
     * @param clazz 目标类型
     * @param <T>   泛型类型
     * @return 节点值
     */
    public static <T> T read(String json, String path, Class<T> clazz) {
        if (json == null || json.isEmpty() || path == null || path.isEmpty()) {
            return null;
        }
        try {
            Object value = JSONPath.eval(json, path);
            if (value == null) {
                return null;
            }
            if (clazz.isInstance(value)) {
                return clazz.cast(value);
            }
            // 如果类型不匹配，尝试转换
            return JSON.parseObject(JSON.toJSONString(value), clazz);
        } catch (Exception e) {
            log.error("JSONPath 读取并转换失败，JSON: {}，Path: {}，Class: {}", json, path, clazz.getName(), e);
            throw new RuntimeException("JSONPath 读取并转换失败: " + e.getMessage(), e);
        }
    }

    /**
     * 使用 JSONPath 读取 JSON 节点值并转换为 List
     *
     * @param json  JSON 字符串
     * @param path  JSONPath 表达式
     * @param clazz 元素类型
     * @param <T>   泛型类型
     * @return List
     */
    public static <T> List<T> readArray(String json, String path, Class<T> clazz) {
        if (json == null || json.isEmpty() || path == null || path.isEmpty()) {
            return null;
        }
        try {
            Object value = JSONPath.eval(json, path);
            if (value == null) {
                return null;
            }
            return JSON.parseArray(JSON.toJSONString(value), clazz);
        } catch (Exception e) {
            log.error("JSONPath 读取 List 失败，JSON: {}，Path: {}，Class: {}", json, path, clazz.getName(), e);
            throw new RuntimeException("JSONPath 读取 List 失败: " + e.getMessage(), e);
        }
    }

    /**
     * 判断字符串是否为有效的 JSON
     *
     * @param json 字符串
     * @return 是否为有效 JSON
     */
    public static boolean isValidJson(String json) {
        if (json == null || json.isEmpty()) {
            return false;
        }
        try {
            JSON.parse(json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断字符串是否为 JSON 对象
     *
     * @param json 字符串
     * @return 是否为 JSON 对象
     */
    public static boolean isJsonObject(String json) {
        if (json == null || json.isEmpty()) {
            return false;
        }
        String trimmed = json.trim();
        return trimmed.startsWith("{") && trimmed.endsWith("}") && isValidJson(json);
    }

    /**
     * 判断字符串是否为 JSON 数组
     *
     * @param json 字符串
     * @return 是否为 JSON 数组
     */
    public static boolean isJsonArray(String json) {
        if (json == null || json.isEmpty()) {
            return false;
        }
        String trimmed = json.trim();
        return trimmed.startsWith("[") && trimmed.endsWith("]") && isValidJson(json);
    }
}
