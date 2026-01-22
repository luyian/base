package com.base.system.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 用户保存请求参数（新增/编辑）
 */
@Data
@ApiModel(description = "用户保存请求参数")
public class UserSaveRequest {

    @ApiModelProperty(value = "用户ID（编辑时必填）")
    private Long id;

    @ApiModelProperty(value = "用户名", required = true)
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]{4,20}$", message = "用户名必须是4-20位字母、数字或下划线")
    private String username;

    @ApiModelProperty(value = "密码（新增时必填，编辑时选填）")
    private String password;

    @ApiModelProperty(value = "昵称", required = true)
    @NotBlank(message = "昵称不能为空")
    private String nickname;

    @ApiModelProperty(value = "邮箱")
    @Email(message = "邮箱格式不正确")
    private String email;

    @ApiModelProperty(value = "手机号")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @ApiModelProperty(value = "性别（0-女，1-男，2-未知）")
    private Integer gender;

    @ApiModelProperty(value = "部门ID")
    private Long deptId;

    @ApiModelProperty(value = "状态（0-禁用，1-启用）")
    private Integer status;

    @ApiModelProperty(value = "备注")
    private String remark;
}
