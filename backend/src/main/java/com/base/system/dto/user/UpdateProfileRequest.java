package com.base.system.dto.user;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 更新个人信息请求
 *
 * @author base
 * @since 2026-01-13
 */
@Data
public class UpdateProfileRequest {

    /**
     * 姓名
     */
    @NotBlank(message = "姓名不能为空")
    private String name;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 性别（0：女，1：男）
     */
    private Integer gender;

    /**
     * 头像
     */
    private String avatar;
}
