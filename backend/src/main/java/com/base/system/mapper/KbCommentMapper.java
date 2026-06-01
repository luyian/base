package com.base.system.mapper;

import com.base.system.entity.KbComment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 知识库文档评论 Mapper 接口
 */
@Mapper
public interface KbCommentMapper extends BaseMapper<KbComment> {
}
