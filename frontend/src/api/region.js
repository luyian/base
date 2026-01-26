import request from '@/utils/request'

/**
 * 查询行政区划树
 */
export function treeRegions(params) {
  return request({
    url: '/system/region/tree',
    method: 'get',
    params
  })
}

/**
 * 根据父级ID获取子区划
 */
export function getChildrenByParentId(parentId) {
  return request({
    url: `/system/region/children/${parentId}`,
    method: 'get'
  })
}

/**
 * 根据层级获取区划列表
 */
export function getRegionsByLevel(level) {
  return request({
    url: `/system/region/level/${level}`,
    method: 'get'
  })
}

/**
 * 获取级联选择器数据
 */
export function getCascadeNodes(parentId) {
  return request({
    url: '/system/region/cascade',
    method: 'get',
    params: { parentId }
  })
}

/**
 * 根据区划代码获取完整路径
 */
export function getFullPath(regionCode) {
  return request({
    url: `/system/region/fullPath/${regionCode}`,
    method: 'get'
  })
}

/**
 * 根据区划代码查询
 */
export function getByRegionCode(regionCode) {
  return request({
    url: `/system/region/code/${regionCode}`,
    method: 'get'
  })
}

/**
 * 搜索行政区划
 */
export function searchRegions(keyword) {
  return request({
    url: '/system/region/search',
    method: 'get',
    params: { keyword }
  })
}

/**
 * 根据ID获取区划详情
 */
export function getRegionById(id) {
  return request({
    url: `/system/region/${id}`,
    method: 'get'
  })
}

/**
 * 新增行政区划
 */
export function addRegion(data) {
  return request({
    url: '/system/region',
    method: 'post',
    data
  })
}

/**
 * 编辑行政区划
 */
export function updateRegion(data) {
  return request({
    url: '/system/region',
    method: 'put',
    data
  })
}

/**
 * 删除行政区划
 */
export function deleteRegion(id) {
  return request({
    url: `/system/region/${id}`,
    method: 'delete'
  })
}

/**
 * 批量导入
 */
export function importRegions(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/system/region/import',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
