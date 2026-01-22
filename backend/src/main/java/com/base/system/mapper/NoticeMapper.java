package com.base.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.system.entity.Notice;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通知公告 Mapper 接口
 *
 * @author base
 * @since 2026-01-13
 */
@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {
}
