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
 * 流程历史记录实体
 */
@Data
@TableName("sys_process_history")
public class ProcessHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long instanceId;

    private String processKey;

    private String nodeKey;

    private String nodeName;

    private String operator;

    private String operatorName;

    private String action;

    private String assignee;

    private String comment;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
