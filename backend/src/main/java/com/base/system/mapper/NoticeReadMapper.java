package com.base.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.system.entity.NoticeRead;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通知阅读记录 Mapper
 */
@Mapper
public interface NoticeReadMapper extends BaseMapper<NoticeRead> {
}
