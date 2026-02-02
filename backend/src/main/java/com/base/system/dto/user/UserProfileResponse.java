package com.base.system.dto.user;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户个人信息响应
 *
 * @author base
 * @since 2026-01-13
 */
@Data
public class UserProfileResponse {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 性别（0：女，1：男）
     */
    private Integer gender;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 部门ID
     */
    private Long departmentId;

    /**
     * 部门名称
     */
    private String departmentName;

    /**
     * 角色列表
     */
    private String roles;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;
}
