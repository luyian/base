package com.base.dev.dto;

import lombok.Data;

/**
 * 分支统计信息响应
 */
@Data
public class BranchStatsResponse {

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 待上线数量
     */
    private Long pendingCount;

    /**
     * 已上线数量
     */
    private Long doneCount;

    /**
     * 紧急数量
     */
    private Long urgentCount;

    /**
     * 特急数量
     */
    private Long criticalCount;

    /**
     * 本周待上线数量
     */
    private Long thisWeekCount;
}
