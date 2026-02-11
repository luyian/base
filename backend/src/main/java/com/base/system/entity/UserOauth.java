package com.base.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户第三方登录绑定实体
 */
@Data
@TableName("sys_user_oauth")
@ApiModel("用户第三方登录绑定")
public class UserOauth implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty("主键ID")
    private Long id;

    /**
     * 系统用户ID
     */
    @ApiModelProperty("系统用户ID")
    private Long userId;

    /**
     * 第三方平台类型（github/wechat/gitee）
     */
    @ApiModelProperty("第三方平台类型")
    private String oauthType;

    /**
     * 第三方平台用户唯一标识
     */
    @ApiModelProperty("第三方平台用户唯一标识")
    private String oauthId;

    /**
     * 第三方平台用户名
     */
    @ApiModelProperty("第三方平台用户名")
    private String oauthName;

    /**
     * 第三方平台头像
     */
    @ApiModelProperty("第三方平台头像")
    private String oauthAvatar;

    /**
     * 第三方平台邮箱
     */
    @ApiModelProperty("第三方平台邮箱")
    private String oauthEmail;

    /**
     * access_token
     */
    @ApiModelProperty("access_token")
    private String accessToken;

    /**
     * 绑定时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("绑定时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
}
