import request from '@/utils/request'

/**
 * 分页查询导出任务
 */
export function pageExportTask(params) {
  return request({
    url: '/system/export/task/page',
    method: 'get',
    params
  })
}

/**
 * 获取任务详情
 */
export function getExportTask(id) {
  return request({
    url: `/system/export/task/${id}`,
    method: 'get'
  })
}

/**
 * 创建导出任务
 */
export function createExportTask(data) {
  return request({
    url: '/system/export/task',
    method: 'post',
    data
  })
}

/**
 * 取消任务
 */
export function cancelExportTask(id) {
  return request({
    url: `/system/export/task/${id}/cancel`,
    method: 'put'
  })
}

/**
 * 删除任务
 */
export function deleteExportTask(id) {
  return request({
    url: `/system/export/task/${id}`,
    method: 'delete'
  })
}

/**
 * 获取任务进度
 */
export function getTaskProgress(id) {
  return request({
    url: `/system/export/task/${id}/progress`,
    method: 'get'
  })
}

/**
 * 获取下载地址
 */
export function getDownloadUrl(taskNo) {
  return `/api/system/export/task/${taskNo}/download`
}
