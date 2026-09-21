package com.base.barcode.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.base.barcode.entity.Barcode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 通用条码/二维码记录 Mapper 接口
 *
 * @author base
 */
@Mapper
public interface BarcodeMapper extends BaseMapper<Barcode> {

    /**
     * 统计指定前缀的条码编号数量（含软删行）。
     * <p>uk_code 全局唯一且逻辑删除行仍占用索引，MyBatis-Plus 内置方法会过滤 deleted=0，
     * 导致软删行占用的编号被重新分配而触发 Duplicate entry，故用原生 SQL 连软删一并统计。
     */
    @Select("SELECT COUNT(*) FROM t_barcode WHERE code LIKE CONCAT(#{prefix}, '%')")
    long countAllByCodePrefix(@Param("prefix") String prefix);

    /**
     * 判断编号是否被任一记录（含软删行）占用。
     */
    @Select("SELECT COUNT(*) FROM t_barcode WHERE code = #{code}")
    int countAllByCode(@Param("code") String code);
}