package com.base.stock.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.stock.entity.StockKline;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;

/**
 * K线数据 Mapper 接口
 *
 * @author base
 */
@Mapper
public interface StockKlineMapper extends BaseMapper<StockKline> {

    /**
     * 查询股票最新交易日期
     *
     * @param stockCode 股票代码
     * @return 最新交易日期
     */
    @Select("SELECT MAX(trade_date) FROM stk_kline_daily WHERE stock_code = #{stockCode}")
    LocalDate selectLatestTradeDate(@Param("stockCode") String stockCode);
}
