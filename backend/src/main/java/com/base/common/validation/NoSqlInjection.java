package com.base.common.validation;

import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.*;

/**
 * SQL 注入校验注解
 *
 * @author base
 * @since 2026-01-15
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = NoSqlInjectionValidator.class)
public @interface NoSqlInjection {

    String message() default "参数包含非法字符";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
