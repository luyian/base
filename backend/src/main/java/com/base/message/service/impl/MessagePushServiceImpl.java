package com.base.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.base.message.channel.ChannelSender;
import com.base.message.content.ContentBuilder;
import com.base.message.entity.Subscription;
import com.base.message.mapper.SubscriptionMapper;
import com.base.message.service.MessagePushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 消息推送调度服务实现类
 *
 * @author base
 */
@Slf4j
@Service
public class MessagePushServiceImpl implements MessagePushService {

    private final SubscriptionMapper subscriptionMapper;
    private final List<ContentBuilder> contentBuilderList;
    private final List<ChannelSender> channelSenderList;

    private final Map<String, ContentBuilder> contentBuilderMap = new HashMap<>();
    private final Map<String, ChannelSender> channelSenderMap = new HashMap<>();

    public MessagePushServiceImpl(SubscriptionMapper subscriptionMapper,
                                  List<ContentBuilder> contentBuilderList,
                                  List<ChannelSender> channelSenderList) {
        this.subscriptionMapper = subscriptionMapper;
        this.contentBuilderList = contentBuilderList;
        this.channelSenderList = channelSenderList;
    }

    @PostConstruct
    public void init() {
        for (ContentBuilder builder : contentBuilderList) {
            contentBuilderMap.put(builder.getSubType(), builder);
        }
        for (ChannelSender sender : channelSenderList) {
            channelSenderMap.put(sender.getChannel(), sender);
        }
        log.info("消息推送服务初始化完成，内容生成器: {}，渠道发送器: {}",
                contentBuilderMap.keySet(), channelSenderMap.keySet());
    }

    @Override
    public void executePush(String subType) {
        log.info("开始执行推送，订阅类型: {}", subType);

        ContentBuilder contentBuilder = contentBuilderMap.get(subType);
        if (contentBuilder == null) {
            log.warn("未找到订阅类型 {} 对应的内容生成器", subType);
            return;
        }

        // 查询所有该类型的活跃订阅
        LambdaQueryWrapper<Subscription> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Subscription::getSubType, subType)
                .eq(Subscription::getStatus, 1);
        List<Subscription> subscriptions = subscriptionMapper.selectList(wrapper);

        if (subscriptions.isEmpty()) {
            log.info("订阅类型 {} 无活跃订阅", subType);
            return;
        }

        // 按 userId 分组
        Map<Long, List<Subscription>> userSubMap = subscriptions.stream()
                .collect(Collectors.groupingBy(Subscription::getUserId));

        log.info("订阅类型 {} 共 {} 个用户需要推送", subType, userSubMap.size());

        for (Map.Entry<Long, List<Subscription>> entry : userSubMap.entrySet()) {
            Long userId = entry.getKey();
            List<Subscription> userSubs = entry.getValue();

            // 内容只生成一次
            String message = null;
            try {
                message = contentBuilder.build(userId);
            } catch (Exception e) {
                log.error("生成推送内容失败，userId: {}, subType: {}", userId, subType, e);
                // 更新所有该用户的订阅状态
                for (Subscription sub : userSubs) {
                    updatePushResult(sub, false, "内容生成失败: " + e.getMessage());
                }
                continue;
            }

            if (message == null) {
                log.info("用户 {} 无推送内容，跳过", userId);
                for (Subscription sub : userSubs) {
                    updatePushResult(sub, true, "无推送内容");
                }
                continue;
            }

            // 分渠道发送
            for (Subscription sub : userSubs) {
                ChannelSender sender = channelSenderMap.get(sub.getChannel());
                if (sender == null) {
                    log.warn("未找到渠道 {} 对应的发送器", sub.getChannel());
                    updatePushResult(sub, false, "渠道不支持: " + sub.getChannel());
                    continue;
                }

                try {
                    sender.send(userId, message);
                    updatePushResult(sub, true, "推送成功");
                    log.info("推送成功，userId: {}, channel: {}", userId, sub.getChannel());
                } catch (Exception e) {
                    log.error("推送失败，userId: {}, channel: {}", userId, sub.getChannel(), e);
                    updatePushResult(sub, false, "推送失败: " + e.getMessage());
                }
            }
        }

        log.info("推送执行完成，订阅类型: {}", subType);
    }

    /**
     * 更新推送结果
     */
    private void updatePushResult(Subscription sub, boolean success, String msg) {
        sub.setLastPushTime(LocalDateTime.now());
        sub.setLastPushStatus(success ? 1 : 0);
        sub.setLastPushMsg(msg != null && msg.length() > 500 ? msg.substring(0, 500) : msg);
        subscriptionMapper.updateById(sub);
    }
}
