package com.base.checkin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.base.checkin.dto.CalendarDayResponse;
import com.base.checkin.entity.CheckinPlan;
import com.base.checkin.entity.CheckinRecord;
import com.base.checkin.mapper.CheckinPlanMapper;
import com.base.checkin.mapper.CheckinRecordMapper;
import com.base.checkin.service.CheckinRecordService;
import com.base.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 每日打卡记录服务实现类
 *
 * @author base
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CheckinRecordServiceImpl implements CheckinRecordService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final CheckinRecordMapper checkinRecordMapper;
    private final CheckinPlanMapper checkinPlanMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleRecord(Long userId, Long planId, LocalDate date) {
        // 锁定计划，避免删除计划与打卡并发造成孤立记录
        CheckinPlan plan = checkinPlanMapper.selectByIdForUpdate(planId);
        if (plan == null) {
            throw new BusinessException(404, "计划不存在");
        }
        if (!plan.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作该计划");
        }

        checkinRecordMapper.atomicToggle(planId, userId, date);
        CheckinRecord record = checkinRecordMapper.selectByPlanAndDateIncludeDeleted(planId, date);
        if (record == null || record.getDeleted() == null) {
            throw new BusinessException(500, "打卡状态更新失败");
        }

        boolean checked = record.getDeleted() == 0;
        log.info("{}打卡，userId: {}, planId: {}, date: {}", checked ? "完成" : "取消", userId, planId, date);
        return checked;
    }

    @Override
    public List<CalendarDayResponse> getCalendar(Long userId, String month) {
        YearMonth yearMonth = parseMonth(month);
        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate lastDay = yearMonth.atEndOfMonth();

        // 包含已删除计划，用创建/删除时间还原每天实际生效的计划总数
        List<CheckinPlan> plans = checkinPlanMapper.selectEnabledByUserIdIncludeDeleted(userId);
        Map<Long, CheckinPlan> planMap = plans.stream()
                .collect(Collectors.toMap(CheckinPlan::getId, plan -> plan));

        // 查询该月有效打卡记录，只统计打卡当天计划仍生效的记录，与 total 口径一致
        LambdaQueryWrapper<CheckinRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckinRecord::getUserId, userId)
                .between(CheckinRecord::getCheckinDate, firstDay, lastDay)
                .select(CheckinRecord::getPlanId, CheckinRecord::getCheckinDate);
        List<CheckinRecord> records = checkinRecordMapper.selectList(wrapper);
        Map<LocalDate, Long> completedMap = records.stream()
                .filter(record -> {
                    CheckinPlan plan = planMap.get(record.getPlanId());
                    return plan != null && isEffectiveOn(plan, record.getCheckinDate());
                })
                .collect(Collectors.groupingBy(CheckinRecord::getCheckinDate, Collectors.counting()));

        // 组装当月每一天
        List<CalendarDayResponse> result = new ArrayList<>();
        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            LocalDate date = yearMonth.atDay(day);
            CalendarDayResponse response = new CalendarDayResponse();
            response.setDate(date.format(DATE_FORMATTER));
            response.setTotal(countEffectivePlans(plans, date));
            response.setCompleted(completedMap.getOrDefault(date, 0L).intValue());
            result.add(response);
        }
        return result;
    }

    /**
     * 解析月份参数（yyyy-MM）
     *
     * @param month 月份字符串
     * @return YearMonth
     */
    private YearMonth parseMonth(String month) {
        try {
            return YearMonth.parse(month, DateTimeFormatter.ofPattern("yyyy-MM"));
        } catch (DateTimeParseException e) {
            throw new BusinessException(400, "月份格式错误，应为yyyy-MM");
        }
    }

    /**
     * 统计指定日期实际生效的计划数
     *
     * @param plans 计划列表（包含已删除计划）
     * @param date  日期
     * @return 生效计划数
     */
    private int countEffectivePlans(List<CheckinPlan> plans, LocalDate date) {
        return Math.toIntExact(plans.stream().filter(plan -> isEffectiveOn(plan, date)).count());
    }

    /**
     * 判断计划在指定日期是否生效，创建日计入、删除日不计入
     *
     * @param plan 计划
     * @param date 日期
     * @return true表示生效
     */
    private boolean isEffectiveOn(CheckinPlan plan, LocalDate date) {
        // 单日事件只在目标日期生效
        if (plan.getPlanType() != null && plan.getPlanType() == 1) {
            return plan.getTargetDate() != null && plan.getTargetDate().equals(date);
        }

        // 长期计划：创建后生效（含创建日），删除后失效（删除日不计入）
        LocalDateTime createTime = plan.getCreateTime();
        if (createTime != null && date.isBefore(createTime.toLocalDate())) {
            return false;
        }
        if (plan.getDeleted() == null || plan.getDeleted() == 0) {
            return true;
        }
        LocalDateTime deletedTime = plan.getDeletedTime() != null ? plan.getDeletedTime() : plan.getUpdateTime();
        return deletedTime == null || date.isBefore(deletedTime.toLocalDate());
    }
}
