package com.base.common.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 接口限流注解
 * <p>
 * 基于 Redis 实现，在指定时间窗口内限制接口调用次数。
 * 默认同一用户对同一接口 5 秒内只能调用 1 次。
 * </p>
 *
 * @author base
 * @since 2026-06-09
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    /**
     * 限流 key 前缀，默认使用方法全限定名
     */
    String key() default "";

    /**
     * 时间窗口内最大调用次数
     */
    int maxCount() default 1;

    /**
     * 时间窗口大小，默认 5
     */
    long period() default 5;

    /**
     * 时间单位，默认秒
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 限流提示消息
     */
    String message() default "操作过于频繁，请稍后再试";
}
