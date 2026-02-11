import request from '@/utils/request'

/**
 * 估值接口超时时间（60秒）
 */
const VALUATION_TIMEOUT = 60000

// ==================== 基金配置管理 ====================

/**
 * 查询基金列表（带缓存估值和自选状态）
 */
export function listFunds() {
    return request({
        url: '/stock/fund/list',
        method: 'get'
    })
}

/**
 * 根据ID获取基金详情
 */
export function getFundById(id) {
    return request({
        url: `/stock/fund/${id}`,
        method: 'get'
    })
}

/**
 * 创建基金
 */
export function createFund(data) {
    return request({
        url: '/stock/fund',
        method: 'post',
        data
    })
}

/**
 * 更新基金
 */
export function updateFund(id, data) {
    return request({
        url: `/stock/fund/${id}`,
        method: 'put',
        data
    })
}

/**
 * 删除基金
 */
export function deleteFund(id) {
    return request({
        url: `/stock/fund/${id}`,
        method: 'delete'
    })
}

// ==================== 基金估值 ====================

/**
 * 获取单个基金实时估值
 */
export function getFundValuation(id) {
    return request({
        url: `/stock/fund/${id}/valuation`,
        method: 'get',
        timeout: VALUATION_TIMEOUT
    })
}

// ==================== 自选管理 ====================

/**
 * 加自选
 */
export function addWatchlist(fundId) {
    return request({
        url: `/stock/fund/watchlist/${fundId}`,
        method: 'post'
    })
}

/**
 * 取消自选
 */
export function removeWatchlist(fundId) {
    return request({
        url: `/stock/fund/watchlist/${fundId}`,
        method: 'delete'
    })
}

/**
 * 我的自选基金列表
 */
export function listMyWatchlist() {
    return request({
        url: '/stock/fund/watchlist/list',
        method: 'get'
    })
}

/**
 * 我的自选基金估值
 */
export function getMyWatchlistValuation() {
    return request({
        url: '/stock/fund/watchlist/valuation',
        method: 'get',
        timeout: VALUATION_TIMEOUT
    })
}
