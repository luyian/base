package com.base.common.feishu.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.base.common.exception.BusinessException;
import com.base.common.feishu.constant.FeishuMsgTypeEnum;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 图片消息处理器
 *
 * @author base
 * @since 2026-02-11
 */
@Component
public class ImageMessageHandler implements FeishuMessageHandler {

    @Override
    public FeishuMsgTypeEnum getMsgType() {
        return FeishuMsgTypeEnum.IMAGE;
    }

    @Override
    public void validate(String content) {
        if (!StringUtils.hasText(content)) {
            throw new BusinessException("图片消息内容不能为空");
        }
        JSONObject json = JSON.parseObject(content);
        if (!json.containsKey("image_key") || !StringUtils.hasText(json.getString("image_key"))) {
            throw new BusinessException("图片消息必须包含 image_key 字段");
        }
    }

    @Override
    public String buildContent(String content) {
        // 飞书图片消息格式：{"image_key": "img_xxxx"}
        return content;
    }
}
