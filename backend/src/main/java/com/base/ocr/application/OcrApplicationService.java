package com.base.ocr.application;

import com.base.ocr.domain.enums.OcrSceneEnum;
import com.base.ocr.domain.model.BankCardResult;
import com.base.ocr.domain.model.IdCardResult;
import com.base.ocr.domain.model.InvoiceResult;
import com.base.ocr.domain.service.OcrProvider;
import com.base.ocr.infrastructure.factory.OcrProviderFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * OCR 应用服务
 * <p>
 * 编排领域逻辑：选择供应商、调用识别、主备降级处理。
 * </p>
 *
 * @author base
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OcrApplicationService {

    private final OcrProviderFactory providerFactory;

    /**
     * 身份证识别（支持主备降级）
     *
     * @param imageData 图片数据
     * @param side      正面 front / 背面 back
     * @param provider  指定供应商（可选，为空则使用主数据源）
     * @return 识别结果
     */
    public IdCardResult recognizeIdCard(byte[] imageData, String side, String provider) {
        OcrProvider primary = resolveProvider(provider);
        IdCardResult result = primary.recognizeIdCard(imageData, side);

        if (result == null) {
            log.warn("OCR 身份证识别失败，供应商: {}，尝试降级", primary.getName());
            OcrProvider fallback = providerFactory.getFallbackProvider();
            if (fallback != null && !fallback.getName().equals(primary.getName())) {
                result = fallback.recognizeIdCard(imageData, side);
                if (result != null) {
                    log.info("OCR 身份证识别降级成功，使用供应商: {}", fallback.getName());
                }
            }
        }

        return result;
    }

    /**
     * 发票识别（支持主备降级）
     *
     * @param fileData 文件数据（图片或PDF）
     * @param isPdf    是否为PDF文件
     * @param provider 指定供应商（可选）
     * @return 识别结果
     */
    public InvoiceResult recognizeInvoice(byte[] fileData, boolean isPdf, String provider) {
        OcrProvider primary = resolveProvider(provider);
        InvoiceResult result = primary.recognizeInvoice(fileData, isPdf);

        if (result == null) {
            log.warn("OCR 发票识别失败，供应商: {}，尝试降级", primary.getName());
            OcrProvider fallback = providerFactory.getFallbackProvider();
            if (fallback != null && !fallback.getName().equals(primary.getName())) {
                result = fallback.recognizeInvoice(fileData, isPdf);
                if (result != null) {
                    log.info("OCR 发票识别降级成功，使用供应商: {}", fallback.getName());
                }
            }
        }

        return result;
    }

    /**
     * 银行卡识别（支持主备降级）
     *
     * @param imageData 图片数据
     * @param provider  指定供应商（可选）
     * @return 识别结果
     */
    public BankCardResult recognizeBankCard(byte[] imageData, String provider) {
        OcrProvider primary = resolveProvider(provider);
        BankCardResult result = primary.recognizeBankCard(imageData);

        if (result == null) {
            log.warn("OCR 银行卡识别失败，供应商: {}，尝试降级", primary.getName());
            OcrProvider fallback = providerFactory.getFallbackProvider();
            if (fallback != null && !fallback.getName().equals(primary.getName())) {
                result = fallback.recognizeBankCard(imageData);
                if (result != null) {
                    log.info("OCR 银行卡识别降级成功，使用供应商: {}", fallback.getName());
                }
            }
        }

        return result;
    }

    /**
     * 获取可用供应商列表
     */
    public List<ProviderInfo> getProviders() {
        return providerFactory.getAllProviders().stream()
                .map(p -> new ProviderInfo(p.getName(), p.getDisplayName()))
                .collect(Collectors.toList());
    }

    /**
     * 解析供应商：指定名称则用指定的，否则用主数据源
     */
    private OcrProvider resolveProvider(String providerName) {
        if (providerName != null && !providerName.isEmpty()) {
            OcrProvider specified = providerFactory.getProvider(providerName);
            if (specified != null) {
                return specified;
            }
            log.warn("指定的 OCR 供应商不存在: {}，使用主数据源", providerName);
        }
        return providerFactory.getPrimaryProvider();
    }

    /**
     * 供应商信息
     */
    public static class ProviderInfo {
        private final String name;
        private final String displayName;

        public ProviderInfo(String name, String displayName) {
            this.name = name;
            this.displayName = displayName;
        }

        public String getName() {
            return name;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
