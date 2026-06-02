package com.base.common.thirdparty.contact;

import lombok.Data;

/**
 * 通用第三方用户信息
 *
 * @author base
 */
@Data
public class ThirdPartyUser {

    private String openId;
    private String unionId;
    private String name;
    private String email;
    private String mobile;
    private String avatar;
    private String departmentId;
}
