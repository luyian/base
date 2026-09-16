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
     * 绑定当前账号的微信（已登录用户）
     *
     * @param code  微信登录code
     * @param appId 小程序 appId（可为空，多小程序路由）
     */
    void bindWechatForCurrentUser(String code, String appId);

    /**
     * 解绑当前账号的微信
     */
    void unbindWechatForCurrentUser();

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
     *
     * @param appId 小程序 appId（可选；传入时 wxOpenid 仅反映该小程序的微信绑定状态，不传则返回任意绑定）
     */
    UserInfoResponse getUserInfo(String appId);

    /**
     * 获取当前用户的路由菜单
     */
    List<RouterVO> getRouters();
}
