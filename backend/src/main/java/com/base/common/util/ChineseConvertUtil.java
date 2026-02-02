package com.base.common.util;

import com.github.houbb.opencc4j.util.ZhConverterUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 简繁体转换工具类
 *
 * @author base
 */
@Slf4j
public class ChineseConvertUtil {

    /**
     * 简体转繁体
     *
     * @param text 简体文本
     * @return 繁体文本
     */
    public static String toTraditional(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        try {
            return ZhConverterUtil.toTraditional(text);
        } catch (Exception e) {
            log.warn("简体转繁体失败: {}", e.getMessage());
            return text;
        }
    }

    /**
     * 繁体转简体
     *
     * @param text 繁体文本
     * @return 简体文本
     */
    public static String toSimplified(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        try {
            return ZhConverterUtil.toSimple(text);
        } catch (Exception e) {
            log.warn("繁体转简体失败: {}", e.getMessage());
            return text;
        }
    }

    /**
     * 判断是否包含繁体字
     *
     * @param text 文本
     * @return 是否包含繁体字
     */
    public static boolean containsTraditional(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        String simplified = toSimplified(text);
        return !text.equals(simplified);
    }

    /**
     * 判断是否包含简体字
     *
     * @param text 文本
     * @return 是否包含简体字
     */
    public static boolean containsSimplified(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        String traditional = toTraditional(text);
        return !text.equals(traditional);
    }
}
