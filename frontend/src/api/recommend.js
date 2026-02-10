import request from '@/utils/request'

/**
 * 分页查询推荐股票列表
 * @param {Object} data 查询参数对象 { recommendDate, current, size }
 */
export function listRecommend(data) {
  return request({
    url: '/stock/recommend/list',
    method: 'post',
    data
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
 * 手动触发打分（全量）
 * @param {String} scoreDate 打分日期 (YYYY-MM-DD)
 */
export function executeScore(scoreDate) {
  return request({
    url: '/stock/recommend/execute',
    method: 'post',
    params: { scoreDate }
  })
}

/**
 * 单条股票打分
 * @param {String} stockCode 股票代码
 * @param {String} scoreDate 打分日期 (YYYY-MM-DD)
 */
export function executeSingleScore(stockCode, scoreDate) {
  return request({
    url: '/stock/recommend/score',
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
 * @param {Object} data 查询参数对象 { current, size }
 */
export function pageRules(data) {
  return request({
    url: '/stock/recommend/rule/page',
    method: 'post',
    data
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
