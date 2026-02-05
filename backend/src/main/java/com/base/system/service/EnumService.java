package com.base.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.system.dto.enums.EnumItemSaveRequest;
import com.base.system.dto.enums.EnumQueryRequest;
import com.base.system.dto.enums.EnumResponse;
import com.base.system.dto.enums.EnumSaveRequest;
import com.base.system.dto.enums.EnumTypeResponse;

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

    /**
     * 查询所有枚举类型列表（按类型分组）
     */
    List<EnumTypeResponse> listEnumTypes();

    /**
     * 批量保存某类型下的枚举项
     *
     * @param enumType 枚举类型
     * @param typeDesc 枚举类型中文描述
     * @param items    枚举项列表
     */
    void batchSaveByType(String enumType, String typeDesc, List<EnumItemSaveRequest> items);

    /**
     * 按类型删除所有枚举项
     */
    void deleteByType(String enumType);
}
