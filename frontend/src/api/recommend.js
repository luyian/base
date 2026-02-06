import request from '@/utils/request'

/**
 * 分页查询推荐股票列表
 * @param {String} recommendDate 推荐日期 (YYYY-MM-DD)
 * @param {Number} page 页码
 * @param {Number} size 每页大小
 */
export function listRecommend(recommendDate, page, size) {
  return request({
    url: '/stock/recommend/list',
    method: 'get',
    params: { recommendDate, page, size }
  })
}

/**
 * 查询打分明细
 * @param {String} stockCode 股票代码
 * @param {String} scoreDate 打分日期 (YYYY-MM-DD)
 */
export function getScoreDetail(stockCode, scoreDate) {
  return request({
    url: '/stock/recommend/detail',
    method: 'get',
    params: { stockCode, scoreDate }
  })
}

/**
 * 手动触发打分
 * @param {String} stockCode 股票代码（可选，不传则对所有股票打分）
 * @param {String} scoreDate 打分日期 (YYYY-MM-DD)
 */
export function executeScore(stockCode, scoreDate) {
  return request({
    url: '/stock/recommend/execute',
    method: 'post',
    params: { stockCode, scoreDate }
  })
}

/**
 * 查询最新推荐日期
 */
export function getLatestRecommendDate() {
  return request({
    url: '/stock/recommend/latest-date',
    method: 'get'
  })
}

/**
 * 查询规则列表
 */
export function listRules() {
  return request({
    url: '/stock/recommend/rule/list',
    method: 'get'
  })
}

/**
 * 分页查询规则列表
 * @param {Number} page 页码
 * @param {Number} size 每页大小
 */
export function pageRules(page, size) {
  return request({
    url: '/stock/recommend/rule/page',
    method: 'get',
    params: { page, size }
  })
}

/**
 * 根据ID查询规则
 * @param {Number} id 规则ID
 */
export function getRuleById(id) {
  return request({
    url: `/stock/recommend/rule/${id}`,
    method: 'get'
  })
}

/**
 * 更新规则配置
 * @param {Number} id 规则ID
 * @param {Object} data 规则数据
 */
export function updateRule(id, data) {
  return request({
    url: `/stock/recommend/rule/${id}`,
    method: 'put',
    data
  })
}

/**
 * 启用规则
 * @param {Number} id 规则ID
 */
export function enableRule(id) {
  return request({
    url: `/stock/recommend/rule/${id}/enable`,
    method: 'post'
  })
}

/**
 * 禁用规则
 * @param {Number} id 规则ID
 */
export function disableRule(id) {
  return request({
    url: `/stock/recommend/rule/${id}/disable`,
    method: 'post'
  })
}
