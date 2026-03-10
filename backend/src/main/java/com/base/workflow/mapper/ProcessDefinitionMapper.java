package com.base.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.workflow.entity.ProcessDefinition;
import org.apache.ibatis.annotations.Mapper;

/**
 * 流程定义 Mapper
 */
@Mapper
public interface ProcessDefinitionMapper extends BaseMapper<ProcessDefinition> {
}
