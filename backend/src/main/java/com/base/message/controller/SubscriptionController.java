package com.base.message.controller;

import com.base.common.result.Result;
import com.base.message.dto.SubscriptionResponse;
import com.base.message.service.MessagePushService;
import com.base.message.service.SubscriptionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 消息订阅控制器
 *
 * @author base
 */
@Api(tags = "消息订阅管理")
@RestController
@RequestMapping("/message/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final MessagePushService messagePushService;

    /**
     * 查询我的订阅列表
     */
    @ApiOperation("查询我的订阅列表")
    @GetMapping("/list")
    public Result<List<SubscriptionResponse>> list() {
        List<SubscriptionResponse> subscriptions = subscriptionService.listMySubscriptions();
        return Result.success(subscriptions);
    }

    /**
     * 开启/关闭订阅
     */
    @ApiOperation("开启/关闭订阅")
    @PutMapping("/toggle")
    public Result<Void> toggle(@RequestParam String subType,
                               @RequestParam String channel,
                               @RequestParam boolean enabled) {
        subscriptionService.toggleSubscription(subType, channel, enabled);
        return Result.success();
    }

    /**
     * 手动触发推送（管理员）
     */
    @ApiOperation("手动触发推送")
    @PreAuthorize("hasAuthority('message:push:execute')")
    @PostMapping("/push/{subType}")
    public Result<Void> manualPush(@PathVariable String subType) {
        messagePushService.executePush(subType);
        return Result.success();
    }
}
