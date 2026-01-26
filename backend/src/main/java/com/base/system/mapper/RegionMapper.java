package com.base.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.system.entity.Region;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 行政区划 Mapper
 */
@Mapper
public interface RegionMapper extends BaseMapper<Region> {

    /**
     * 递归查询区划及所有子区划ID
     *
     * @param regionId 区划ID
     * @return 区划ID列表
     */
    @Select("WITH RECURSIVE region_tree AS (" +
            "  SELECT id FROM sys_region WHERE id = #{regionId} AND deleted = 0 " +
            "  UNION ALL " +
            "  SELECT r.id FROM sys_region r " +
            "  INNER JOIN region_tree rt ON r.parent_id = rt.id " +
            "  WHERE r.deleted = 0" +
            ") SELECT id FROM region_tree")
    List<Long> selectRegionAndChildIds(@Param("regionId") Long regionId);

    /**
     * 根据区划代码批量查询
     *
     * @param regionCodes 区划代码列表
     * @return 区划列表
     */
    List<Region> selectByRegionCodes(@Param("regionCodes") List<String> regionCodes);
}
