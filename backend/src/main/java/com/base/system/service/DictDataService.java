package com.base.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.system.dto.dict.DictDataQueryRequest;
import com.base.system.dto.dict.DictDataResponse;
import com.base.system.dto.dict.DictDataSaveRequest;

import java.util.List;

/**
 * 字典数据服务接口
 *
 * @author base
 * @since 2026-06-09
 */
public interface DictDataService {

    /**
     * 分页查询字典数据
     *
     * @param request 查询参数
     * @return 分页结果
     */
    Page<DictDataResponse> pageDictData(DictDataQueryRequest request);

    /**
     * 根据ID获取字典数据详情
     *
     * @param id 主键ID
     * @return 字典数据详情
     */
    DictDataResponse getDictDataById(Long id);

    /**
     * 根据字典类型查询数据列表
     *
     * @param dictType 字典类型编码
     * @return 字典数据列表
     */
    List<DictDataResponse> listByDictType(String dictType);

    /**
     * 新增字典数据
     *
     * @param request 保存参数
     */
    void addDictData(DictDataSaveRequest request);

    /**
     * 编辑字典数据
     *
     * @param request 保存参数
     */
    void updateDictData(DictDataSaveRequest request);

    /**
     * 删除字典数据
     *
     * @param id 主键ID
     */
    void deleteDictData(Long id);

    /**
     * 批量删除字典数据
     *
     * @param ids ID列表
     */
    void batchDeleteDictData(List<Long> ids);

    /**
     * 根据字典类型删除所有数据
     *
     * @param dictType 字典类型编码
     */
    void deleteByDictType(String dictType);
}
