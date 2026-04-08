package com.base.open.context;

/**
 * 开放接口请求上下文
 * 通过 ThreadLocal 存储当前请求的应用信息
 *
 * @author base
 */
public class OpenApiContext {

    private static final ThreadLocal<String> CURRENT_APP_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> CURRENT_APP_NAME = new ThreadLocal<>();

    /**
     * 设置当前应用信息
     *
     * @param appId   应用ID
     * @param appName 应用名称
     */
    public static void set(String appId, String appName) {
        CURRENT_APP_ID.set(appId);
        CURRENT_APP_NAME.set(appName);
    }

    /**
     * 获取当前应用ID
     */
    public static String getAppId() {
        return CURRENT_APP_ID.get();
    }

    /**
     * 获取当前应用名称
     */
    public static String getAppName() {
        return CURRENT_APP_NAME.get();
    }

    /**
     * 清除当前应用信息
     */
    public static void clear() {
        CURRENT_APP_ID.remove();
        CURRENT_APP_NAME.remove();
    }
}
