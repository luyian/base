package com.base.checkin.service;

import com.base.checkin.dto.CalendarDayResponse;

import java.time.LocalDate;
import java.util.List;

/**
 * 每日打卡记录服务接口
 *
 * @author base
 */
public interface CheckinRecordService {

    /**
     * 打卡/取消打卡切换
     *
     * @param userId 用户ID
     * @param planId 计划ID
     * @param date   打卡日期
     * @return 切换后的状态（true已打卡 false未打卡）
     */
    boolean toggleRecord(Long userId, Long planId, LocalDate date);

    /**
     * 查询某月完成度概览
     *
     * @param userId 用户ID
     * @param month  月份（yyyy-MM）
     * @return 当月每一天的完成度
     */
    List<CalendarDayResponse> getCalendar(Long userId, String month);
}
