package com.base.checkin.util;

import com.base.checkin.entity.CheckinPlan;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 打卡计划生效判定工具类
 *
 * <p>统一“计划在某日是否生效”的判定口径，供日历完成度统计与历史日期计划列表共用，
 * 保证两者数据一致：删除计划不改变历史日期的生效状态。</p>
 *
 * @author base
 */
public final class CheckinPlanEffectiveUtil {

    private CheckinPlanEffectiveUtil() {
    }

    /**
     * 判断计划在指定日期是否生效。
     *
     * <p>口径：单日事件只在目标日期生效；长期计划创建日计入、删除日不计入。
     * 与日历完成度统计保持一致。</p>
     *
     * @param plan 计划，为 null 时返回 false
     * @param date 日期，为 null 时返回 false
     * @return true 表示该计划在指定日期生效
     */
    public static boolean isEffectiveOn(CheckinPlan plan, LocalDate date) {
        if (plan == null || date == null) {
            return false;
        }

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
