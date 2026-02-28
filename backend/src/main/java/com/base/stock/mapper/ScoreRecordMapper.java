package com.base.stock.recommend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.stock.recommend.entity.ScoreRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 打分记录Mapper
 *
 * @author base
 */
@Mapper
public interface ScoreRecordMapper extends BaseMapper<ScoreRecord> {

    /**
     * 批量插入打分记录
     *
     * @param records 打分记录列表
     * @return 插入数量
     */
    int batchInsert(@Param("list") List<ScoreRecord> records);
}
