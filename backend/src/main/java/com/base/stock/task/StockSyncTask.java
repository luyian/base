package com.base.stock.task;

import com.base.stock.service.TokenManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 股票同步定时任务
 *
 * @author base
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StockSyncTask {

    private final TokenManagerService tokenManagerService;

    /**
     * 每天 0:00 重置 Token 每日计数
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void resetTokenDailyCount() {
        log.info("开始执行定时任务：重置 Token 每日计数");
        try {
            tokenManagerService.resetDailyCount("itick");
            log.info("定时任务执行完成：重置 Token 每日计数");
        } catch (Exception e) {
            log.error("定时任务执行失败：重置 Token 每日计数", e);
        }
    }
}
