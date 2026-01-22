package com.base.common.result;

import lombok.Getter;

/**
 * 响应状态码枚举
 */
@Getter
public enum ResultCode {

    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 失败
     */
    ERROR(500, "操作失败"),

    /**
     * 参数错误
     */
    PARAM_ERROR(400, "参数错误"),

    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权"),

    /**
     * 禁止访问
     */
    FORBIDDEN(403, "禁止访问"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 业务异常
     */
    BUSINESS_ERROR(600, "业务异常"),

    /**
     * 数据不存在
     */
    DATA_NOT_FOUND(404, "数据不存在"),

    /**
     * 操作不允许
     */
    OPERATION_NOT_ALLOWED(403, "操作不允许"),

    /**
     * 用户不存在
     */
    USER_NOT_FOUND(404, "用户不存在"),

    /**
     * 账号已锁定
     */
    ACCOUNT_LOCKED(423, "账号已锁定"),

    /**
     * 账号已禁用
     */
    ACCOUNT_DISABLED(423, "账号已禁用"),

    /**
     * 验证码已过期
     */
    CAPTCHA_EXPIRED(400, "验证码已过期"),

    /**
     * 验证码错误
     */
    CAPTCHA_ERROR(400, "验证码错误"),

    /**
     * 用户名或密码错误
     */
    USERNAME_OR_PASSWORD_ERROR(401, "用户名或密码错误"),

    /**
     * Token无效
     */
    TOKEN_INVALID(401, "Token无效"),

    /**
     * 角色不存在
     */
    ROLE_NOT_FOUND(404, "角色不存在"),

    /**
     * 角色编码已存在
     */
    ROLE_CODE_ALREADY_EXISTS(400, "角色编码已存在"),

    /**
     * 数据正在使用中
     */
    DATA_IN_USE(400, "数据正在使用中"),

    /**
     * 用户名已存在
     */
    USERNAME_ALREADY_EXISTS(400, "用户名已存在"),

    /**
     * 数据不存在
     */
    DATA_NOT_EXIST(404, "数据不存在"),

    /**
     * 数据已存在
     */
    DATA_EXIST(400, "数据已存在"),

    /**
     * 方法不允许
     */
    METHOD_NOT_ALLOWED(405, "方法不允许"),

    /**
     * 内部服务器错误
     */
    INTERNAL_SERVER_ERROR(500, "内部服务器错误");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
