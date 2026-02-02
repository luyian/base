package com.base.stock.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.stock.entity.Watchlist;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 自选股票 Mapper 接口
 *
 * @author base
 */
@Mapper
public interface WatchlistMapper extends BaseMapper<Watchlist> {

    /**
     * 查询用户自选列表（关联股票信息）
     *
     * @param userId 用户ID
     * @return 自选列表
     */
    @Select("SELECT w.*, s.stock_name, s.market " +
            "FROM stk_watchlist w " +
            "LEFT JOIN stk_stock_info s ON w.stock_code = s.stock_code " +
            "WHERE w.user_id = #{userId} AND w.deleted = 0 " +
            "ORDER BY w.sort_order ASC, w.create_time DESC")
    List<Watchlist> selectListWithStockInfo(@Param("userId") Long userId);
}
