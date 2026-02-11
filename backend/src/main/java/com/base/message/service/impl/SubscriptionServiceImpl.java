package com.base.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.base.common.util.SecurityUtils;
import com.base.message.constant.ChannelEnum;
import com.base.message.constant.SubscriptionTypeEnum;
import com.base.message.dto.SubscriptionResponse;
import com.base.message.entity.Subscription;
import com.base.message.mapper.SubscriptionMapper;
import com.base.message.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订阅管理服务实现类
 *
 * @author base
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionMapper subscriptionMapper;

    @Override
    public List<SubscriptionResponse> listMySubscriptions() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }

        // 查询用户已有的订阅记录
        LambdaQueryWrapper<Subscription> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Subscription::getUserId, userId);
        List<Subscription> existingSubs = subscriptionMapper.selectList(wrapper);

        // 按 subType+channel 建立索引
        Map<String, Subscription> subMap = existingSubs.stream()
                .collect(Collectors.toMap(
                        s -> s.getSubType() + ":" + s.getChannel(),
                        s -> s
                ));

        // 遍历所有订阅类型和渠道，生成完整列表
        List<SubscriptionResponse> responses = new ArrayList<>();
        for (SubscriptionTypeEnum subType : SubscriptionTypeEnum.values()) {
            for (ChannelEnum channel : ChannelEnum.values()) {
                // 目前只支持飞书渠道
                if (channel != ChannelEnum.FEISHU) {
                    continue;
                }
                SubscriptionResponse resp = new SubscriptionResponse();
                resp.setSubType(subType.getCode());
                resp.setSubTypeDesc(subType.getDesc());
                resp.setChannel(channel.getCode());
                resp.setChannelDesc(channel.getDesc());

                String key = subType.getCode() + ":" + channel.getCode();
                Subscription sub = subMap.get(key);
                if (sub != null) {
                    resp.setEnabled(sub.getStatus() == 1);
                    resp.setLastPushTime(sub.getLastPushTime());
                    resp.setLastPushStatus(sub.getLastPushStatus());
                    resp.setLastPushMsg(sub.getLastPushMsg());
                } else {
                    resp.setEnabled(false);
                }
                responses.add(resp);
            }
        }
        return responses;
    }

    @Override
    public void toggleSubscription(String subType, String channel, boolean enabled) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }

        LambdaQueryWrapper<Subscription> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Subscription::getUserId, userId)
                .eq(Subscription::getSubType, subType)
                .eq(Subscription::getChannel, channel);
        Subscription existing = subscriptionMapper.selectOne(wrapper);

        if (existing != null) {
            existing.setStatus(enabled ? 1 : 0);
            subscriptionMapper.updateById(existing);
        } else if (enabled) {
            Subscription sub = new Subscription();
            sub.setUserId(userId);
            sub.setSubType(subType);
            sub.setChannel(channel);
            sub.setStatus(1);
            subscriptionMapper.insert(sub);
        }
    }
}
