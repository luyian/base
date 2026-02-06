package com.base.stock.recommend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.stock.recommend.entity.ScoreRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 打分记录Mapper
 *
 * @author base
 */
@Mapper
public interface ScoreRecordMapper extends BaseMapper<ScoreRecord> {
}
