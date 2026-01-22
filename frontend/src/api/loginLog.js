import request from '@/utils/request'

/**
 * 分页查询登录日志列表
 */
export function pageLoginLogs(params) {
  return request({
    url: '/system/log/login/page',
    method: 'get',
    params
  })
}

/**
 * 根据ID获取登录日志详情
 */
export function getLoginLogById(id) {
  return request({
    url: `/system/log/login/${id}`,
    method: 'get'
  })
}

/**
 * 删除登录日志
 */
export function deleteLoginLog(id) {
  return request({
    url: `/system/log/login/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除登录日志
 */
export function batchDeleteLoginLogs(ids) {
  return request({
    url: '/system/log/login/batch',
    method: 'delete',
    data: ids
  })
}

/**
 * 清空登录日志
 */
export function clearLoginLogs() {
  return request({
    url: '/system/log/login/clear',
    method: 'delete'
  })
}
