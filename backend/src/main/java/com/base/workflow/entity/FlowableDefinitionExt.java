package com.base.workflow.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Flowable 流程定义扩展实体
 */
@Data
@TableName("sys_flowable_definition_ext")
public class FlowableDefinitionExt implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** Flowable 部署ID */
    private String deploymentId;

    /** Flowable 流程定义ID */
    private String processDefinitionId;

    /** 流程标识 */
    private String processKey;

    /** 流程名称 */
    private String processName;

    /** 流程分类 */
    private String category;

    /** 描述 */
    private String description;

    /** 版本号 */
    private Integer version;

    /** 状态(0草稿 1已发布 2禁用) */
    private Integer status;

    /** BPMN XML 内容 */
    private String bpmnXml;

    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
