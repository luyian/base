package com.base.stock.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.stock.entity.FundValuationRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

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

    /**
     * 批量查询指定日期的估值记录
     *
     * @param fundIds   基金ID列表
     * @param tradeDate 交易日期
     * @return 估值记录列表
     */
    @Select("<script>" +
            "SELECT * FROM stk_fund_valuation_record WHERE trade_date = #{tradeDate} AND fund_id IN " +
            "<foreach collection='fundIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<FundValuationRecord> selectByFundIdsAndDate(@Param("fundIds") List<Long> fundIds, @Param("tradeDate") LocalDate tradeDate);
}
