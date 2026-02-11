import request from '@/utils/request'

/**
 * 查询我的订阅列表
 */
export function listSubscriptions() {
    return request({
        url: '/message/subscription/list',
        method: 'get'
    })
}

/**
 * 开启/关闭订阅
 */
export function toggleSubscription(subType, channel, enabled) {
    return request({
        url: '/message/subscription/toggle',
        method: 'put',
        params: { subType, channel, enabled }
    })
}

/**
 * 手动触发推送
 */
export function manualPush(subType) {
    return request({
        url: `/message/subscription/push/${subType}`,
        method: 'post'
    })
}
