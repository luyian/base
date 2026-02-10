package com.base.stock.recommend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.stock.recommend.entity.RecommendStock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 推荐股票Mapper
 *
 * @author base
 */
@Mapper
public interface RecommendStockMapper extends BaseMapper<RecommendStock> {

    /**
     * 批量插入推荐记录
     *
     * @param records 推荐记录列表
     * @return 插入数量
     */
    int batchInsert(@Param("list") List<RecommendStock> records);
}
