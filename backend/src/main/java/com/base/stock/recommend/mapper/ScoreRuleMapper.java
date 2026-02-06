package com.base.stock.recommend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.stock.recommend.entity.ScoreRule;
import org.apache.ibatis.annotations.Mapper;

/**
 * 打分规则配置Mapper
 *
 * @author base
 */
@Mapper
public interface ScoreRuleMapper extends BaseMapper<ScoreRule> {
}
