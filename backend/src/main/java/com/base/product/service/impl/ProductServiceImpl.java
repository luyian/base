package com.base.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.barcode.entity.Barcode;
import com.base.barcode.mapper.BarcodeMapper;
import com.base.common.exception.BusinessException;
import com.base.common.service.CosService;
import com.base.product.dto.ProductBarcodeResponse;
import com.base.product.dto.ProductRequest;
import com.base.product.dto.ProductResponse;
import com.base.product.entity.Product;
import com.base.product.mapper.ProductMapper;
import com.base.product.service.ProductService;
import com.base.system.entity.SysFile;
import com.base.system.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品服务实现类
 *
 * @author base
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    /** 商品业务类型标识 */
    public static final String BIZ_TYPE_PRODUCT = "PRODUCT";

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    /** 自生成条码来源 */
    private static final int SOURCE_GENERATED = 1;

    /** 图片识别录入来源 */
    private static final int SOURCE_SCAN = 2;

    /** 条码类型：CODE128 条形码 */
    private static final int TYPE_CODE128 = 1;

    /** 用户最大重试次数（用于并发生成唯一编码） */
    private static final int MAX_RETRY = 5;

    private final ProductMapper productMapper;
    private final BarcodeMapper barcodeMapper;
    private final FileService fileService;
    private final CosService cosService;

    @Override
    public IPage<ProductResponse> pageProducts(Long userId, String name, long page, long size) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getUserId, userId)
                .and(StringUtils.hasText(name), w -> w.like(Product::getName, name))
                .orderByDesc(Product::getId);
        Page<Product> productPage = new Page<>(page, size);
        Page<Product> resultPage = productMapper.selectPage(productPage, wrapper);

        Page<ProductResponse> responsePage = new Page<>(resultPage.getCurrent(),
                resultPage.getSize(), resultPage.getTotal());
        responsePage.setRecords(resultPage.getRecords().stream()
                .map(this::toResponse)
                .collect(Collectors.toList()));
        return responsePage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createProduct(Long userId, ProductRequest request) {
        Product product = new Product();
        applyRequest(product, request);
        product.setUserId(userId);
        // 新增商品即生成商品唯一编码（PROD + 日期 + 递增），与条码/二维码（BR/BAR）区分
        product.setCode(generateProductCode(userId));
        productMapper.insert(product);
        log.info("新增商品成功，userId: {}, name: {}, code: {}, id: {}",
                userId, request.getName(), product.getCode(), product.getId());
        return product.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProduct(Long userId, Long id, ProductRequest request) {
        Product product = checkOwnProduct(userId, id);
        applyRequest(product, request);
        // 历史数据补编码：新增编码字段前创建的商品 code 为空，编辑保存时自动补生成
        if (!StringUtils.hasText(product.getCode())) {
            product.setCode(generateProductCode(userId));
        }
        productMapper.updateById(product);
        log.info("编辑商品成功，userId: {}, id: {}", userId, id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(Long userId, Long id) {
        Product product = checkOwnProduct(userId, id);
        productMapper.deleteById(product.getId());
        log.info("删除商品成功，userId: {}, id: {}", userId, id);
    }

    @Override
    public ProductResponse getProduct(Long userId, Long id) {
        Product product = checkOwnProduct(userId, id);
        return toResponse(product);
    }

    @Override
    public List<ProductBarcodeResponse> listBarcodes(Long productId) {
        LambdaQueryWrapper<Barcode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Barcode::getBizType, BIZ_TYPE_PRODUCT)
                .eq(Barcode::getBizId, productId)
                .orderByDesc(Barcode::getId);
        List<Barcode> barcodes = barcodeMapper.selectList(wrapper);
        if (barcodes.isEmpty()) {
            return Collections.emptyList();
        }
        return barcodes.stream().map(this::toBarcodeResponse).collect(Collectors.toList());
    }

    @Override
    public ProductResponse scanByCode(Long userId, String code) {
        if (!StringUtils.hasText(code)) {
            throw new BusinessException(400, "条码内容不能为空");
        }
        LambdaQueryWrapper<Barcode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Barcode::getCode, code)
                .eq(Barcode::getBizType, BIZ_TYPE_PRODUCT)
                .last("LIMIT 1");
        Barcode barcode = barcodeMapper.selectOne(wrapper);
        if (barcode == null || barcode.getBizId() == null) {
            return null;
        }
        Product product = productMapper.selectById(barcode.getBizId());
        if (product == null || product.getDeleted() != null && product.getDeleted() == 1) {
            return null;
        }
        // 商品归属校验：仅返回本人商品
        if (!product.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权查看该商品");
        }
        return toResponse(product);
    }

    @Override
    public String generateProductCode(Long userId) {
        String prefix = "PROD" + LocalDate.now().format(DATE_FORMAT);
        for (int i = 0; i < MAX_RETRY; i++) {
            String code = prefix + String.format("%04d", productSequence());
            // 含逻辑删除查重：软删行仍占用 uk_code，须一并排除才不撞唯一键
            if (productMapper.countByCode(code) == 0) {
                return code;
            }
        }
        throw new BusinessException("生成商品编码失败，请重试");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long bindCodeToProduct(Long userId, Long productId, String code, Integer type, Integer sourceValue) {
        if (!StringUtils.hasText(code)) {
            throw new BusinessException(400, "条码内容不能为空");
        }
        if (productId == null) {
            throw new BusinessException(400, "商品ID不能为空");
        }
        // 按 code 定位记录：generate 已落库未绑定则复用打上商品 biz 标记，不存在才新建
        LambdaQueryWrapper<Barcode> existedWrapper = new LambdaQueryWrapper<>();
        existedWrapper.eq(Barcode::getCode, code).eq(Barcode::getDeleted, 0).last("LIMIT 1");
        Barcode existed = barcodeMapper.selectOne(existedWrapper);
        if (existed != null) {
            if (existed.getBizId() != null) {
                if (existed.getBizId().equals(productId)) {
                    // 已绑定本商品：幂等返回
                    return existed.getId();
                }
                throw new BusinessException(400, "该条码已绑定其他商品");
            }
            // 复用未绑定记录：补充码制/来源后打上商品标记
            if (type != null) {
                existed.setType(type);
            }
            if (sourceValue != null) {
                existed.setSource(sourceValue);
            }
            existed.setBizType(BIZ_TYPE_PRODUCT);
            existed.setBizId(productId);
            barcodeMapper.updateById(existed);
            log.info("复用并绑定条码到商品成功，productId: {}, code: {}, id: {}",
                    productId, code, existed.getId());
            return existed.getId();
        }

        Barcode barcode = new Barcode();
        barcode.setCode(code);
        barcode.setType(type != null ? type : TYPE_CODE128);
        barcode.setSource(sourceValue != null ? sourceValue : SOURCE_GENERATED);
        barcode.setBizType(BIZ_TYPE_PRODUCT);
        barcode.setBizId(productId);
        barcode.setUserId(userId);
        barcodeMapper.insert(barcode);
        log.info("新建并绑定条码到商品成功，productId: {}, code: {}, type: {}, source: {}",
                productId, code, type, sourceValue);
        return barcode.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unbindCode(Long userId, Long productId, Long codeId) {
        Product product = checkOwnProduct(userId, productId);
        if (productId == null) {
            throw new BusinessException(400, "商品ID不能为空");
        }
        Barcode barcode = barcodeMapper.selectById(codeId);
        if (barcode == null || (barcode.getDeleted() != null && barcode.getDeleted() == 1)) {
            throw new BusinessException(404, "条码记录不存在");
        }
        if (barcode.getBizId() == null || !barcode.getBizId().equals(productId)
                || !BIZ_TYPE_PRODUCT.equals(barcode.getBizType())) {
            throw new BusinessException(400, "该条码不属于此商品");
        }
        // 软解绑：清业务关联，保留码记录与已归档图片（生成页仍可作为未绑定码存在）
        // 注：updateById 默认跳过 null 字段，须用 UpdateWrapper 显式置 NULL 才能清空
        barcodeMapper.update(null, new LambdaUpdateWrapper<Barcode>()
                .eq(Barcode::getId, codeId)
                .set(Barcode::getBizType, null)
                .set(Barcode::getBizId, null));
        log.info("软解绑商品条码成功，userId: {}, productId: {}, codeId: {}", userId, productId, codeId);
    }

    /**
     * 生成商品编码递增序号（当天维度，基于商品表编码统计）
     * 商品编码全局唯一，统计须含逻辑删除记录（软删行仍占用 uk_code），故不做用户维度过滤
     *
     * @return 序号（1 起）
     */
    private long productSequence() {
        String prefix = "PROD" + LocalDate.now().format(DATE_FORMAT);
        // 含逻辑删除统计：软删行仍占用 uk_code，序号须跳过它们，避免生成到已占用的编号
        return productMapper.countByCodePrefix(prefix) + 1;
    }

    /**
     * 校验商品存在且属于当前用户
     */
    private Product checkOwnProduct(Long userId, Long id) {
        Product product = productMapper.selectById(id);
        if (product == null || (product.getDeleted() != null && product.getDeleted() == 1)) {
            throw new BusinessException(404, "商品不存在");
        }
        if (!product.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作该商品");
        }
        return product;
    }

    /**
     * 复制请求字段到商品实体
     */
    private void applyRequest(Product product, ProductRequest request) {
        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setSpec(request.getSpec());
        product.setUnit(request.getUnit());
        product.setSalePrice(request.getSalePrice());
        product.setCostPrice(request.getCostPrice());
        product.setStock(request.getStock() != null ? request.getStock() : 0);
        product.setSupplier(request.getSupplier());
        product.setProductionDate(request.getProductionDate());
        product.setImageUrl(request.getImageUrl());
        product.setRemark(request.getRemark());
    }

    /**
     * 商品实体转响应
     */
    private ProductResponse toResponse(Product product) {
        ProductResponse response = new ProductResponse();
        BeanUtils.copyProperties(product, response);
        return response;
    }

    /**
     * 条码实体转响应（回显条码图片地址）
     */
    private ProductBarcodeResponse toBarcodeResponse(Barcode barcode) {
        ProductBarcodeResponse response = new ProductBarcodeResponse();
        response.setId(barcode.getId());
        response.setCode(barcode.getCode());
        response.setType(barcode.getType() != null ? barcode.getType() : TYPE_CODE128);
        response.setSource(barcode.getSource() != null ? barcode.getSource() : SOURCE_GENERATED);
        response.setFileUrl(resolveBarcodeFileUrl(barcode.getFileId()));
        response.setCreateTime(barcode.getCreateTime());
        return response;
    }

    /**
     * 据条码归档文件ID解析预签名图片 URL（file_id → sys_file.file_path → COS URL）
     */
    private String resolveBarcodeFileUrl(Long fileId) {
        if (fileId == null) {
            return null;
        }
        SysFile sysFile = fileService.getFileById(fileId);
        if (sysFile == null || sysFile.getFilePath() == null) {
            return null;
        }
        return cosService.getFileUrl(sysFile.getFilePath());
    }
}