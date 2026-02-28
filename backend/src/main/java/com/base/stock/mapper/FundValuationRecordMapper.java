package com.base.stock.fund.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.stock.fund.entity.FundValuationRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;

/**
 * 基金估值记录 Mapper 接口
 *
 * @author base
 */
@Mapper
public interface FundValuationRecordMapper extends BaseMapper<FundValuationRecord> {

    /**
     * 查询指定基金指定日期的估值记录
     *
     * @param fundId    基金ID
     * @param tradeDate 交易日期
     * @return 估值记录
     */
    @Select("SELECT * FROM stk_fund_valuation_record WHERE fund_id = #{fundId} AND trade_date = #{tradeDate} LIMIT 1")
    FundValuationRecord selectByFundIdAndDate(@Param("fundId") Long fundId, @Param("tradeDate") LocalDate tradeDate);
}
