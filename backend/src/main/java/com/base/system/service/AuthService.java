package com.base.system.service;

import com.base.system.dto.*;
import java.util.List;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 生成验证码
     */
    CaptchaResponse generateCaptcha();

    /**
     * 用户登录（账号密码）
     */
    LoginResponse login(LoginRequest request);

    /**
     * 微信小程序登录
     */
    LoginResponse wxLogin(WxLoginRequest request);

    /**
     * 用户注册
     */
    LoginResponse register(RegisterRequest request);

    /**
     * 微信绑定（已有账号，绑定微信）
     */
    LoginResponse bindWechat(WxBindRequest request);

    /**
     * 用户登出
     */
    void logout();

    /**
     * 刷新 Token
     */
    String refreshToken(String token);

    /**
     * 获取当前用户信息
     */
    UserInfoResponse getUserInfo();

    /**
     * 获取当前用户的路由菜单
     */
    List<RouterVO> getRouters();
}
