package com.base.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.workflow.entity.ProcessNode;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 流程节点 Mapper
 */
@Mapper
public interface ProcessNodeMapper extends BaseMapper<ProcessNode> {

    List<ProcessNode> selectByProcessId(Long processId);
}
