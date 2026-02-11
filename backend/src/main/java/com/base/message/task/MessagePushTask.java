package com.base.message.task;

import com.base.message.constant.SubscriptionTypeEnum;
import com.base.message.service.MessagePushService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 消息推送定时任务
 *
 * @author base
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessagePushTask {

    private final MessagePushService messagePushService;

    /**
     * 每天 14:30 推送基金估值
     */
    @Scheduled(cron = "0 30 14 * * ?")
    public void pushFundValuation() {
        log.info("定时任务触发：基金估值推送");
        messagePushService.executePush(SubscriptionTypeEnum.FUND_VALUATION.getCode());
    }
}
