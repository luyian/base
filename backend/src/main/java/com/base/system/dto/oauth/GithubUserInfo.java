package com.base.system.dto.oauth;

import lombok.Data;

import java.io.Serializable;

/**
 * GitHub 用户信息 DTO
 * 用于存储从 GitHub API 获取的用户信息，同时作为 Redis 临时缓存对象
 */
@Data
public class GithubUserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * GitHub 用户ID
     */
    private String id;

    /**
     * GitHub 用户名
     */
    private String login;

    /**
     * GitHub 昵称
     */
    private String name;

    /**
     * GitHub 头像
     */
    private String avatarUrl;

    /**
     * GitHub 邮箱
     */
    private String email;

    /**
     * access_token
     */
    private String accessToken;

    /**
     * 第三方平台类型
     */
    private String oauthType;
}
