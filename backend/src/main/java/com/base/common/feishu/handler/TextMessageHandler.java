package com.base.common.feishu.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.base.common.exception.BusinessException;
import com.base.common.feishu.constant.FeishuMsgTypeEnum;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 文本消息处理器
 *
 * @author base
 * @since 2026-02-11
 */
@Component
public class TextMessageHandler implements FeishuMessageHandler {

    @Override
    public FeishuMsgTypeEnum getMsgType() {
        return FeishuMsgTypeEnum.TEXT;
    }

    @Override
    public void validate(String content) {
        if (!StringUtils.hasText(content)) {
            throw new BusinessException("文本消息内容不能为空");
        }
        JSONObject json = JSON.parseObject(content);
        if (!json.containsKey("text") || !StringUtils.hasText(json.getString("text"))) {
            throw new BusinessException("文本消息必须包含 text 字段");
        }
    }

    @Override
    public String buildContent(String content) {
        // 飞书文本消息格式：{"text": "xxx"}
        return content;
    }
}
