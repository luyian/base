import request from '@/utils/request'

/** 403 事件通知（访问码失效时触发页面重新验证） */
const devTokenExpiredCallbacks = []

export function onDevTokenExpired(callback) {
  devTokenExpiredCallbacks.push(callback)
}

/** 从 localStorage 获取开发工具访问令牌 */
function getDevTokenHeader() {
  const token = localStorage.getItem('dev_access_token') || ''
  return { 'X-Dev-Token': token }
}

/**
 * 包装请求，403 时清除 token 并通知页面
 */
function devRequest(config) {
  return request(config).catch(e => {
    if (e.response && e.response.status === 403 && e.response.data?.code === 403) {
      localStorage.removeItem('dev_access_token')
      devTokenExpiredCallbacks.forEach(cb => cb())
    }
    return Promise.reject(e)
  })
}

/**
 * 分页查询分支列表
 */
export function pageBranches(data) {
  return devRequest({
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
  return devRequest({
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
  return devRequest({
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
  return devRequest({
    url: `/dev/branch/${id}`,
    method: 'delete',
    headers: getDevTokenHeader()
  })
}

/**
 * 获取当前生产分支
 */
export function getCurrentProdBranch() {
  return devRequest({
    url: '/dev/branch/current-prod',
    method: 'get',
    headers: getDevTokenHeader()
  })
}

/**
 * 更新当前生产分支
 */
export function updateCurrentProdBranch(prodBranch) {
  return devRequest({
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
  return devRequest({
    url: '/dev/branch/stats',
    method: 'get',
    headers: getDevTokenHeader()
  })
}

/**
 * 完成分支记录
 */
export function completeBranch(id) {
  return devRequest({
    url: `/dev/branch/complete/${id}`,
    method: 'put',
    headers: getDevTokenHeader()
  })
}
