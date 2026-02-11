package com.base.common.feishu.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.base.common.exception.BusinessException;
import com.base.common.feishu.constant.FeishuMsgTypeEnum;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 文件消息处理器
 *
 * @author base
 * @since 2026-02-11
 */
@Component
public class FileMessageHandler implements FeishuMessageHandler {

    @Override
    public FeishuMsgTypeEnum getMsgType() {
        return FeishuMsgTypeEnum.FILE;
    }

    @Override
    public void validate(String content) {
        if (!StringUtils.hasText(content)) {
            throw new BusinessException("文件消息内容不能为空");
        }
        JSONObject json = JSON.parseObject(content);
        if (!json.containsKey("file_key") || !StringUtils.hasText(json.getString("file_key"))) {
            throw new BusinessException("文件消息必须包含 file_key 字段");
        }
    }

    @Override
    public String buildContent(String content) {
        // 飞书文件消息格式：{"file_key": "file_xxxx"}
        return content;
    }
}
