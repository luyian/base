package com.base.ai.image.dedicated;

import com.alibaba.fastjson2.JSONObject;
import com.base.common.exception.BusinessException;
import com.base.common.result.ResultCode;
import com.base.common.util.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AI 作图专用商汤日日新 SenseNova 适配器。
 *
 * @author base
 */
@Slf4j
@Order(15)
@Component
public class DedicatedSenseNovaImageAdapter extends AbstractDedicatedAiImageAdapter implements DisposableBean {

    private static final int MAX_CONCURRENT_REQUESTS = 8;

    private static final int THREAD_KEEP_ALIVE_SECONDS = 60;

    private static final int REQUEST_QUEUE_CAPACITY = 16;

    private static final String DEFAULT_BASE_URL = "https://token.sensenova.cn/v1";

    private static final String DEFAULT_MODEL = "sensenova-u1-fast";

    private static final String CHAT_COMPLETIONS_PATH = "/chat/completions";

    private static final String IMAGES_GENERATIONS_PATH = "/images/generations";

    private static final String DEFAULT_SIZE = "2048x2048";

    private static final Pattern SIZE_PATTERN = Pattern.compile("(\\d{3,4})\\s*[xX*×]\\s*(\\d{3,4})");

    private static final Set<String> ALLOWED_SIZES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "1664x2496", "2496x1664", "1760x2368", "2368x1760", "1824x2272", "2272x1824",
            "2048x2048", "2752x1536", "1536x2752", "3072x1376", "1344x3136", "2560x720", "3072x864"
    )));

    private final ThreadPoolExecutor imageExecutor = buildImageExecutor();

    @Override
    public boolean supports(DedicatedAiImageContext context) {
        if (StringUtils.hasText(context.getAdapter())) {
            return "sensenova".equalsIgnoreCase(context.getAdapter())
                    || "sense-nova".equalsIgnoreCase(context.getAdapter());
        }
        String baseUrl = context.getBaseUrl() == null ? "" : context.getBaseUrl().toLowerCase();
        return baseUrl.contains("sensenova");
    }

    @Override
    public DedicatedAiImageResult generate(DedicatedAiImageContext context) {
        String size = resolveSize(context.getSize());
        String model = resolveModel(context);
        String url = buildImagesUrl(context.getBaseUrl());
        int requestCount = resolveRequestCount(context);
        log.info("AI 作图专用请求开始，adapter: {}，model: {}，requestSize: {}，apiSize: {}，count: {}，url: {}",
                getName(), model, context.getSize(), size, requestCount, url);
        if (requestCount > 1) {
            log.info("商汤图片模型单次仅允许 n=1，已拆分为 {} 次并发请求", requestCount);
        }

        DedicatedAiImageResult aggregateResult = buildBaseResult(context);
        aggregateResult.setModel(model);
        aggregateResult.setSize(size);
        aggregateResult.setCount(requestCount);
        List<Future<DedicatedAiImageResult>> futures = submitImageTasks(context, model, size, url, requestCount);
        mergeFutureResults(aggregateResult, futures);
        return aggregateResult;
    }

    @Override
    public String getName() {
        return "sensenova";
    }

    @Override
    public void destroy() {
        imageExecutor.shutdown();
    }

    private String resolveModel(DedicatedAiImageContext context) {
        if (StringUtils.hasText(context.getModel())) {
            return context.getModel().trim();
        }
        return DEFAULT_MODEL;
    }

    private int resolveRequestCount(DedicatedAiImageContext context) {
        if (context.getCount() == null) {
            return 1;
        }
        return Math.max(1, context.getCount());
    }

    private Map<String, Object> buildRequestBody(DedicatedAiImageContext context, String model, String size,
                                                 int index) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", model);
        body.put("prompt", context.getPrompt());
        body.put("n", 1);
        body.put("size", size);
        if (context.getSeed() != null) {
            body.put("seed", resolveSeed(context.getSeed(), index));
        }
        return body;
    }

    private int resolveSeed(int seed, int index) {
        if (index <= 0) {
            return seed;
        }
        long value = (long) seed + index;
        return value > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) value;
    }

    private List<Future<DedicatedAiImageResult>> submitImageTasks(DedicatedAiImageContext context, String model,
                                                                  String size, String url, int requestCount) {
        List<Future<DedicatedAiImageResult>> futures = new ArrayList<>(requestCount);
        for (int i = 0; i < requestCount; i++) {
            final int index = i;
            futures.add(imageExecutor.submit(buildImageTask(context, model, size, url, index)));
        }
        return futures;
    }

    private Callable<DedicatedAiImageResult> buildImageTask(DedicatedAiImageContext context, String model, String size,
                                                           String url, int index) {
        return () -> {
            Map<String, Object> body = buildRequestBody(context, model, size, index);
            String response = postImageRequest(url, body, context);
            return parseResponse(context, response);
        };
    }

    private void mergeFutureResults(DedicatedAiImageResult aggregateResult,
                                    List<Future<DedicatedAiImageResult>> futures) {
        for (Future<DedicatedAiImageResult> future : futures) {
            try {
                mergeImages(aggregateResult, future.get());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new BusinessException(ResultCode.AI_SERVICE_UNAVAILABLE.getCode(), "商汤图片生成并发任务被中断");
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                if (cause instanceof BusinessException) {
                    throw (BusinessException) cause;
                }
                String message = cause == null ? e.getMessage() : cause.getMessage();
                throw new BusinessException(ResultCode.AI_SERVICE_UNAVAILABLE.getCode(),
                        "商汤图片生成并发任务失败：" + message);
            }
        }
    }

    private String resolveSize(String size) {
        if (!StringUtils.hasText(size)) {
            return DEFAULT_SIZE;
        }
        String normalized = size.trim().toLowerCase().replace('*', 'x');
        if (ALLOWED_SIZES.contains(normalized)) {
            return normalized;
        }
        String mapped = mapToAllowedSize(normalized);
        log.info("商汤图片尺寸 {} 不在支持列表，已映射为 {}", size, mapped);
        return mapped;
    }

    private String mapToAllowedSize(String size) {
        Matcher matcher = SIZE_PATTERN.matcher(size);
        if (!matcher.find()) {
            return DEFAULT_SIZE;
        }
        int width = Integer.parseInt(matcher.group(1));
        int height = Integer.parseInt(matcher.group(2));
        if (height == 0) {
            return DEFAULT_SIZE;
        }
        double ratio = (double) width / height;
        String best = DEFAULT_SIZE;
        double bestDiff = Double.MAX_VALUE;
        for (String allowed : ALLOWED_SIZES) {
            String[] parts = allowed.split("x");
            double allowedRatio = Double.parseDouble(parts[0]) / Double.parseDouble(parts[1]);
            double diff = Math.abs(allowedRatio - ratio);
            if (diff < bestDiff) {
                bestDiff = diff;
                best = allowed;
            }
        }
        return best;
    }

    private String postImageRequest(String url, Map<String, Object> body, DedicatedAiImageContext context) {
        try {
            return HttpClientUtil.post(url, body, buildHeaders(context), imageTimeout(context));
        } catch (RuntimeException e) {
            String message = e.getMessage() == null ? "" : e.getMessage();
            if (message.contains("401") || message.contains("403")) {
                throw new BusinessException(ResultCode.AI_SERVICE_UNAVAILABLE.getCode(),
                        "商汤图片生成鉴权失败，请确认 API Key 来自 Token Plan 控制台，"
                                + "且基础地址为 https://token.sensenova.cn/v1。原始错误：" + message);
            }
            if (message.contains("400") && message.contains("size")) {
                throw new BusinessException(ResultCode.PARAM_ERROR.getCode(),
                        "商汤图片尺寸不受支持，请使用 2048x2048、2752x1536、1536x2752 等官方尺寸。"
                                + "原始错误：" + message);
            }
            throw new BusinessException(ResultCode.AI_SERVICE_UNAVAILABLE.getCode(),
                    "商汤图片生成请求失败，请确认模型名称和接口地址可用，模型："
                            + body.get("model") + "，原因：" + message);
        }
    }

    private DedicatedAiImageResult parseResponse(DedicatedAiImageContext context, String response) {
        JSONObject resultObject = JSONObject.parseObject(response);
        assertNoProviderError(resultObject);

        DedicatedAiImageResult result = buildBaseResult(context);
        fillImagesFromJson(result, resultObject.get("data"));
        fillImagesFromJson(result, resultObject.get("output"));
        fillImagesFromJson(result, resultObject.get("images"));
        if (!hasImages(result)) {
            fillImagesFromJson(result, resultObject);
        }
        if (!hasImages(result)) {
            throw new BusinessException(ResultCode.AI_SERVICE_UNAVAILABLE.getCode(),
                    "商汤图片生成接口未返回可展示图片，响应摘要：" + abbreviate(response));
        }
        return result;
    }

    private void mergeImages(DedicatedAiImageResult aggregateResult, DedicatedAiImageResult singleResult) {
        if (singleResult.getImages() == null || singleResult.getImages().isEmpty()) {
            return;
        }
        for (DedicatedAiImageItem item : singleResult.getImages()) {
            if (aggregateResult.getImages().size() >= aggregateResult.getCount()) {
                return;
            }
            item.setIndex(aggregateResult.getImages().size() + 1);
            aggregateResult.getImages().add(item);
        }
        if (StringUtils.hasText(singleResult.getRawText())) {
            aggregateResult.setRawText(singleResult.getRawText());
        }
    }

    private String buildImagesUrl(String baseUrl) {
        String url = baseUrl == null ? "" : baseUrl.trim();
        if (!StringUtils.hasText(url)) {
            return DEFAULT_BASE_URL + IMAGES_GENERATIONS_PATH;
        }
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        if (url.endsWith(IMAGES_GENERATIONS_PATH)) {
            return url;
        }
        if (url.endsWith(CHAT_COMPLETIONS_PATH)) {
            return url.substring(0, url.length() - CHAT_COMPLETIONS_PATH.length()) + IMAGES_GENERATIONS_PATH;
        }
        if (url.endsWith("/v1")) {
            return url + IMAGES_GENERATIONS_PATH;
        }
        return url + "/v1" + IMAGES_GENERATIONS_PATH;
    }

    private ThreadPoolExecutor buildImageExecutor() {
        ThreadFactory threadFactory = new ThreadFactory() {
            private final AtomicInteger index = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable);
                thread.setName("ai-image-sensenova-" + index.getAndIncrement());
                thread.setDaemon(true);
                return thread;
            }
        };
        return new ThreadPoolExecutor(
                MAX_CONCURRENT_REQUESTS,
                MAX_CONCURRENT_REQUESTS,
                THREAD_KEEP_ALIVE_SECONDS,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(REQUEST_QUEUE_CAPACITY),
                threadFactory,
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}
