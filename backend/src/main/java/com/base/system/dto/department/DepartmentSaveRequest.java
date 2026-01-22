package com.base.system.dto.department;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 部门保存请求参数
 */
@Data
public class DepartmentSaveRequest {

    /**
     * 部门ID（编辑时必填）
     */
    private Long id;

    /**
     * 父级ID（0表示顶级部门）
     */
    @NotNull(message = "父级ID不能为空")
    private Long parentId;

    /**
     * 部门名称
     */
    @NotBlank(message = "部门名称不能为空")
    @Size(min = 2, max = 20, message = "部门名称长度必须在2-20个字符之间")
    private String deptName;

    /**
     * 部门编码
     */
    @NotBlank(message = "部门编码不能为空")
    @Size(min = 2, max = 20, message = "部门编码长度必须在2-20个字符之间")
    private String deptCode;

    /**
     * 负责人
     */
    @Size(max = 20, message = "负责人长度不能超过20个字符")
    private String leader;

    /**
     * 联系电话
     */
    @Size(max = 20, message = "联系电话长度不能超过20个字符")
    private String phone;

    /**
     * 邮箱
     */
    @Size(max = 50, message = "邮箱长度不能超过50个字符")
    private String email;

    /**
     * 状态（0-禁用 1-正常）
     */
    @NotNull(message = "状态不能为空")
    private Integer status;

    /**
     * 排序
     */
    @NotNull(message = "排序不能为空")
    private Integer sort;

    /**
     * 备注
     */
    @Size(max = 200, message = "备注长度不能超过200个字符")
    private String remark;
}
