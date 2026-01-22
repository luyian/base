package com.base.system.service;

import com.base.system.dto.CaptchaResponse;
import com.base.system.dto.LoginRequest;
import com.base.system.dto.LoginResponse;
import com.base.system.dto.RouterVO;
import com.base.system.dto.UserInfoResponse;

import java.util.List;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 生成验证码
     *
     * @return 验证码响应
     */
    CaptchaResponse generateCaptcha();

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录响应
     */
    LoginResponse login(LoginRequest request);

    /**
     * 用户登出
     */
    void logout();

    /**
     * 刷新 Token
     *
     * @param token 旧 Token
     * @return 新 Token
     */
    String refreshToken(String token);

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    UserInfoResponse getUserInfo();

    /**
     * 获取当前用户的路由菜单
     *
     * @return 路由菜单列表
     */
    List<RouterVO> getRouters();
}
