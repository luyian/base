package com.base.approval.task;

import com.base.approval.entity.ApprovalInstance;
import com.base.approval.enums.ApprovalInstanceStatus;
import com.base.approval.mapper.ApprovalInstanceMapper;
import com.base.approval.service.ApprovalInstanceService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 审批状态兜底同步定时任务
 * 每 30 分钟主动拉取 PENDING 实例的最新状态
 *
 * @author base
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApprovalStatusSyncTask {

    private final ApprovalInstanceMapper approvalInstanceMapper;
    private final ApprovalInstanceService approvalInstanceService;

    @Scheduled(fixedDelay = 1800000)
    public void syncPendingApprovals() {
        LambdaQueryWrapper<ApprovalInstance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApprovalInstance::getStatus, ApprovalInstanceStatus.PENDING.getCode())
                .isNotNull(ApprovalInstance::getPlatformInstanceId)
                .lt(ApprovalInstance::getUpdateTime, LocalDateTime.now().minusMinutes(10))
                .last("LIMIT 50");
        List<ApprovalInstance> pendingList = approvalInstanceMapper.selectList(wrapper);

        if (pendingList.isEmpty()) {
            return;
        }
        log.info("开始兜底同步审批状态，共 {} 条", pendingList.size());
        for (ApprovalInstance instance : pendingList) {
            try {
                approvalInstanceService.syncApprovalStatus(instance.getId());
            } catch (Exception e) {
                log.error("同步审批状态失败: id={}", instance.getId(), e);
            }
        }
    }
}
