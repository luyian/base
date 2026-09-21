package com.base.barcode.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.barcode.dto.BarcodeGenerateResult;
import com.base.barcode.dto.BarcodeListResponse;
import com.base.barcode.dto.BarcodeRecordRequest;
import com.base.barcode.entity.Barcode;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 通用条码/二维码服务接口
 *
 * @author base
 */
public interface BarcodeService {

    /**
     * 生成条码/二维码图片
     *
     * @param content 待编码内容（任意文本）
     * @param type    码制（CODE128 / QR）
     * @return 条码图片字节与 MIME 描述
     */
    BarcodeGenerateResult generate(String content, String type);

    /**
     * 登记一条通用条码记录（可与任意业务关联）
     *
     * @param userId  当前用户ID
     * @param request 记录信息
     * @return 新记录ID
     */
    Long record(Long userId, BarcodeRecordRequest request);

    /**
     * 批量生成指定数量的条码/二维码（内容由系统自动生成全局唯一编号）
     *
     * @param type  码制（CODE128 / QR）
     * @param count 生成数量（1~200）
     * @return 生成结果列表（每项含编号、归档文件、绑定态）
     */
    List<BarcodeGenerateResult> generateBatch(String type, int count);

    /**
     * 分页查询当前用户的条码/二维码列表（可过滤绑定态）
     *
     * @param userId 当前用户ID
     * @param type   类型（1条码 2二维码），可为 null 查全部
     * @param bound  绑定态过滤，true 已绑定 / false 未绑定 / null 全部
     * @param page   页码
     * @param size   每页条数
     * @return 分页列表
     */
    Page<BarcodeListResponse> listBarcodes(Long userId, Integer type, Boolean bound, long page, long size);

    /**
     * 按编码查询通用条码记录
     *
     * @param code 条码内容
     * @return 条码记录，不存在返回 null
     */
    Barcode getByCode(String code);

    /**
     * 删除一条条码记录（仅本人）
     *
     * @param userId 当前用户ID
     * @param id     记录ID
     */
    void deleteRecord(Long userId, Long id);

    /**
     * 识别图片中的条码/二维码（转发 python-tools，返回原始内容）
     *
     * @param file 上传的图片
     * @return 识别结果（含 content 原始内容）
     */
    Map<String, Object> decodeImage(MultipartFile file);
}