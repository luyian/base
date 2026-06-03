package com.base.weather.provider;

/**
 * QPS 超限异常
 * <p>
 * 当天气数据源返回 QPS/频率限制错误时抛出，
 * 服务层可据此立即切换到降级数据源，避免继续浪费请求。
 * </p>
 */
public class QpsExceededException extends RuntimeException {

    public QpsExceededException(String providerName) {
        super(providerName + " QPS 超限");
    }

    public QpsExceededException(String providerName, String detail) {
        super(providerName + " QPS 超限: " + detail);
    }
}
