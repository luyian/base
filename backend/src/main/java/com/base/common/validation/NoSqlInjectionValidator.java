package com.base.common.validation;

import com.base.common.security.SqlInjectionUtil;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * SQL 注入校验器
 *
 * @author base
 * @since 2026-01-15
 */
public class NoSqlInjectionValidator implements ConstraintValidator<NoSqlInjection, String> {

    @Override
    public void initialize(NoSqlInjection constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        return !SqlInjectionUtil.hasSqlInjection(value);
    }
}
