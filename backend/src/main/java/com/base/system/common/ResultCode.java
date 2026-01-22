package com.base.system.common;

import lombok.Getter;

/**
 * 响应码枚举
 *
 * @author base
 * @since 2026-01-12
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
    UNAUTHORIZED(401, "未授权，请先登录"),

    /**
     * 无权限
     */
    FORBIDDEN(403, "无权限访问"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 请求方法不支持
     */
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),

    /**
     * 请求超时
     */
    REQUEST_TIMEOUT(408, "请求超时"),

    /**
     * 系统内部错误
     */
    INTERNAL_SERVER_ERROR(500, "系统内部错误"),

    /**
     * 服务不可用
     */
    SERVICE_UNAVAILABLE(503, "服务不可用"),

    /**
     * 用户名或密码错误
     */
    LOGIN_ERROR(1001, "用户名或密码错误"),

    /**
     * 验证码错误
     */
    CAPTCHA_ERROR(1002, "验证码错误"),

    /**
     * 账号已被禁用
     */
    ACCOUNT_DISABLED(1003, "账号已被禁用"),

    /**
     * 账号已被锁定
     */
    ACCOUNT_LOCKED(1004, "账号已被锁定，请稍后再试"),

    /**
     * Token 无效
     */
    TOKEN_INVALID(1005, "Token 无效"),

    /**
     * Token 已过期
     */
    TOKEN_EXPIRED(1006, "Token 已过期，请重新登录"),

    /**
     * 用户不存在
     */
    USER_NOT_EXIST(1007, "用户不存在"),

    /**
     * 用户已存在
     */
    USER_EXIST(1008, "用户已存在"),

    /**
     * 原密码错误
     */
    OLD_PASSWORD_ERROR(1009, "原密码错误"),

    /**
     * 用户不存在
     */
    USER_NOT_FOUND(1010, "用户不存在"),

    /**
     * 用户名已存在
     */
    USERNAME_ALREADY_EXISTS(1011, "用户名已存在"),

    /**
     * 操作不允许
     */
    OPERATION_NOT_ALLOWED(1012, "操作不允许"),

    /**
     * 角色不存在
     */
    ROLE_NOT_FOUND(1013, "角色不存在"),

    /**
     * 角色编码已存在
     */
    ROLE_CODE_ALREADY_EXISTS(1014, "角色编码已存在"),

    /**
     * 数据已存在
     */
    DATA_EXIST(2001, "数据已存在"),

    /**
     * 数据不存在
     */
    DATA_NOT_EXIST(2002, "数据不存在"),

    /**
     * 数据正在使用中
     */
    DATA_IN_USE(2003, "数据正在使用中，无法删除"),

    /**
     * 文件上传失败
     */
    FILE_UPLOAD_ERROR(3001, "文件上传失败"),

    /**
     * 文件类型不支持
     */
    FILE_TYPE_ERROR(3002, "文件类型不支持"),

    /**
     * 文件大小超出限制
     */
    FILE_SIZE_ERROR(3003, "文件大小超出限制");

    /**
     * 响应码
     */
    private final Integer code;

    /**
     * 响应消息
     */
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
