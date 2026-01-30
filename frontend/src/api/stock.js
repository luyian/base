import request from '@/utils/request'

// ==================== Token 管理 ====================

/**
 * 查询 Token 列表
 */
export function listTokens(params) {
  return request({
    url: '/stock/token/list',
    method: 'get',
    params
  })
}

/**
 * 根据 ID 获取 Token
 */
export function getTokenById(id) {
  return request({
    url: `/stock/token/${id}`,
    method: 'get'
  })
}

/**
 * 添加 Token
 */
export function addToken(data) {
  return request({
    url: '/stock/token',
    method: 'post',
    data
  })
}

/**
 * 更新 Token
 */
export function updateToken(id, data) {
  return request({
    url: `/stock/token/${id}`,
    method: 'put',
    data
  })
}

/**
 * 作废 Token
 */
export function disableToken(id) {
  return request({
    url: `/stock/token/${id}/disable`,
    method: 'put'
  })
}

/**
 * 启用 Token
 */
export function enableToken(id) {
  return request({
    url: `/stock/token/${id}/enable`,
    method: 'put'
  })
}

/**
 * 删除 Token
 */
export function deleteToken(id) {
  return request({
    url: `/stock/token/${id}`,
    method: 'delete'
  })
}

/**
 * 重置每日计数
 */
export function resetTokenDaily(provider = 'itick') {
  return request({
    url: '/stock/token/reset-daily',
    method: 'post',
    params: { provider }
  })
}

// ==================== 数据映射配置 ====================

/**
 * 查询映射配置列表
 */
export function listMappings() {
  return request({
    url: '/stock/mapping/list',
    method: 'get'
  })
}

/**
 * 根据编码获取映射配置
 */
export function getMappingByCode(code) {
  return request({
    url: `/stock/mapping/code/${code}`,
    method: 'get'
  })
}

/**
 * 根据 ID 获取映射配置
 */
export function getMappingById(id) {
  return request({
    url: `/stock/mapping/${id}`,
    method: 'get'
  })
}

/**
 * 保存映射配置
 */
export function saveMapping(data) {
  return request({
    url: '/stock/mapping',
    method: 'post',
    data
  })
}

/**
 * 更新映射配置
 */
export function updateMapping(id, data) {
  return request({
    url: `/stock/mapping/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除映射配置
 */
export function deleteMapping(id) {
  return request({
    url: `/stock/mapping/${id}`,
    method: 'delete'
  })
}

// ==================== 自选股票 ====================

/**
 * 查询自选股票列表
 */
export function listWatchlist() {
  return request({
    url: '/stock/watchlist',
    method: 'get'
  })
}

/**
 * 添加自选股票
 */
export function addWatchlist(stockCode, remark) {
  return request({
    url: '/stock/watchlist',
    method: 'post',
    params: { stockCode, remark }
  })
}

/**
 * 删除自选股票
 */
export function deleteWatchlist(id) {
  return request({
    url: `/stock/watchlist/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除自选股票
 */
export function batchDeleteWatchlist(ids) {
  return request({
    url: '/stock/watchlist/batch',
    method: 'delete',
    data: ids
  })
}

/**
 * 调整排序
 */
export function updateWatchlistSort(id, sortOrder) {
  return request({
    url: `/stock/watchlist/${id}/sort`,
    method: 'put',
    params: { sortOrder }
  })
}

/**
 * 检查是否已添加自选
 */
export function checkWatchlist(stockCode) {
  return request({
    url: '/stock/watchlist/check',
    method: 'get',
    params: { stockCode }
  })
}

// ==================== 股票查询 ====================

/**
 * 分页查询股票列表
 */
export function listStocks(params) {
  return request({
    url: '/stock/list',
    method: 'get',
    params
  })
}

/**
 * 根据股票代码获取股票信息
 */
export function getStockByCode(stockCode) {
  return request({
    url: `/stock/${stockCode}`,
    method: 'get'
  })
}

/**
 * 查询股票 K 线数据
 */
export function getKlineData(stockCode, startDate, endDate) {
  return request({
    url: `/stock/${stockCode}/kline`,
    method: 'get',
    params: { startDate, endDate }
  })
}

// ==================== 数据同步 ====================

/**
 * 同步股票列表
 */
export function syncStockList(market = 'HK') {
  return request({
    url: '/stock/sync/stock-list',
    method: 'post',
    params: { market }
  })
}

/**
 * 同步单只股票的 K 线数据
 */
export function syncKline(stockCode, startDate, endDate) {
  return request({
    url: `/stock/sync/kline/${stockCode}`,
    method: 'post',
    params: { startDate, endDate }
  })
}

/**
 * 批量同步自选股票的 K 线数据
 */
export function batchSyncKline(startDate, endDate) {
  return request({
    url: '/stock/sync/kline/batch',
    method: 'post',
    params: { startDate, endDate }
  })
}
