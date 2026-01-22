import request from '@/utils/request'

/**
 * 获取服务器信息
 */
export function getServerInfo() {
  return request({
    url: '/system/monitor/server',
    method: 'get'
  })
}

/**
 * 获取缓存信息
 */
export function getCacheInfo() {
  return request({
    url: '/system/monitor/cache',
    method: 'get'
  })
}

/**
 * 获取缓存键列表
 */
export function getCacheKeys(pattern) {
  return request({
    url: '/system/monitor/cache/keys',
    method: 'get',
    params: { pattern }
  })
}

/**
 * 获取缓存值
 */
export function getCacheValue(key) {
  return request({
    url: `/system/monitor/cache/value/${key}`,
    method: 'get'
  })
}

/**
 * 删除缓存键
 */
export function deleteCacheKey(key) {
  return request({
    url: `/system/monitor/cache/key/${key}`,
    method: 'delete'
  })
}

/**
 * 清空缓存
 */
export function clearCache() {
  return request({
    url: '/system/monitor/cache/clear',
    method: 'delete'
  })
}
