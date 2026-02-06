package com.base.stock.recommend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.base.stock.recommend.entity.ScoreRule;
import com.base.stock.recommend.mapper.ScoreRuleMapper;
import com.base.stock.recommend.service.ScoreRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 打分规则配置服务实现类
 *
 * @author base
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScoreRuleServiceImpl extends ServiceImpl<ScoreRuleMapper, ScoreRule> implements ScoreRuleService {

    @Override
    public List<ScoreRule> listEnabledRules() {
        return this.list(
                new LambdaQueryWrapper<ScoreRule>()
                        .eq(ScoreRule::getStatus, 1)
                        .eq(ScoreRule::getDeleted, 0)
                        .orderByAsc(ScoreRule::getSortOrder)
        );
    }

    @Override
    public ScoreRule getByRuleCode(String ruleCode) {
        return this.getOne(
                new LambdaQueryWrapper<ScoreRule>()
                        .eq(ScoreRule::getRuleCode, ruleCode)
                        .eq(ScoreRule::getDeleted, 0)
        );
    }

    @Override
    public boolean enableRule(Long id) {
        ScoreRule rule = this.getById(id);
        if (rule == null) {
            return false;
        }
        rule.setStatus(1);
        return this.updateById(rule);
    }

    @Override
    public boolean disableRule(Long id) {
        ScoreRule rule = this.getById(id);
        if (rule == null) {
            return false;
        }
        rule.setStatus(0);
        return this.updateById(rule);
    }
}
