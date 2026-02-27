package com.base.system.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.base.common.exception.BusinessException;
import com.base.common.feishu.config.FeishuConfig;
import com.base.common.feishu.dto.FeishuBindRequest;
import com.base.common.feishu.dto.FeishuSendMessageRequest;
import com.base.common.feishu.service.FeishuMessageService;
import com.base.common.feishu.service.FeishuTokenService;
import com.base.common.result.Result;
import com.base.common.util.HttpClientUtil;
import com.base.common.util.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.base.system.dto.oauth.OauthCallbackRequest;
import com.base.system.entity.UserOauth;
import com.base.system.mapper.UserOauthMapper;
import com.base.system.util.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 飞书消息控制器
 * 提供飞书账号绑定/解绑、消息发送、文件上传等接口
 *
 * @author base
 * @since 2026-02-11
 */
@Slf4j
@RestController
@RequestMapping("/system/feishu")
@Api(tags = "飞书消息管理")
public class FeishuController {

    /**
     * 飞书绑定类型标识
     */
    private static final String OAUTH_TYPE_FEISHU = "feishu";

    /**
     * 飞书 OAuth state 前缀
     */
    private static final String FEISHU_STATE_PREFIX = "feishu:oauth:state:";

    /**
     * state 过期时间（秒）
     */
    private static final long STATE_EXPIRE_SECONDS = 180;

    @Autowired
    private FeishuMessageService feishuMessageService;

    @Autowired
    private UserOauthMapper userOauthMapper;

    @Autowired
    private FeishuConfig feishuConfig;

    @Autowired
    private FeishuTokenService feishuTokenService;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 获取飞书 OAuth 授权地址
     */
    @GetMapping("/oauth/url")
    @ApiOperation("获取飞书OAuth授权地址")
    public Result<String> getFeishuAuthUrl() {
        if (!Boolean.TRUE.equals(feishuConfig.getEnabled())) {
            return Result.error("飞书功能未启用");
        }
        // 生成随机 state 防止 CSRF
        String state = UUID.randomUUID().toString().replace("-", "");
        redisUtil.set(FEISHU_STATE_PREFIX + state, "1", STATE_EXPIRE_SECONDS);

        String redirectUri;
        try {
            redirectUri = URLEncoder.encode(feishuConfig.getRedirectUri(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new BusinessException("URL 编码失败");
        }
        String url = "https://open.feishu.cn/open-apis/authen/v1/authorize"
                + "?app_id=" + feishuConfig.getAppId()
                + "&redirect_uri=" + redirectUri
                + "&state=" + state;

        log.info("生成飞书授权 URL，state: {}", state);
        return Result.success(url);
    }

    /**
     * 飞书 OAuth 回调处理
     * 用 code 换 user_access_token，获取用户信息并绑定到当前登录用户
     */
    @PostMapping("/oauth/callback")
    @ApiOperation("飞书OAuth回调处理")
    public Result<Void> handleFeishuCallback(@Validated @RequestBody OauthCallbackRequest request) {
        if (!Boolean.TRUE.equals(feishuConfig.getEnabled())) {
            return Result.error("飞书功能未启用");
        }
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.error("未获取到当前用户信息");
        }

        // 1. 校验 state
        String stateKey = FEISHU_STATE_PREFIX + request.getState();
        Object stateValue = redisUtil.get(stateKey);
        if (stateValue == null) {
            throw new BusinessException("无效的 state 参数或已过期");
        }
        redisUtil.delete(stateKey);

        // 2. 用 code 换 user_access_token
        String appAccessToken = feishuTokenService.getTenantAccessToken();
        Map<String, Object> tokenBody = new HashMap<>(4);
        tokenBody.put("grant_type", "authorization_code");
        tokenBody.put("code", request.getCode());

        Map<String, String> tokenHeaders = new HashMap<>(4);
        tokenHeaders.put("Authorization", "Bearer " + appAccessToken);

        String tokenUrl = feishuConfig.getBaseUrl() + "/authen/v1/oidc/access_token";
        String tokenResponse = HttpClientUtil.post(tokenUrl, tokenBody, tokenHeaders);
        JSONObject tokenJson = JSON.parseObject(tokenResponse);
        if (tokenJson.getIntValue("code") != 0) {
            log.error("飞书 code 换 token 失败: {}", tokenJson.getString("msg"));
            throw new BusinessException("飞书授权失败: " + tokenJson.getString("msg"));
        }
        String userAccessToken = tokenJson.getJSONObject("data").getString("access_token");

        // 3. 获取飞书用户信息
        Map<String, String> userHeaders = new HashMap<>(4);
        userHeaders.put("Authorization", "Bearer " + userAccessToken);

        String userInfoUrl = feishuConfig.getBaseUrl() + "/authen/v1/user_info";
        String userInfoResponse = HttpClientUtil.get(userInfoUrl, userHeaders);
        JSONObject userInfoJson = JSON.parseObject(userInfoResponse);
        if (userInfoJson.getIntValue("code") != 0) {
            log.error("获取飞书用户信息失败: {}", userInfoJson.getString("msg"));
            throw new BusinessException("获取飞书用户信息失败: " + userInfoJson.getString("msg"));
        }
        JSONObject userData = userInfoJson.getJSONObject("data");
        String openId = userData.getString("open_id");
        String feishuName = userData.getString("name");

        // 4. 绑定到当前用户
        LambdaQueryWrapper<UserOauth> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserOauth::getUserId, userId)
                .eq(UserOauth::getOauthType, OAUTH_TYPE_FEISHU);
        UserOauth existing = userOauthMapper.selectOne(wrapper);

        if (existing != null) {
            existing.setOauthId(openId);
            existing.setOauthName(feishuName);
            existing.setUpdateTime(LocalDateTime.now());
            userOauthMapper.updateById(existing);
        } else {
            UserOauth userOauth = new UserOauth();
            userOauth.setUserId(userId);
            userOauth.setOauthType(OAUTH_TYPE_FEISHU);
            userOauth.setOauthId(openId);
            userOauth.setOauthName(feishuName);
            userOauth.setCreateTime(LocalDateTime.now());
            userOauth.setUpdateTime(LocalDateTime.now());
            userOauthMapper.insert(userOauth);
        }

        log.info("飞书 OAuth 绑定成功，userId: {}, openId: {}, name: {}", userId, openId, feishuName);
        return Result.success();
    }

    /**
     * 绑定飞书 open_id
     */
    @PostMapping("/bind")
    @ApiOperation("绑定飞书账号")
    public Result<Void> bind(@Validated @RequestBody FeishuBindRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.error("未获取到当前用户信息");
        }

        // 检查是否已绑定
        LambdaQueryWrapper<UserOauth> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserOauth::getUserId, userId)
                .eq(UserOauth::getOauthType, OAUTH_TYPE_FEISHU);
        UserOauth existing = userOauthMapper.selectOne(wrapper);

        if (existing != null) {
            // 已绑定，更新 open_id
            existing.setOauthId(request.getOpenId());
            existing.setOauthName(request.getFeishuName());
            existing.setUpdateTime(LocalDateTime.now());
            userOauthMapper.updateById(existing);
        } else {
            // 新绑定
            UserOauth userOauth = new UserOauth();
            userOauth.setUserId(userId);
            userOauth.setOauthType(OAUTH_TYPE_FEISHU);
            userOauth.setOauthId(request.getOpenId());
            userOauth.setOauthName(request.getFeishuName());
            userOauth.setCreateTime(LocalDateTime.now());
            userOauth.setUpdateTime(LocalDateTime.now());
            userOauthMapper.insert(userOauth);
        }

        return Result.success();
    }

    /**
     * 解绑飞书账号
     */
    @DeleteMapping("/unbind")
    @ApiOperation("解绑飞书账号")
    public Result<Void> unbind() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.error("未获取到当前用户信息");
        }

        LambdaQueryWrapper<UserOauth> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserOauth::getUserId, userId)
                .eq(UserOauth::getOauthType, OAUTH_TYPE_FEISHU);
        userOauthMapper.delete(wrapper);

        return Result.success();
    }

    /**
     * 查询当前用户飞书绑定信息
     */
    @GetMapping("/bindInfo")
    @ApiOperation("查询飞书绑定信息")
    public Result<Map<String, Object>> getBindInfo() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.error("未获取到当前用户信息");
        }

        LambdaQueryWrapper<UserOauth> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserOauth::getUserId, userId)
                .eq(UserOauth::getOauthType, OAUTH_TYPE_FEISHU);
        UserOauth userOauth = userOauthMapper.selectOne(wrapper);

        if (userOauth == null) {
            return Result.success(null);
        }

        Map<String, Object> info = new HashMap<>(4);
        info.put("openId", userOauth.getOauthId());
        info.put("feishuName", userOauth.getOauthName());
        info.put("bindTime", userOauth.getCreateTime());
        return Result.success(info);
    }

    /**
     * 发送飞书消息（指定接收者 ID）
     */
    @PostMapping("/send")
    @ApiOperation("发送飞书消息")
    public Result<String> sendMessage(@Validated @RequestBody FeishuSendMessageRequest request) {
        String messageId = feishuMessageService.sendMessage(request);
        return Result.success(messageId);
    }

    /**
     * 发送飞书消息给系统用户
     */
    @PostMapping("/send/user/{userId}")
    @ApiOperation("发送飞书消息给系统用户")
    public Result<String> sendMessageToUser(
            @PathVariable Long userId,
            @Validated @RequestBody FeishuSendMessageRequest request) {
        String messageId = feishuMessageService.sendMessageToUser(userId, request.getMsgType(), request.getContent());
        return Result.success(messageId);
    }

    /**
     * 上传图片到飞书
     */
    @PostMapping("/upload/image")
    @ApiOperation("上传图片到飞书")
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        String imageKey = feishuMessageService.uploadImage(file);
        Map<String, String> result = new HashMap<>(2);
        result.put("imageKey", imageKey);
        return Result.success(result);
    }

    /**
     * 上传文件到飞书
     */
    @PostMapping("/upload/file")
    @ApiOperation("上传文件到飞书")
    public Result<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @ApiParam(value = "文件类型", defaultValue = "stream")
            @RequestParam(value = "fileType", defaultValue = "stream") String fileType) {
        String fileKey = feishuMessageService.uploadFile(file, fileType);
        Map<String, String> result = new HashMap<>(2);
        result.put("fileKey", fileKey);
        return Result.success(result);
    }
}
