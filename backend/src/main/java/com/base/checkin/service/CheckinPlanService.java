package com.base.checkin.service;

import com.base.checkin.dto.CheckinPlanRequest;
import com.base.checkin.dto.CheckinPlanResponse;

import java.util.List;

/**
 * 打卡计划服务接口
 *
 * @author base
 */
public interface CheckinPlanService {

    /**
     * 查询用户的计划列表（含今日打卡状态）
     *
     * @param userId 用户ID
     * @return 计划列表
     */
    List<CheckinPlanResponse> listByUserId(Long userId);

    /**
     * 新增计划
     *
     * @param userId  用户ID
     * @param request 计划信息
     * @return 新计划ID
     */
    Long addPlan(Long userId, CheckinPlanRequest request);

    /**
     * 编辑计划（仅本人）
     *
     * @param userId  用户ID
     * @param id      计划ID
     * @param request 计划信息
     */
    void updatePlan(Long userId, Long id, CheckinPlanRequest request);

    /**
     * 删除计划（仅本人，逻辑删除）
     *
     * @param userId 用户ID
     * @param id     计划ID
     */
    void deletePlan(Long userId, Long id);

    /**
     * 统计用户启用计划总数
     *
     * @param userId 用户ID
     * @return 启用计划总数
     */
    int countEnabledByUserId(Long userId);
}
