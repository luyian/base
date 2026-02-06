package com.base.stock.recommend.strategy;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 打分结果
 *
 * @author base
 */
@Data
@Builder
public class ScoreResult {

    /**
     * 是否命中规则
     */
    private boolean hit;

    /**
     * 得分
     */
    private double score;

    /**
     * 打分详情（用于记录计算过程）
     */
    private Map<String, Object> detail;

    /**
     * 失败原因（未命中或计算失败时）
     */
    private String reason;

    /**
     * 创建命中结果
     *
     * @param score  得分
     * @param detail 打分详情
     * @return 打分结果
     */
    public static ScoreResult hit(double score, Map<String, Object> detail) {
        return ScoreResult.builder()
                .hit(true)
                .score(score)
                .detail(detail != null ? detail : new HashMap<>())
                .build();
    }

    /**
     * 创建未命中结果
     *
     * @param reason 未命中原因
     * @return 打分结果
     */
    public static ScoreResult miss(String reason) {
        return ScoreResult.builder()
                .hit(false)
                .score(0)
                .reason(reason)
                .detail(new HashMap<>())
                .build();
    }
}
