package com.base.common.feishu.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.base.common.exception.BusinessException;
import com.base.common.feishu.client.FeishuApiClient;
import com.base.common.feishu.dto.FeishuSendMessageRequest;
import com.base.common.feishu.handler.FeishuMessageHandler;
import com.base.common.feishu.service.FeishuMessageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.base.system.entity.UserOauth;
import com.base.system.mapper.UserOauthMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 飞书消息发送服务实现
 * 使用策略模式路由不同消息类型到对应的 Handler
 *
 * @author base
 * @since 2026-02-11
 */
@Slf4j
@Service
public class FeishuMessageServiceImpl implements FeishuMessageService {

    /**
     * 飞书绑定类型标识
     */
    private static final String OAUTH_TYPE_FEISHU = "feishu";

    @Autowired
    private FeishuApiClient feishuApiClient;

    @Autowired
    private UserOauthMapper userOauthMapper;

    @Autowired
    private List<FeishuMessageHandler> handlerList;

    /**
     * 消息类型 -> Handler 路由表
     */
    private final Map<String, FeishuMessageHandler> handlerMap = new HashMap<>();

    /**
     * 初始化路由表
     */
    @PostConstruct
    public void init() {
        for (FeishuMessageHandler handler : handlerList) {
            handlerMap.put(handler.getMsgType().getCode(), handler);
        }
        log.info("飞书消息处理器初始化完成，已注册 {} 个处理器: {}", handlerMap.size(), handlerMap.keySet());
    }

    @Override
    public String sendMessage(FeishuSendMessageRequest request) {
        // 获取对应的消息处理器
        FeishuMessageHandler handler = getHandler(request.getMsgType());

        // 校验消息内容
        handler.validate(request.getContent());

        // 构建消息内容
        String content = handler.buildContent(request.getContent());

        // 构建请求体
        Map<String, Object> body = new HashMap<>(8);
        body.put("receive_id", request.getReceiveId());
        body.put("msg_type", request.getMsgType());
        body.put("content", content);

        // 构建查询参数
        Map<String, Object> params = new HashMap<>(4);
        params.put("receive_id_type", request.getReceiveIdType());

        // 调用飞书 API 发送消息
        JSONObject response = feishuApiClient.post("/im/v1/messages", body, params);
        String messageId = response.getJSONObject("data").getString("message_id");
        log.info("飞书消息发送成功，messageId: {}，receiveId: {}，msgType: {}",
                messageId, request.getReceiveId(), request.getMsgType());
        return messageId;
    }

    @Override
    public String sendMessageToUser(Long userId, String msgType, String content) {
        // 查找用户绑定的飞书 open_id
        String openId = getFeishuOpenId(userId);

        // 构建发送请求
        FeishuSendMessageRequest request = new FeishuSendMessageRequest();
        request.setReceiveId(openId);
        request.setReceiveIdType("open_id");
        request.setMsgType(msgType);
        request.setContent(content);

        return sendMessage(request);
    }

    @Override
    public String uploadImage(MultipartFile file) {
        JSONObject response = feishuApiClient.uploadImage(file);
        String imageKey = response.getJSONObject("data").getString("image_key");
        log.info("飞书图片上传成功，imageKey: {}", imageKey);
        return imageKey;
    }

    @Override
    public String uploadFile(MultipartFile file, String fileType) {
        JSONObject response = feishuApiClient.uploadFile(file, fileType);
        String fileKey = response.getJSONObject("data").getString("file_key");
        log.info("飞书文件上传成功，fileKey: {}", fileKey);
        return fileKey;
    }

    /**
     * 获取消息处理器
     *
     * @param msgType 消息类型
     * @return 消息处理器
     */
    private FeishuMessageHandler getHandler(String msgType) {
        FeishuMessageHandler handler = handlerMap.get(msgType);
        if (handler == null) {
            throw new BusinessException("不支持的飞书消息类型: " + msgType);
        }
        return handler;
    }

    /**
     * 根据系统用户 ID 获取绑定的飞书 open_id
     *
     * @param userId 系统用户 ID
     * @return 飞书 open_id
     */
    private String getFeishuOpenId(Long userId) {
        LambdaQueryWrapper<UserOauth> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserOauth::getUserId, userId)
                .eq(UserOauth::getOauthType, OAUTH_TYPE_FEISHU);
        UserOauth userOauth = userOauthMapper.selectOne(wrapper);
        if (userOauth == null) {
            throw new BusinessException("该用户未绑定飞书账号");
        }
        return userOauth.getOauthId();
    }
}
