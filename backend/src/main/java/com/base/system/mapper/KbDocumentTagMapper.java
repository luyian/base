package com.base.system.mapper;

import com.base.system.entity.KbDocumentTag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文档-标签关联 Mapper 接口
 */
@Mapper
public interface KbDocumentTagMapper extends BaseMapper<KbDocumentTag> {
}
