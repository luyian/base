package com.base.stock.recommend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.stock.recommend.entity.RecommendStock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;

/**
 * 推荐股票Mapper
 *
 * @author base
 */
@Mapper
public interface RecommendStockMapper extends BaseMapper<RecommendStock> {

    /**
     * 更新排名
     *
     * @param recommendDate 推荐日期
     */
    @Update("UPDATE stk_recommend r1 " +
            "SET r1.rank = ( " +
            "    SELECT COUNT(*) + 1 " +
            "    FROM stk_recommend r2 " +
            "    WHERE r2.recommend_date = #{recommendDate} " +
            "    AND r2.total_score > r1.total_score " +
            ") " +
            "WHERE r1.recommend_date = #{recommendDate}")
    void updateRank(@Param("recommendDate") LocalDate recommendDate);
}
