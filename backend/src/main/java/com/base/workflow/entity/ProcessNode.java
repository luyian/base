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
 * 流程节点配置实体
 */
@Data
@TableName("sys_process_node")
public class ProcessNode implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long processId;

    private String nodeKey;

    private String nodeName;

    private String nodeType;

    private String eventHandler;

    private String candidateType;

    private String candidateConfig;

    private String approveType;

    private Double approveRatio;

    private Integer canRollback;

    private String rollbackNodes;

    private Integer positionX;

    private Integer positionY;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
