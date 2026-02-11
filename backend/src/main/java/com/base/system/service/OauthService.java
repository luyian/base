package com.base.system.service;

import com.base.system.dto.LoginResponse;
import com.base.system.dto.oauth.*;
import com.base.system.entity.UserOauth;

import java.util.List;

/**
 * OAuth 第三方登录服务接口
 */
public interface OauthService {

    /**
     * 查询第三方登录是否启用
     *
     * @return 是否启用
     */
    boolean isOauthEnabled();

    /**
     * 获取 GitHub 授权 URL
     *
     * @return 授权 URL
     */
    String getGithubAuthUrl();

    /**
     * 处理 GitHub 回调（code 换 token，查询绑定关系）
     *
     * @param request 回调请求
     * @return 回调响应
     */
    OauthCallbackResponse handleGithubCallback(OauthCallbackRequest request);

    /**
     * 创建新账号并绑定
     *
     * @param request 绑定请求
     * @return 登录响应
     */
    LoginResponse bindNewUser(OauthBindNewRequest request);

    /**
     * 绑定已有账号
     *
     * @param request 绑定请求
     * @return 登录响应
     */
    LoginResponse bindExistUser(OauthBindExistRequest request);

    /**
     * 查询用户的第三方绑定列表
     *
     * @param userId 用户ID
     * @return 绑定列表
     */
    List<UserOauth> listUserOauthBindings(Long userId);

    /**
     * 解绑第三方账号
     *
     * @param userId    用户ID
     * @param oauthType 第三方平台类型
     */
    void unbind(Long userId, String oauthType);
}
