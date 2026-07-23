package com.base.checkin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.checkin.entity.CheckinPlan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 打卡计划 Mapper 接口
 *
 * @author base
 */
@Mapper
public interface CheckinPlanMapper extends BaseMapper<CheckinPlan> {

    /**
     * 锁定查询未删除计划，保证打卡与删除操作串行执行
     *
     * @param id 计划ID
     * @return 计划实体
     */
    @Select("SELECT * FROM checkin_plan WHERE id = #{id} AND deleted = 0 FOR UPDATE")
    CheckinPlan selectByIdForUpdate(@Param("id") Long id);

    /**
     * 查询用户全部启用计划，包含已逻辑删除的数据
     *
     * @param userId 用户ID
     * @return 计划列表
     */
    @Select("SELECT * FROM checkin_plan WHERE user_id = #{userId} AND status = 1")
    List<CheckinPlan> selectEnabledByUserIdIncludeDeleted(@Param("userId") Long userId);

    /**
     * 逻辑删除计划并记录删除时间
     *
     * @param id          计划ID
     * @param userId      用户ID
     * @param deletedTime 删除时间
     * @return 影响行数
     */
    @Update("UPDATE checkin_plan SET deleted = 1, deleted_time = #{deletedTime}, "
            + "update_time = #{deletedTime}, update_by = 'system' "
            + "WHERE id = #{id} AND user_id = #{userId} AND deleted = 0")
    int logicDeleteById(@Param("id") Long id,
                        @Param("userId") Long userId,
                        @Param("deletedTime") LocalDateTime deletedTime);
}
