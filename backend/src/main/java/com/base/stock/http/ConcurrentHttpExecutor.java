package com.base.stock.http;

import com.base.common.util.HttpClientUtil;
import com.base.stock.entity.ApiToken;
import com.base.stock.service.TokenManagerService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 并发HTTP执行器
 * 管理Token池和线程池，提供并发HTTP请求能力
 *
 * @author base
 */
@Slf4j
public class ConcurrentHttpExecutor {

    /**
     * 服务商标识
     */
    private final String provider;

    /**
     * Token管理服务
     */
    private final TokenManagerService tokenManagerService;

    /**
     * Token池（线程安全的阻塞队列）
     */
    private final BlockingQueue<ApiToken> tokenPool;

    /**
     * 线程池
     */
    private final ExecutorService executorService;

    /**
     * 每个线程需要的Token数量
     */
    private final int tokensPerThread;

    /**
     * 最大线程数
     */
    private final int maxThreads;

    /**
     * 获取Token的超时时间（毫秒）
     */
    private final long tokenAcquireTimeout;

    /**
     * 是否已关闭
     */
    private volatile boolean shutdown = false;

    /**
     * 构造函数
     *
     * @param provider            服务商标识
     * @param tokenManagerService Token管理服务
     * @param tokensPerThread     每个线程需要的Token数量
     * @param maxThreads          最大线程数
     * @param tokenAcquireTimeout 获取Token的超时时间（毫秒）
     */
    public ConcurrentHttpExecutor(String provider,
                                   TokenManagerService tokenManagerService,
                                   int tokensPerThread,
                                   int maxThreads,
                                   long tokenAcquireTimeout) {
        this.provider = provider;
        this.tokenManagerService = tokenManagerService;
        this.tokensPerThread = tokensPerThread;
        this.maxThreads = maxThreads;
        this.tokenAcquireTimeout = tokenAcquireTimeout;

        // 初始化Token池
        this.tokenPool = new LinkedBlockingQueue<>();
        refreshTokenPool();

        // 计算线程数并创建线程池
        int threadCount = calculateThreadCount();
        this.executorService = createThreadPool(threadCount);

        log.info("并发HTTP执行器初始化完成，provider: {}, tokenCount: {}, threadCount: {}",
                provider, tokenPool.size(), threadCount);
    }

    /**
     * 刷新Token池
     */
    public void refreshTokenPool() {
        tokenPool.clear();
        List<ApiToken> tokens = tokenManagerService.getAvailableTokens(provider);
        tokenPool.addAll(tokens);
        log.info("Token池已刷新，可用Token数量: {}", tokenPool.size());
    }

    /**
     * 计算线程数
     * 规则：线程数 = Token数 / tokensPerThread，最小1，最大maxThreads
     *
     * @return 线程数
     */
    private int calculateThreadCount() {
        int tokenCount = tokenPool.size();
        if (tokenCount == 0) {
            return 1;
        }
        int threadCount = tokenCount / tokensPerThread;
        return Math.max(1, Math.min(threadCount, maxThreads));
    }

    /**
     * 创建线程池
     *
     * @param threadCount 线程数
     * @return 线程池
     */
    private ExecutorService createThreadPool(int threadCount) {
        AtomicInteger threadNumber = new AtomicInteger(1);
        ThreadFactory threadFactory = r -> {
            Thread t = new Thread(r);
            t.setName("concurrent-http-" + provider + "-" + threadNumber.getAndIncrement());
            t.setDaemon(true);
            return t;
        };

        return new ThreadPoolExecutor(
                threadCount,
                threadCount,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000),
                threadFactory,
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    /**
     * 同步执行单个请求
     *
     * @param request 请求对象
     * @return 响应对象
     */
    public ConcurrentHttpResponse execute(ConcurrentHttpRequest request) {
        if (shutdown) {
            return ConcurrentHttpResponse.fail("执行器已关闭", request.getBizId());
        }

        ApiToken token = null;
        try {
            // 从Token池获取Token（阻塞等待）
            token = tokenPool.poll(tokenAcquireTimeout, TimeUnit.MILLISECONDS);
            if (token == null) {
                log.warn("获取Token超时，bizId: {}", request.getBizId());
                return ConcurrentHttpResponse.fail("获取Token超时", request.getBizId());
            }

            // 执行HTTP请求
            String response = doExecute(request, token);

            // 检查是否为Token错误
            if (isTokenError(response)) {
                log.error("Token认证失败，tokenId: {}, bizId: {}", token.getId(), request.getBizId());
                tokenManagerService.recordTokenFailure(token.getId());
                // Token错误不放回池中
                return ConcurrentHttpResponse.tokenError("Token认证失败", request.getBizId());
            }

            // 请求成功，重置失败计数并放回Token池
            tokenManagerService.resetTokenFailure(token.getId());
            tokenPool.offer(token);

            return ConcurrentHttpResponse.success(response, request.getBizId());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取Token被中断，bizId: {}", request.getBizId());
            return ConcurrentHttpResponse.fail("获取Token被中断", request.getBizId());

        } catch (Exception e) {
            log.error("请求执行失败，bizId: {}, error: {}", request.getBizId(), e.getMessage());
            if (token != null) {
                // 非Token错误，记录失败但放回Token池
                tokenManagerService.recordTokenFailure(token.getId());
                // 检查Token是否还可用（失败次数未超限）
                ApiToken refreshedToken = tokenManagerService.getTokenById(token.getId());
                if (refreshedToken != null && refreshedToken.getStatus() == 1) {
                    tokenPool.offer(token);
                }
            }
            return ConcurrentHttpResponse.fail(e.getMessage(), request.getBizId());
        }
    }

    /**
     * 异步执行单个请求
     *
     * @param request 请求对象
     * @return Future对象
     */
    public Future<ConcurrentHttpResponse> executeAsync(ConcurrentHttpRequest request) {
        return executorService.submit(() -> execute(request));
    }

    /**
     * 批量异步执行请求
     *
     * @param requests 请求列表
     * @return Future列表
     */
    public List<Future<ConcurrentHttpResponse>> executeBatch(List<ConcurrentHttpRequest> requests) {
        List<Future<ConcurrentHttpResponse>> futures = new ArrayList<>();
        for (ConcurrentHttpRequest request : requests) {
            futures.add(executeAsync(request));
        }
        return futures;
    }

    /**
     * 批量执行请求并等待全部完成
     *
     * @param requests 请求列表
     * @param timeout  超时时间（毫秒）
     * @return 响应列表
     */
    public List<ConcurrentHttpResponse> executeBatchAndWait(List<ConcurrentHttpRequest> requests, long timeout) {
        List<Future<ConcurrentHttpResponse>> futures = executeBatch(requests);
        List<ConcurrentHttpResponse> responses = new ArrayList<>();

        long deadline = System.currentTimeMillis() + timeout;

        for (Future<ConcurrentHttpResponse> future : futures) {
            try {
                long remaining = deadline - System.currentTimeMillis();
                if (remaining <= 0) {
                    responses.add(ConcurrentHttpResponse.fail("批量执行超时", null));
                    continue;
                }
                responses.add(future.get(remaining, TimeUnit.MILLISECONDS));
            } catch (TimeoutException e) {
                responses.add(ConcurrentHttpResponse.fail("请求超时", null));
            } catch (Exception e) {
                responses.add(ConcurrentHttpResponse.fail(e.getMessage(), null));
            }
        }

        return responses;
    }

    /**
     * 执行HTTP请求
     *
     * @param request 请求对象
     * @param token   Token
     * @return 响应内容
     */
    private String doExecute(ConcurrentHttpRequest request, ApiToken token) {
        Map<String, String> headers = buildHeaders(request.getHeaders(), token.getTokenValue());

        if ("POST".equalsIgnoreCase(request.getMethod())) {
            return HttpClientUtil.post(request.getUrl(), request.getBody(), headers, request.getTimeout());
        } else {
            return HttpClientUtil.get(request.getUrl(), headers, request.getTimeout());
        }
    }

    /**
     * 构建请求头
     *
     * @param customHeaders 自定义请求头
     * @param tokenValue    Token值
     * @return 完整请求头
     */
    private Map<String, String> buildHeaders(Map<String, String> customHeaders, String tokenValue) {
        Map<String, String> headers = new HashMap<>();
        headers.put("accept", "application/json");
        headers.put("Content-Type", "application/json");
        headers.put("token", tokenValue);

        if (customHeaders != null) {
            headers.putAll(customHeaders);
        }

        return headers;
    }

    /**
     * 判断是否为Token相关错误
     *
     * @param response 响应内容
     * @return 是否为Token错误
     */
    private boolean isTokenError(String response) {
        if (response == null) {
            return false;
    }
        return response.contains("\"code\":1") &&
                (response.contains("token") || response.contains("Token") ||
                        response.contains("unauthorized") || response.contains("Unauthorized") ||
                        response.contains("authentication") || response.contains("Authentication"));
    }

    /**
     * 获取当前Token池大小
     *
     * @return Token池大小
     */
    public int getTokenPoolSize() {
        return tokenPool.size();
    }

    /**
     * 关闭执行器
     */
    @PreDestroy
    public void shutdown() {
        shutdown = true;
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        log.info("并发HTTP执行器已关闭，provider: {}", provider);
    }
}
