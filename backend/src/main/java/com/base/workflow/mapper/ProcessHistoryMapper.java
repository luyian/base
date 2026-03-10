package com.base.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.workflow.entity.ProcessHistory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 流程历史记录 Mapper
 */
@Mapper
public interface ProcessHistoryMapper extends BaseMapper<ProcessHistory> {

    List<ProcessHistory> selectByInstanceId(Long instanceId);
}
