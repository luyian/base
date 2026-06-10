package com.base.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.system.dto.dict.DictTypeQueryRequest;
import com.base.system.dto.dict.DictTypeResponse;
import com.base.system.dto.dict.DictTypeSaveRequest;

import java.util.List;

/**
 * 字典类型服务接口
 *
 * @author base
 * @since 2026-06-09
 */
public interface DictTypeService {

    /**
     * 分页查询字典类型
     *
     * @param request 查询参数
     * @return 分页结果
     */
    Page<DictTypeResponse> pageDictTypes(DictTypeQueryRequest request);

    /**
     * 根据ID获取字典类型详情
     *
     * @param id 主键ID
     * @return 字典类型详情
     */
    DictTypeResponse getDictTypeById(Long id);

    /**
     * 查询所有字典类型列表
     *
     * @return 字典类型列表
     */
    List<DictTypeResponse> listAllDictTypes();

    /**
     * 新增字典类型
     *
     * @param request 保存参数
     */
    void addDictType(DictTypeSaveRequest request);

    /**
     * 编辑字典类型
     *
     * @param request 保存参数
     */
    void updateDictType(DictTypeSaveRequest request);

    /**
     * 删除字典类型
     *
     * @param id 主键ID
     */
    void deleteDictType(Long id);

    /**
     * 批量删除字典类型
     *
     * @param ids ID列表
     */
    void batchDeleteDictTypes(List<Long> ids);

    /**
     * 刷新字典缓存
     */
    void refreshCache();
}
