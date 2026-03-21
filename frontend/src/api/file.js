import request from '@/utils/request'

/**
 * 上传文件
 */
export function uploadFile(file, fileGroup = 'default', fileDesc = '') {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('fileGroup', fileGroup)
  formData.append('fileDesc', fileDesc)
  return request({
    url: '/system/file/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 上传头像
 */
export function uploadAvatar(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/common/file/upload/avatar',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 分页查询文件列表
 */
export function pageFiles(data) {
  return request({
    url: '/system/file/page',
    method: 'get',
    params: data
  })
}

/**
 * 获取文件信息
 */
export function getFile(id) {
  return request({
    url: `/system/file/${id}`,
    method: 'get'
  })
}

/**
 * 删除文件
 */
export function deleteFile(id) {
  return request({
    url: `/system/file/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除文件
 */
export function batchDeleteFiles(ids) {
  return request({
    url: '/system/file/batch',
    method: 'delete',
    data: ids
  })
}

/**
 * 下载文件
 */
export function downloadFile(id) {
  return request({
    url: `/system/file/download/${id}`,
    method: 'get',
    responseType: 'blob'
  })
}

/**
 * 获取文件URL
 */
export function getFileUrl(id) {
  return request({
    url: `/system/file/url/${id}`,
    method: 'get'
  })
}

/**
 * 获取文件分组列表
 */
export function getFileGroups() {
  return request({
    url: '/system/file/groups',
    method: 'get'
  })
}

/**
 * 分页查询文件日志列表
 */
export function pageFileLogs(data) {
  return request({
    url: '/system/file/log/page',
    method: 'get',
    params: data
  })
}

/**
 * 获取文件日志详情
 */
export function getFileLog(id) {
  return request({
    url: `/system/file/log/${id}`,
    method: 'get'
  })
}

/**
 * 删除文件日志
 */
export function deleteFileLog(id) {
  return request({
    url: `/system/file/log/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除文件日志
 */
export function batchDeleteFileLogs(ids) {
  return request({
    url: '/system/file/log/batch',
    method: 'delete',
    data: ids
  })
}

/**
 * 清空文件日志
 */
export function clearFileLogs() {
  return request({
    url: '/system/file/log/clear',
    method: 'delete'
  })
}

/**
 * 获取操作类型列表
 */
export function getOperationTypes() {
  return request({
    url: '/system/file/log/operationTypes',
    method: 'get'
  })
}
