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
 * 流程任务实体
 */
@Data
@TableName("sys_process_task")
public class ProcessTask implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long instanceId;

    private String processKey;

    private String nodeKey;

    private String nodeName;

    private String taskType;

    private String assignee;

    private String candidateUsers;

    private String status;

    private String approveType;

    private String approveResult;

    private String comment;

    private LocalDateTime claimTime;

    private LocalDateTime completeTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
