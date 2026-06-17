import request from '@/utils/request'

/** 从 localStorage 获取开发工具访问令牌 */
function getDevTokenHeader() {
  const token = localStorage.getItem('dev_access_token') || ''
  return { 'X-Dev-Token': token }
}

/**
 * 分页查询分支列表
 */
export function pageBranches(data) {
  return request({
    url: '/dev/branch/page',
    method: 'post',
    headers: getDevTokenHeader(),
    data
  })
}

/**
 * 新增分支记录
 */
export function addBranch(data) {
  return request({
    url: '/dev/branch',
    method: 'post',
    headers: getDevTokenHeader(),
    data
  })
}

/**
 * 编辑分支记录
 */
export function updateBranch(data) {
  return request({
    url: '/dev/branch',
    method: 'put',
    headers: getDevTokenHeader(),
    data
  })
}

/**
 * 删除分支记录
 */
export function deleteBranch(id) {
  return request({
    url: `/dev/branch/${id}`,
    method: 'delete',
    headers: getDevTokenHeader()
  })
}

/**
 * 获取当前生产分支
 */
export function getCurrentProdBranch() {
  return request({
    url: '/dev/branch/current-prod',
    method: 'get',
    headers: getDevTokenHeader()
  })
}

/**
 * 更新当前生产分支
 */
export function updateCurrentProdBranch(prodBranch) {
  return request({
    url: '/dev/branch/current-prod',
    method: 'put',
    headers: getDevTokenHeader(),
    data: { prodBranch }
  })
}

/**
 * 获取分支统计信息
 */
export function getBranchStats() {
  return request({
    url: '/dev/branch/stats',
    method: 'get',
    headers: getDevTokenHeader()
  })
}

/**
 * 完成分支记录
 */
export function completeBranch(id) {
  return request({
    url: `/dev/branch/complete/${id}`,
    method: 'put',
    headers: getDevTokenHeader()
  })
}
