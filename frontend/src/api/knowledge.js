import request from '@/utils/request'

// ==================== 知识库 API ====================

/**
 * 获取知识库列表
 */
export function getKnowledgeBaseList() {
  return request({
    url: '/knowledge-base',
    method: 'get'
  })
}

/**
 * 获取知识库详情
 */
export function getKnowledgeBaseDetail(id) {
  return request({
    url: `/knowledge-base/${id}`,
    method: 'get'
  })
}

/**
 * 创建知识库
 */
export function createKnowledgeBase(data) {
  return request({
    url: '/knowledge-base',
    method: 'post',
    data
  })
}

/**
 * 更新知识库
 */
export function updateKnowledgeBase(data) {
  return request({
    url: '/knowledge-base',
    method: 'put',
    data
  })
}

/**
 * 删除知识库
 */
export function deleteKnowledgeBase(id) {
  return request({
    url: `/knowledge-base/${id}`,
    method: 'delete'
  })
}

// ==================== 文档 API ====================

/**
 * 获取文档列表
 * @param {Object} params - 查询参数 { knowledgeBaseId, directoryId, tagId }
 */
export function getDocumentList(params) {
  return request({
    url: '/documents',
    method: 'get',
    params
  })
}

/**
 * 获取文档详情
 */
export function getDocumentDetail(id) {
  return request({
    url: `/documents/${id}`,
    method: 'get'
  })
}

/**
 * 创建文档
 */
export function createDocument(data) {
  return request({
    url: '/documents',
    method: 'post',
    data
  })
}

/**
 * 更新文档
 */
export function updateDocument(data) {
  return request({
    url: '/documents',
    method: 'put',
    data
  })
}

/**
 * 删除文档
 */
export function deleteDocument(id) {
  return request({
    url: `/documents/${id}`,
    method: 'delete'
  })
}

// ==================== 目录 API ====================

/**
 * 获取目录树
 * @param {number} knowledgeBaseId - 知识库ID
 */
export function getDirectoryTree(knowledgeBaseId) {
  return request({
    url: '/directories',
    method: 'get',
    params: { knowledgeBaseId }
  })
}

/**
 * 创建目录
 */
export function createDirectory(data) {
  return request({
    url: '/directories',
    method: 'post',
    data
  })
}

/**
 * 更新目录
 */
export function updateDirectory(data) {
  return request({
    url: '/directories',
    method: 'put',
    data
  })
}

/**
 * 删除目录
 */
export function deleteDirectory(id) {
  return request({
    url: `/directories/${id}`,
    method: 'delete'
  })
}

// ==================== 标签 API ====================

/**
 * 获取标签列表
 * @param {number} knowledgeBaseId - 知识库ID
 */
export function getTagList(knowledgeBaseId) {
  return request({
    url: '/tags',
    method: 'get',
    params: { knowledgeBaseId }
  })
}

/**
 * 创建标签
 */
export function createTag(data) {
  return request({
    url: '/tags',
    method: 'post',
    data
  })
}

/**
 * 更新标签
 */
export function updateTag(data) {
  return request({
    url: '/tags',
    method: 'put',
    data
  })
}

/**
 * 删除标签
 */
export function deleteTag(id) {
  return request({
    url: `/tags/${id}`,
    method: 'delete'
  })
}