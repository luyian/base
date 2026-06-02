package com.base.common.feishu.event.handler;

import com.alibaba.fastjson2.JSONObject;
import com.base.common.thirdparty.ThirdPartyPlatform;
import com.base.common.thirdparty.event.ThirdPartyEvent;
import com.base.common.thirdparty.event.ThirdPartyEventHandler;
import com.base.system.entity.UserOauth;
import com.base.system.mapper.UserOauthMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 飞书通讯录变更事件处理器
 *
 * @author base
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FeishuContactEventHandler implements ThirdPartyEventHandler {

    private final UserOauthMapper userOauthMapper;

    @Override
    public String getEventType() {
        return "contact.user.updated_v3";
    }

    @Override
    public ThirdPartyPlatform getPlatform() {
        return ThirdPartyPlatform.FEISHU;
    }

    @Override
    public void handle(ThirdPartyEvent event) {
        JSONObject payload = JSONObject.parseObject(event.getPayload());
        JSONObject eventBody = payload.getJSONObject("event");
        if (eventBody == null) {
            return;
        }

        JSONObject object = eventBody.getJSONObject("object");
        if (object == null) {
            return;
        }

        String openId = object.getString("open_id");
        String name = object.getString("name");
        String avatar = object.getString("avatar_url");

        if (openId == null) {
            return;
        }

        log.info("处理飞书通讯录变更: openId={}, name={}", openId, name);

        LambdaQueryWrapper<UserOauth> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserOauth::getOauthType, "feishu")
                .eq(UserOauth::getOauthId, openId);
        UserOauth userOauth = userOauthMapper.selectOne(wrapper);

        if (userOauth != null) {
            if (name != null) {
                userOauth.setOauthName(name);
            }
            if (avatar != null) {
                userOauth.setOauthAvatar(avatar);
            }
            userOauthMapper.updateById(userOauth);
        }
    }
}
