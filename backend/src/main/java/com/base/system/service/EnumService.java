package com.base.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.system.dto.enums.EnumQueryRequest;
import com.base.system.dto.enums.EnumResponse;
import com.base.system.dto.enums.EnumSaveRequest;

import java.util.List;

/**
 * 枚举服务接口
 */
public interface EnumService {

    /**
     * 分页查询枚举列表
     */
    Page<EnumResponse> pageEnums(EnumQueryRequest request);

    /**
     * 根据ID获取枚举详情
     */
    EnumResponse getEnumById(Long id);

    /**
     * 新增枚举
     */
    void addEnum(EnumSaveRequest request);

    /**
     * 编辑枚举
     */
    void updateEnum(EnumSaveRequest request);

    /**
     * 删除枚举
     */
    void deleteEnum(Long id);

    /**
     * 批量删除枚举
     */
    void batchDeleteEnums(List<Long> ids);

    /**
     * 根据枚举类型查询枚举列表
     */
    List<EnumResponse> listByType(String enumType);

    /**
     * 刷新枚举缓存
     */
    void refreshCache();
}
