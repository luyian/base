package com.base.common.export.registry;

import com.base.common.export.mask.DataMasker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 脱敏器注册表
 *
 * @author base
 * @since 2026-02-04
 */
@Component
public class MaskerRegistry {

    @Autowired
    private List<DataMasker> maskers;

    private final Map<String, DataMasker> maskerMap = new HashMap<>();

    @PostConstruct
    public void init() {
        for (DataMasker masker : maskers) {
            maskerMap.put(masker.getMaskType(), masker);
        }
    }

    /**
     * 获取脱敏器
     *
     * @param maskType 脱敏类型
     * @return 脱敏器
     */
    public DataMasker getMasker(String maskType) {
        if (!StringUtils.hasText(maskType)) {
            return null;
        }
        return maskerMap.get(maskType);
    }

    /**
     * 注册脱敏器
     *
     * @param maskType 脱敏类型
     * @param masker   脱敏器
     */
    public void register(String maskType, DataMasker masker) {
        maskerMap.put(maskType, masker);
    }
}
