package com.base.stock.recommend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.base.stock.recommend.entity.ScoreRule;

import java.util.List;

/**
 * 打分规则配置服务接口
 *
 * @author base
 */
public interface ScoreRuleService extends IService<ScoreRule> {

    /**
     * 查询所有启用的规则（按排序号升序）
     *
     * @return 规则列表
     */
    List<ScoreRule> listEnabledRules();

    /**
     * 根据规则编码查询规则
     *
     * @param ruleCode 规则编码
     * @return 规则配置
     */
    ScoreRule getByRuleCode(String ruleCode);

    /**
     * 启用规则
     *
     * @param id 规则ID
     * @return 是否成功
     */
    boolean enableRule(Long id);

    /**
     * 禁用规则
     *
     * @param id 规则ID
     * @return 是否成功
     */
    boolean disableRule(Long id);
}
