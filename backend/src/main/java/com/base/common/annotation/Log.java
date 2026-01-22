package com.base.common.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 *
 * @author base
 * @since 2026-01-13
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

    /**
     * 操作模块
     */
    String title() default "";

    /**
     * 操作内容
     */
    String content() default "";

    /**
     * 操作类型
     */
    String type() default "OTHER";
}
