package com.base.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.system.entity.SysFileLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件操作日志 Mapper 接口
 */
@Mapper
public interface SysFileLogMapper extends BaseMapper<SysFileLog> {
}