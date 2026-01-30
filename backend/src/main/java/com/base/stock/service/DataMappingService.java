package com.base.stock.service;

import com.base.stock.entity.DataMapping;

import java.util.List;

/**
 * 数据映射配置服务接口
 *
 * @author base
 */
public interface DataMappingService {

    /**
     * 查询映射配置列表
     *
     * @return 映射配置列表
     */
    List<DataMapping> listMappings();

    /**
     * 根据编码获取映射配置
     *
     * @param mappingCode 映射编码
     * @return 映射配置
     */
    DataMapping getByCode(String mappingCode);

    /**
     * 根据 ID 获取映射配置
     *
     * @param id 映射 ID
     * @return 映射配置
     */
    DataMapping getById(Long id);

    /**
     * 保存映射配置
     *
     * @param dataMapping 映射配置
     * @return 映射 ID
     */
    Long saveMapping(DataMapping dataMapping);

    /**
     * 更新映射配置
     *
     * @param dataMapping 映射配置
     */
    void updateMapping(DataMapping dataMapping);

    /**
     * 删除映射配置
     *
     * @param id 映射 ID
     */
    void deleteMapping(Long id);
}
