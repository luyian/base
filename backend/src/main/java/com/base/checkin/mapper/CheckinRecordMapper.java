package com.base.checkin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.checkin.entity.CheckinRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;

/**
 * 每日打卡记录 Mapper 接口
 *
 * @author base
 */
@Mapper
public interface CheckinRecordMapper extends BaseMapper<CheckinRecord> {

    /**
     * 查询指定计划某天的打卡记录（包含已逻辑删除的，用于"恢复优先"打卡切换）
     *
     * @param planId      计划ID
     * @param checkinDate 打卡日期
     * @return 打卡记录，不存在返回 null
     */
    @Select("SELECT * FROM checkin_record WHERE plan_id = #{planId} AND checkin_date = #{checkinDate} LIMIT 1")
    CheckinRecord selectByPlanAndDateIncludeDeleted(@Param("planId") Long planId,
                                                    @Param("checkinDate") LocalDate checkinDate);

    /**
     * 原子切换打卡状态：不存在则新增，存在则翻转逻辑删除标志
     *
     * @param planId      计划ID
     * @param userId      用户ID
     * @param checkinDate 打卡日期
     * @return 影响行数
     */
    @Insert("INSERT INTO checkin_record "
            + "(plan_id, user_id, checkin_date, create_time, create_by, update_time, update_by, deleted) "
            + "VALUES (#{planId}, #{userId}, #{checkinDate}, NOW(), 'system', NOW(), 'system', 0) "
            + "ON DUPLICATE KEY UPDATE deleted = IF(deleted = 0, 1, 0), "
            + "user_id = VALUES(user_id), update_time = NOW(), update_by = 'system'")
    int atomicToggle(@Param("planId") Long planId,
                     @Param("userId") Long userId,
                     @Param("checkinDate") LocalDate checkinDate);
}
