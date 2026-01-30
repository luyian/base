package com.base.stock.factory;

import java.util.List;

/**
 * 数据工厂接口
 * 负责源数据到目标数据的转换
 *
 * @author base
 */
public interface DataFactory {

    /**
     * 根据映射配置转换数据
     *
     * @param sourceJson  源 JSON 数据
     * @param mappingCode 映射编码
     * @param targetClass 目标类型
     * @param <T>         泛型类型
     * @return 转换后的对象列表
     */
    <T> List<T> transform(String sourceJson, String mappingCode, Class<T> targetClass);

    /**
     * 根据映射配置转换单个对象
     *
     * @param sourceJson  源 JSON 数据
     * @param mappingCode 映射编码
     * @param targetClass 目标类型
     * @param <T>         泛型类型
     * @return 转换后的对象
     */
    <T> T transformOne(String sourceJson, String mappingCode, Class<T> targetClass);
}
