package com.base.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.base.product.dto.ProductBarcodeResponse;
import com.base.product.dto.ProductRequest;
import com.base.product.dto.ProductResponse;

import java.util.List;

/**
 * 商品服务接口
 *
 * @author base
 */
public interface ProductService {

    /**
     * 分页查询我的商品
     *
     * @param userId 用户ID
     * @param name   商品名称（模糊，可选）
     * @param page  页码（从1开始）
     * @param size  每页条数
     * @return 商品分页
     */
    IPage<ProductResponse> pageProducts(Long userId, String name, long page, long size);

    /**
     * 新增商品
     *
     * @param userId  用户ID
     * @param request 商品信息
     * @return 商品ID
     */
    Long createProduct(Long userId, ProductRequest request);

    /**
     * 编辑商品（仅本人）
     *
     * @param userId  用户ID
     * @param id      商品ID
     * @param request 商品信息
     */
    void updateProduct(Long userId, Long id, ProductRequest request);

    /**
     * 删除商品（仅本人，逻辑删除）
     *
     * @param userId 用户ID
     * @param id     商品ID
     */
    void deleteProduct(Long userId, Long id);

    /**
     * 查询商品详情（含关联条码列表）
     *
     * @param userId 用户ID
     * @param id     商品ID
     * @return 商品信息
     */
    ProductResponse getProduct(Long userId, Long id);

    /**
     * 查询商品关联条码列表
     *
     * @param productId 商品ID
     * @return 条码列表
     */
    List<ProductBarcodeResponse> listBarcodes(Long productId);

    /**
     * 扫码命中：编码 → 商品
     *
     * @param userId 当前用户ID
     * @param code   条码内容
     * @return 商品信息（未命中返回 null）
     */
    ProductResponse scanByCode(Long userId, String code);

    /**
     * 生成一个新的业务唯一编码（PROD + 日期 + 递增）
     *
     * @param userId 用户ID
     * @return 业务编码
     */
    String generateProductCode(Long userId);

    /**
     * 绑定一条条码到商品（登记 t_barcode，bizType=PRODUCT）
     *
     * @param userId      用户ID（归属性，条码列表按用户过滤）
     * @param productId   商品ID
     * @param code        条码内容
     * @param type        条码类型（1条码 2二维码）
     * @param sourceValue 来源（1自生成 2图片识别录入）
     * @return 条码记录ID
     */
    Long bindCodeToProduct(Long userId, Long productId, String code, Integer type, Integer sourceValue);

    /**
     * 解绑一条商品条码（仅本人商品）
     *
     * @param userId   用户ID
     * @param productId 商品ID
     * @param codeId   条码记录ID
     */
    void unbindCode(Long userId, Long productId, Long codeId);
}