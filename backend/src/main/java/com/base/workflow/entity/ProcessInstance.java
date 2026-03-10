package com.base.workflow.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 流程实例实体
 */
@Data
@TableName("sys_process_instance")
public class ProcessInstance implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String processKey;

    private String processName;

    private Long processDefinitionId;

    private String businessKey;

    private String businessType;

    private String currentNodeKey;

    private String currentNodeName;

    private String initiator;

    private Long initiatorDeptId;

    private String status;

    private String variables;

    private String title;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
