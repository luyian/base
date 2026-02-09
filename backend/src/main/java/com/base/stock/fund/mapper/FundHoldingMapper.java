package com.base.stock.fund.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.stock.fund.entity.FundHolding;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 基金持仓 Mapper 接口
 *
 * @author base
 */
@Mapper
public interface FundHoldingMapper extends BaseMapper<FundHolding> {

    /**
     * 查询基金持仓列表（关联股票信息）
     *
     * @param fundId 基金ID
     * @return 持仓列表
     */
    @Select("SELECT h.*, s.stock_name, s.market " +
            "FROM stk_fund_holding h " +
            "LEFT JOIN stk_stock_info s ON h.stock_code = s.stock_code " +
            "WHERE h.fund_id = #{fundId} " +
            "ORDER BY h.weight DESC")
    List<FundHolding> selectHoldingsWithStockInfo(@Param("fundId") Long fundId);
}
