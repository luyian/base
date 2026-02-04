package com.base.common.export.mask;

import com.base.common.export.constant.MaskTypeEnum;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 身份证脱敏器
 * 规则：保留前6位和后4位，中间用****代替
 * 示例：110101****1234
 *
 * @author base
 * @since 2026-02-04
 */
@Component
public class IdCardMasker implements DataMasker {

    @Override
    public String mask(String value) {
        if (!StringUtils.hasText(value) || value.length() < 10) {
            return value;
        }
        return value.substring(0, 6) + "********" + value.substring(value.length() - 4);
    }

    @Override
    public String getMaskType() {
        return MaskTypeEnum.ID_CARD.getCode();
    }
}
