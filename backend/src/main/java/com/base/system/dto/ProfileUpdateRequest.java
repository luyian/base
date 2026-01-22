package com.base.system.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 个人信息修改请求
 *
 * @author base
 * @since 2026-01-13
 */
@Data
public class ProfileUpdateRequest {

    @Size(min = 2, max = 20, message = "昵称长度必须在2-20个字符之间")
    private String nickname;

    @Size(max = 20, message = "真实姓名长度不能超过20个字符")
    private String realName;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    private Integer gender;

    private String avatar;
}
