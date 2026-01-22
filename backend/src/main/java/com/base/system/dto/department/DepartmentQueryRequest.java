package com.base.system.dto.department;

import lombok.Data;

/**
 * 部门查询请求参数
 */
@Data
public class DepartmentQueryRequest {

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 部门编码
     */
    private String deptCode;

    /**
     * 状态（0-禁用 1-正常）
     */
    private Integer status;
}
