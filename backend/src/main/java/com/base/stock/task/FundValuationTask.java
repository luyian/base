package com.base.stock.fund.task;

import com.base.stock.fund.entity.FundConfig;
import com.base.stock.fund.service.FundService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 基金估值定时任务
 * 
 * 定时刷新所有基金的估值数据到Redis缓存
 * 
 * @author base
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FundValuationTask {

    private final FundService fundService;

    /**
     * 每天 10:00-12:00、14:00-16:00 每半小时刷新一次基金估值
     * 
     * 执行时间：10:00, 10:30, 11:00, 11:30, 12:00, 14:00, 14:30, 15:00, 15:30, 16:00
     */
    @Scheduled(cron = "0 0,30 10-12,14-16 * * ?")
    public void refreshFundValuation() {
        log.info("========== 开始执行基金估值刷新任务 ==========");
        
        try {
            // 获取所有基金列表
            List<FundConfig> allFunds = fundService.listAllFunds();
            
            if (allFunds == null || allFunds.isEmpty()) {
                log.info("没有需要刷新的基金");
                return;
            }
            
            log.info("共有 {} 只基金需要刷新估值", allFunds.size());
            
            int successCount = 0;
            int failCount = 0;
            
            // 遍历所有基金，刷新估值
            for (FundConfig fund : allFunds) {
                try {
                    fundService.getValuation(fund.getId());
                    successCount++;
                    log.debug("基金估值刷新成功：{} ({})", fund.getFundName(), fund.getFundCode());
                } catch (Exception e) {
                    failCount++;
                    log.error("基金估值刷新失败：{} ({}), 错误: {}", 
                            fund.getFundName(), fund.getFundCode(), e.getMessage());
                }
            }
            
            log.info("========== 基金估值刷新任务完成 ==========");
            log.info("成功: {} 只, 失败: {} 只", successCount, failCount);
            
        } catch (Exception e) {
            log.error("基金估值刷新任务执行异常", e);
        }
    }
}
