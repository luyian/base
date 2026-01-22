package com.base.common.aspect;

/**
 * 数据权限上下文
 *
 * @author base
 * @since 2026-01-13
 */
public class DataScopeContextHolder {

    private static final ThreadLocal<String> DATA_SCOPE = new ThreadLocal<>();

    /**
     * 设置数据权限SQL
     */
    public static void setDataScope(String sql) {
        DATA_SCOPE.set(sql);
    }

    /**
     * 获取数据权限SQL
     */
    public static String getDataScope() {
        return DATA_SCOPE.get();
    }

    /**
     * 清除数据权限SQL
     */
    public static void clearDataScope() {
        DATA_SCOPE.remove();
    }
}
