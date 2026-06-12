import request from '@/utils/request'

/**
 * 分页查询分支列表
 */
export function pageBranches(data) {
  return request({
    url: '/dev/branch/page',
    method: 'post',
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
    data
  })
}

/**
 * 删除分支记录
 */
export function deleteBranch(id) {
  return request({
    url: `/dev/branch/${id}`,
    method: 'delete'
  })
}

/**
 * 获取当前生产分支
 */
export function getCurrentProdBranch() {
  return request({
    url: '/dev/branch/current-prod',
    method: 'get'
  })
}

/**
 * 更新当前生产分支
 */
export function updateCurrentProdBranch(prodBranch) {
  return request({
    url: '/dev/branch/current-prod',
    method: 'put',
    data: { prodBranch }
  })
}

/**
 * 获取分支统计信息
 */
export function getBranchStats() {
  return request({
    url: '/dev/branch/stats',
    method: 'get'
  })
}

/**
 * 完成分支记录
 */
export function completeBranch(id) {
  return request({
    url: `/dev/branch/complete/${id}`,
    method: 'put'
  })
}
