package com.base.stock.recommend.task;

import com.base.stock.recommend.service.ScoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 每日打分定时任务
 *
 * @author base
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DailyScoreTask {

    private final ScoreService scoreService;

    /**
     * 每天16:30执行打分任务（港股收盘后）
     */
//    @Scheduled(cron = "0 30 16 * * ?")
    public void executeDailyScore() {
        log.info("========== 开始执行每日打分任务 ==========");
        try {
            LocalDate scoreDate = LocalDate.now();
            scoreService.executeAllStockScore(scoreDate);
            log.info("========== 每日打分任务执行完成 ==========");
        } catch (Exception e) {
            log.error("========== 每日打分任务执行失败 ==========", e);
        }
    }
}
