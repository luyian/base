import request from '@/utils/request'

// ==================== 字典类型 ====================

/**
 * 分页查询字典类型
 */
export function pageDictTypes(data) {
  return request({
    url: '/system/dict/type/page',
    method: 'post',
    data
  })
}

/**
 * 根据ID获取字典类型详情
 */
export function getDictTypeById(id) {
  return request({
    url: `/system/dict/type/${id}`,
    method: 'get'
  })
}

/**
 * 查询所有字典类型列表
 */
export function listAllDictTypes() {
  return request({
    url: '/system/dict/type/list',
    method: 'get'
  })
}

/**
 * 新增字典类型
 */
export function addDictType(data) {
  return request({
    url: '/system/dict/type',
    method: 'post',
    data
  })
}

/**
 * 编辑字典类型
 */
export function updateDictType(data) {
  return request({
    url: '/system/dict/type',
    method: 'put',
    data
  })
}

/**
 * 删除字典类型
 */
export function deleteDictType(id) {
  return request({
    url: `/system/dict/type/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除字典类型
 */
export function batchDeleteDictTypes(ids) {
  return request({
    url: '/system/dict/type/batch',
    method: 'delete',
    data: ids
  })
}

/**
 * 刷新字典缓存
 */
export function refreshDictCache() {
  return request({
    url: '/system/dict/type/refresh-cache',
    method: 'post'
  })
}

// ==================== 字典数据 ====================

/**
 * 分页查询字典数据
 */
export function pageDictData(data) {
  return request({
    url: '/system/dict/data/page',
    method: 'post',
    data
  })
}

/**
 * 根据ID获取字典数据详情
 */
export function getDictDataById(id) {
  return request({
    url: `/system/dict/data/${id}`,
    method: 'get'
  })
}

/**
 * 根据字典类型查询数据列表
 */
export function listByDictType(dictType) {
  return request({
    url: `/system/dict/data/type/${dictType}`,
    method: 'get'
  })
}

/**
 * 新增字典数据
 */
export function addDictData(data) {
  return request({
    url: '/system/dict/data',
    method: 'post',
    data
  })
}

/**
 * 编辑字典数据
 */
export function updateDictData(data) {
  return request({
    url: '/system/dict/data',
    method: 'put',
    data
  })
}

/**
 * 删除字典数据
 */
export function deleteDictData(id) {
  return request({
    url: `/system/dict/data/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除字典数据
 */
export function batchDeleteDictData(ids) {
  return request({
    url: '/system/dict/data/batch',
    method: 'delete',
    data: ids
  })
}
