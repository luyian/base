package com.base.common.aspect;

import com.base.common.annotation.RateLimit;
import com.base.common.exception.BusinessException;
import com.base.common.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * 接口限流切面
 * <p>
 * 基于 Redis 的计数器实现，拦截标注了 {@link RateLimit} 的方法，
 * 在指定时间窗口内超过最大调用次数则抛出业务异常。
 * </p>
 *
 * @author base
 * @since 2026-06-09
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class RateLimitAspect {

    private static final String RATE_LIMIT_KEY_PREFIX = "rate_limit:";

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 限流校验
     */
    @Before("@annotation(rateLimit)")
    public void doBefore(JoinPoint point, RateLimit rateLimit) {
        String redisKey = buildRedisKey(point, rateLimit);
        long period = rateLimit.period();
        TimeUnit timeUnit = rateLimit.timeUnit();
        int maxCount = rateLimit.maxCount();

        Long currentCount = redisTemplate.opsForValue().increment(redisKey, 1);
        if (currentCount == null) {
            return;
        }

        // 第一次访问时设置过期时间
        if (currentCount == 1) {
            redisTemplate.expire(redisKey, period, timeUnit);
        }

        if (currentCount > maxCount) {
            log.warn("接口限流触发: key={}, currentCount={}, maxCount={}", redisKey, currentCount, maxCount);
            throw new BusinessException(rateLimit.message());
        }
    }

    /**
     * 构建 Redis 限流 key
     * <p>
     * 格式: rate_limit:{自定义key或方法全限定名}:{用户标识}
     * </p>
     */
    private String buildRedisKey(JoinPoint point, RateLimit rateLimit) {
        StringBuilder key = new StringBuilder(RATE_LIMIT_KEY_PREFIX);

        // 确定业务 key
        if (rateLimit.key() != null && !rateLimit.key().isEmpty()) {
            key.append(rateLimit.key());
        } else {
            MethodSignature signature = (MethodSignature) point.getSignature();
            Method method = signature.getMethod();
            key.append(method.getDeclaringClass().getName())
                    .append(".")
                    .append(method.getName());
        }

        key.append(":");

        // 用户标识：优先使用用户ID，未登录时使用IP
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId != null) {
            key.append("user_").append(userId);
        } else {
            key.append("ip_").append(getClientIp());
        }

        return key.toString();
    }

    /**
     * 获取客户端 IP 地址
     */
    private String getClientIp() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return "unknown";
        }
        HttpServletRequest request = attributes.getRequest();
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多级代理时取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
