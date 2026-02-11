package com.base.system.controller;

import com.base.common.feishu.dto.FeishuBindRequest;
import com.base.common.feishu.dto.FeishuSendMessageRequest;
import com.base.common.feishu.service.FeishuMessageService;
import com.base.common.result.Result;
import com.base.common.util.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.base.system.entity.UserOauth;
import com.base.system.mapper.UserOauthMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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

    @Autowired
    private FeishuMessageService feishuMessageService;

    @Autowired
    private UserOauthMapper userOauthMapper;

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
