package com.base.common.export.mask;

import com.base.common.export.constant.MaskTypeEnum;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 手机号脱敏器
 * 规则：保留前3位和后4位，中间用****代替
 * 示例：138****1234
 *
 * @author base
 * @since 2026-02-04
 */
@Component
public class PhoneMasker implements DataMasker {

    @Override
    public String mask(String value) {
        if (!StringUtils.hasText(value) || value.length() < 7) {
            return value;
        }
        return value.substring(0, 3) + "****" + value.substring(value.length() - 4);
    }

    @Override
    public String getMaskType() {
        return MaskTypeEnum.PHONE.getCode();
    }
}
