package com.base.ai.skill;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.base.ai.config.AiSkillConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Python 工具服务 HTTP 客户端 — 替代原有的 PythonExecutor 进程调用，
 * 通过 HTTP 请求调用 python-tools 服务获取股票数据。
 *
 * @author base
 * @since 2026-06-10
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PythonToolsClient {

    private final AiSkillConfig skillConfig;

    private RestTemplate restTemplate;

    /** 并发控制：最多同时发起 5 个请求 */
    private static final int MAX_CONCURRENT = 5;
    private final Semaphore concurrencySemaphore = new Semaphore(MAX_CONCURRENT);

    @PostConstruct
    public void init() {
        restTemplate = new RestTemplate();
        log.info("Python 工具服务 HTTP 客户端初始化完成，baseUrl: {}", skillConfig.getPythonToolsUrl());
    }

    /**
     * 调用 python-tools 服务接口
     *
     * @param path 接口路径（如 /api/stock/quote?codes=600519）
     * @return JSON 格式响应中的 data 字段内容
     */
    public String call(String path) {
        boolean acquired = false;
        try {
            acquired = concurrencySemaphore.tryAcquire(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "{\"error\": \"系统繁忙，请稍后重试\"}";
        }
        if (!acquired) {
            return "{\"error\": \"当前查询较多，请稍后重试\"}";
        }

        try {
            String url = skillConfig.getPythonToolsUrl() + path;
            log.debug("调用 python-tools: {}", url);

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String body = response.getBody();
            if (body == null || body.isEmpty()) {
                return "{\"error\": \"数据查询无结果\"}";
            }

            // 解析统一响应格式
            JSONObject result = JSON.parseObject(body);
            int code = result.getIntValue("code");
            if (code != 200) {
                String message = result.getString("message");
                return "{\"error\": \"" + (message != null ? message : "数据获取失败") + "\"}";
            }

            // 返回 data 字段的 JSON 字符串
            Object data = result.get("data");
            if (data == null) {
                return "{\"error\": \"数据查询无结果\"}";
            }
            return JSON.toJSONString(data);

        } catch (Exception e) {
            log.warn("调用 python-tools 失败: path={}, error={}", path, e.getMessage());
            return "{\"error\": \"数据查询异常，请稍后重试\"}";
        } finally {
            concurrencySemaphore.release();
        }
    }
}
