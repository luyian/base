package com.base.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.product.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 商品 Mapper 接口
 *
 * @author base
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    /**
     * 统计商品编码以指定前缀开头的记录数（含逻辑删除，软删行仍占用 uk_code 唯一键）
     *
     * @param prefix 编码前缀（如 PROD20260921）
     * @return 记录数
     */
    @Select("SELECT COUNT(*) FROM t_product WHERE code LIKE CONCAT(#{prefix}, '%')")
    long countByCodePrefix(@Param("prefix") String prefix);

    /**
     * 判断商品编码是否已存在（含逻辑删除，软删行仍占用 uk_code 唯一键）
     *
     * @param code 商品编码
     * @return 存在返回 true
     */
    @Select("SELECT COUNT(*) FROM t_product WHERE code = #{code}")
    int countByCode(@Param("code") String code);
}