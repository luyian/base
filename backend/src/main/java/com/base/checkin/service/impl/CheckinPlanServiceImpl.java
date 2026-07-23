package com.base.checkin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.base.checkin.dto.CheckinPlanRequest;
import com.base.checkin.dto.CheckinPlanResponse;
import com.base.checkin.entity.CheckinPlan;
import com.base.checkin.entity.CheckinRecord;
import com.base.checkin.mapper.CheckinPlanMapper;
import com.base.checkin.mapper.CheckinRecordMapper;
import com.base.checkin.service.CheckinPlanService;
import com.base.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 打卡计划服务实现类
 *
 * @author base
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CheckinPlanServiceImpl implements CheckinPlanService {

    /**
     * 默认卡片颜色（未指定时使用）
     */
    private static final String DEFAULT_COLOR = "#3B82F6";

    private final CheckinPlanMapper checkinPlanMapper;
    private final CheckinRecordMapper checkinRecordMapper;

    @Override
    public List<CheckinPlanResponse> listByUserId(Long userId) {
        LambdaQueryWrapper<CheckinPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckinPlan::getUserId, userId)
                .orderByAsc(CheckinPlan::getSortOrder)
                .orderByAsc(CheckinPlan::getId);
        List<CheckinPlan> plans = checkinPlanMapper.selectList(wrapper);
        if (plans.isEmpty()) {
            return Collections.emptyList();
        }

        // 查询今日已打卡的计划ID集合
        Set<Long> checkedPlanIds = queryCheckedPlanIds(userId, LocalDate.now());

        return plans.stream().map(plan -> {
            CheckinPlanResponse response = new CheckinPlanResponse();
            BeanUtils.copyProperties(plan, response);
            response.setTodayChecked(checkedPlanIds.contains(plan.getId()));
            return response;
        }).collect(Collectors.toList());
    }

    @Override
    public Long addPlan(Long userId, CheckinPlanRequest request) {
        CheckinPlan plan = new CheckinPlan();
        plan.setUserId(userId);
        plan.setTitle(request.getTitle());
        plan.setIcon(request.getIcon());
        plan.setColor(StringUtils.hasText(request.getColor()) ? request.getColor() : DEFAULT_COLOR);
        plan.setRemark(request.getRemark());
        plan.setSortOrder(0);
        plan.setStatus(1);
        checkinPlanMapper.insert(plan);
        log.info("新增打卡计划成功，userId: {}, title: {}", userId, request.getTitle());
        return plan.getId();
    }

    @Override
    public void updatePlan(Long userId, Long id, CheckinPlanRequest request) {
        CheckinPlan plan = checkOwnPlan(userId, id);
        plan.setTitle(request.getTitle());
        plan.setIcon(request.getIcon());
        plan.setColor(StringUtils.hasText(request.getColor()) ? request.getColor() : plan.getColor());
        plan.setRemark(request.getRemark());
        checkinPlanMapper.updateById(plan);
        log.info("编辑打卡计划成功，userId: {}, id: {}", userId, id);
    }

    @Override
    public void deletePlan(Long userId, Long id) {
        CheckinPlan plan = checkOwnPlan(userId, id);
        int affectedRows = checkinPlanMapper.logicDeleteById(plan.getId(), userId, LocalDateTime.now());
        if (affectedRows == 0) {
            throw new BusinessException(404, "计划不存在");
        }
        log.info("删除打卡计划成功，userId: {}, id: {}", userId, id);
    }

    @Override
    public int countEnabledByUserId(Long userId) {
        LambdaQueryWrapper<CheckinPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckinPlan::getUserId, userId)
                .eq(CheckinPlan::getStatus, 1);
        return Math.toIntExact(checkinPlanMapper.selectCount(wrapper));
    }

    /**
     * 校验计划存在且属于当前用户
     *
     * @param userId 用户ID
     * @param id     计划ID
     * @return 计划实体
     */
    private CheckinPlan checkOwnPlan(Long userId, Long id) {
        CheckinPlan plan = checkinPlanMapper.selectById(id);
        if (plan == null) {
            throw new BusinessException(404, "计划不存在");
        }
        if (!plan.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作该计划");
        }
        return plan;
    }

    /**
     * 查询用户某天已打卡的计划ID集合
     *
     * @param userId 用户ID
     * @param date   日期
     * @return 已打卡的计划ID集合
     */
    private Set<Long> queryCheckedPlanIds(Long userId, LocalDate date) {
        LambdaQueryWrapper<CheckinRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckinRecord::getUserId, userId)
                .eq(CheckinRecord::getCheckinDate, date)
                .select(CheckinRecord::getPlanId);
        List<CheckinRecord> records = checkinRecordMapper.selectList(wrapper);
        return records.stream().map(CheckinRecord::getPlanId).collect(Collectors.toSet());
    }
}
