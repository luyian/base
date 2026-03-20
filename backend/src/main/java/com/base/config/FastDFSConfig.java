package com.base.config;

import org.springframework.context.annotation.Configuration;

/**
 * FastDFS 配置类
 */
@Configuration
public class FastDFSConfig {

    /**
     * FastDFS tracker 服务器地址
     */
    private String trackerServers = "10.10.0.2:22122";

    /**
     * 连接超时时间（毫秒）
     */
    private int connectTimeout = 3000;

    /**
     * 网络超时时间（毫秒）
     */
    private int networkTimeout = 5000;

    /**
     * 字符集
     */
    private String charset = "UTF-8";

    /**
     * 是否启用 token 验证
     */
    private boolean antiStealToken = false;

    /**
     * 密钥
     */
    private String secretKey = "";

    /**
     * HTTP 访问端口
     */
    private int httpPort = 8888;

    public String getTrackerServers() {
        return trackerServers;
    }

    public void setTrackerServers(String trackerServers) {
        this.trackerServers = trackerServers;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getNetworkTimeout() {
        return networkTimeout;
    }

    public void setNetworkTimeout(int networkTimeout) {
        this.networkTimeout = networkTimeout;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public boolean isAntiStealToken() {
        return antiStealToken;
    }

    public void setAntiStealToken(boolean antiStealToken) {
        this.antiStealToken = antiStealToken;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public int getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
    }
}