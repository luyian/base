package com.base.common.security;

import com.base.system.exception.BusinessException;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * SQL 注入防护工具类
 *
 * @author base
 * @since 2026-01-15
 */
public class SqlInjectionUtil {

    private SqlInjectionUtil() {
    }

    /**
     * SQL 注入关键字正则
     */
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
            "(?i)(\\b(select|insert|update|delete|drop|truncate|alter|create|grant|revoke|union|exec|execute|xp_|sp_|0x)\\b)" +
            "|(--)|(;)|(/\\*)" +
            "|(\\bor\\b.*=.*)" +
            "|(\\band\\b.*=.*)",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * 检查字符串是否包含 SQL 注入风险
     *
     * @param value 待检查的字符串
     * @return true-包含风险，false-安全
     */
    public static boolean hasSqlInjection(String value) {
        if (!StringUtils.hasText(value)) {
            return false;
        }
        return SQL_INJECTION_PATTERN.matcher(value).find();
    }

    /**
     * 校验参数是否安全，不安全则抛出异常
     *
     * @param value 待校验的参数
     * @param paramName 参数名称
     */
    public static void checkSqlInjection(String value, String paramName) {
        if (hasSqlInjection(value)) {
            throw new BusinessException("参数 " + paramName + " 包含非法字符");
        }
    }

    /**
     * 过滤 SQL 注入字符
     *
     * @param value 原始值
     * @return 过滤后的值
     */
    public static String filterSqlInjection(String value) {
        if (!StringUtils.hasText(value)) {
            return value;
        }
        // 移除危险字符
        return value.replaceAll("(?i)(select|insert|update|delete|drop|truncate|alter|create|grant|revoke|union|exec|execute)", "")
                .replaceAll("--", "")
                .replaceAll(";", "")
                .replaceAll("/\\*", "")
                .replaceAll("\\*/", "");
    }

    /**
     * 转义 SQL 特殊字符
     *
     * @param value 原始值
     * @return 转义后的值
     */
    public static String escapeSql(String value) {
        if (!StringUtils.hasText(value)) {
            return value;
        }
        return value.replace("'", "''")
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}
