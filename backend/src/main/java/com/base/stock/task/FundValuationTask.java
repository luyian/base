package com.base.stock.task;

import com.base.stock.service.FundService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 基金估值定时任务
 *
 * @author base
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FundValuationTask {

    private final FundService fundService;

    @PostConstruct
    public void init() {
        log.info("基金估值定时任务已注册: refreshFundValuation, 执行时间 10:00-12:00、14:00-16:00 每半点/整点");
    }

    /**
     * 每天 10:00-12:00、14:00-16:00 每半小时刷新一次基金估值
     * 执行时间：10:00, 10:30, 11:00, 11:30, 12:00, 14:00, 14:30, 15:00, 15:30, 16:00
     */
    @Scheduled(cron = "0 0,30 10-12,14-16 * * ?")
    public void refreshFundValuation() {
        log.info("开始执行基金估值刷新定时任务");
        try {
            fundService.refreshAllFundValuation();
        } catch (Exception e) {
            log.error("基金估值刷新定时任务异常", e);
        }
    }
}
