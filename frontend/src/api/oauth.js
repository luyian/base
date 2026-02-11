import request from '@/utils/request'

/**
 * 查询第三方登录是否启用
 */
export function isOauthEnabled() {
    return request({
        url: '/auth/oauth/enabled',
        method: 'get'
    })
}

/**
 * 获取 GitHub 授权地址
 */
export function getGithubAuthUrl() {
    return request({
        url: '/auth/oauth/github/url',
        method: 'get'
    })
}

/**
 * GitHub 回调处理
 */
export function handleGithubCallback(data) {
    return request({
        url: '/auth/oauth/github/callback',
        method: 'post',
        data
    })
}

/**
 * 创建新账号并绑定
 */
export function bindNewUser(data) {
    return request({
        url: '/auth/oauth/bindNew',
        method: 'post',
        data
    })
}

/**
 * 绑定已有账号
 */
export function bindExistUser(data) {
    return request({
        url: '/auth/oauth/bindExist',
        method: 'post',
        data
    })
}

/**
 * 查询当前用户绑定列表
 */
export function listOauthBindings() {
    return request({
        url: '/system/oauth/bindList',
        method: 'get'
    })
}

/**
 * 解绑第三方账号
 */
export function unbindOauth(oauthType) {
    return request({
        url: `/system/oauth/unbind/${oauthType}`,
        method: 'delete'
    })
}
