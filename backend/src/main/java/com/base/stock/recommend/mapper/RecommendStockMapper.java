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

}
