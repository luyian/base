import request from '@/utils/request'

/**
 * 分页查询操作日志列表
 */
export function pageOperationLogs(params) {
  return request({
    url: '/system/log/operation/page',
    method: 'get',
    params
  })
}

/**
 * 根据ID获取操作日志详情
 */
export function getOperationLogById(id) {
  return request({
    url: `/system/log/operation/${id}`,
    method: 'get'
  })
}

/**
 * 删除操作日志
 */
export function deleteOperationLog(id) {
  return request({
    url: `/system/log/operation/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除操作日志
 */
export function batchDeleteOperationLogs(ids) {
  return request({
    url: '/system/log/operation/batch',
    method: 'delete',
    data: ids
  })
}

/**
 * 清空操作日志
 */
export function clearOperationLogs() {
  return request({
    url: '/system/log/operation/clear',
    method: 'delete'
  })
}
