package com.base.dev.dto;

import com.base.common.dto.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 分支管理查询请求参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BranchQueryRequest extends BasePageRequest {

    /**
     * 编号
     */
    private String code;

    /**
     * 标题
     */
    private String title;

    /**
     * 生产分支
     */
    private String prodBranch;

    /**
     * 状态：0-进行中，1-已完成
     */
    private Integer status;
}
