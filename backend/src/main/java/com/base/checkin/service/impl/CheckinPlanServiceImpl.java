package com.base.checkin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.base.checkin.dto.CheckinPlanRequest;
import com.base.checkin.dto.CheckinPlanResponse;
import com.base.checkin.entity.CheckinPlan;
import com.base.checkin.entity.CheckinRecord;
import com.base.checkin.mapper.CheckinPlanMapper;
import com.base.checkin.mapper.CheckinRecordMapper;
import com.base.checkin.service.CheckinPlanService;
import com.base.checkin.util.CheckinPlanEffectiveUtil;
import com.base.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
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
    private static final String DEFAULT_COLOR = "#C6402E";

    private final CheckinPlanMapper checkinPlanMapper;
    private final CheckinRecordMapper checkinRecordMapper;

    @Override
    public List<CheckinPlanResponse> listByUserId(Long userId) {
        LambdaQueryWrapper<CheckinPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckinPlan::getUserId, userId)
                .and(w -> w
                        // 长期计划
                        .eq(CheckinPlan::getPlanType, 0)
                        // 或者今天到期的单日事件
                        .or(x -> x.eq(CheckinPlan::getPlanType, 1)
                                .eq(CheckinPlan::getTargetDate, LocalDate.now()))
                )
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
        plan.setPlanType(request.getPlanType() != null ? request.getPlanType() : 0);
        plan.setTargetDate(request.getTargetDate());
        plan.setSortOrder(0);
        plan.setStatus(1);
        checkinPlanMapper.insert(plan);
        log.info("新增打卡计划成功，userId: {}, title: {}, planType: {}", userId, request.getTitle(), plan.getPlanType());
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
    public List<CheckinPlanResponse> listByUserIdAndDate(Long userId, LocalDate date) {
        // 查询用户全部启用计划（含已逻辑删除），用创建/删除时间还原指定日期实际生效的计划，
        // 与日历完成度统计口径保持一致：删除计划不改变历史日期的生效状态
        List<CheckinPlan> plans = checkinPlanMapper.selectEnabledByUserIdIncludeDeleted(userId);
        if (plans.isEmpty()) {
            return Collections.emptyList();
        }

        // 过滤出在指定日期生效的计划，按 sort_order、id 排序
        List<CheckinPlan> effectivePlans = plans.stream()
                .filter(plan -> CheckinPlanEffectiveUtil.isEffectiveOn(plan, date))
                .sorted(Comparator
                        .comparing(CheckinPlan::getSortOrder, Comparator.nullsFirst(Comparator.naturalOrder()))
                        .thenComparing(CheckinPlan::getId))
                .collect(Collectors.toList());
        if (effectivePlans.isEmpty()) {
            return Collections.emptyList();
        }

        // 查询指定日期已打卡的计划ID集合
        Set<Long> checkedPlanIds = queryCheckedPlanIds(userId, date);

        return effectivePlans.stream().map(plan -> {
            CheckinPlanResponse response = new CheckinPlanResponse();
            BeanUtils.copyProperties(plan, response);
            response.setTodayChecked(checkedPlanIds.contains(plan.getId()));
            response.setDeleted(plan.getDeleted() != null && plan.getDeleted() == 1);
            return response;
        }).collect(Collectors.toList());
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
