import request from '@/utils/request'

/**
 * PDF 转 Word
 * @param {File} file PDF 文件
 */
export function pdfToWord(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/system/file-convert/pdf-to-word',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' },
    timeout: 60000
  })
}

/**
 * PDF 转 Markdown
 * @param {File} file PDF 文件
 */
export function pdfToMarkdown(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/system/file-convert/pdf-to-markdown',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' },
    timeout: 60000
  })
}

/**
 * 扫描文档整理：开始整理（创建文档工作区）
 * @param {string} docName 文档命名
 */
export function createScanDoc(docName) {
  return request({
    url: '/system/scan-doc/create',
    method: 'post',
    params: { docName }
  })
}

/**
 * 扫描文档整理：追加图片
 * @param {string} docId 工作区编号
 * @param {File[]} files 新增图片
 */
export function pushScanImages(docId, files) {
  const formData = new FormData()
  files.forEach((f) => formData.append('files', f))
  return request({
    url: `/system/scan-doc/${docId}/images`,
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' },
    timeout: 120000
  })
}

/**
 * 扫描文档整理：调整图片顺序
 * @param {string} docId 工作区编号
 * @param {string[]} ids 图片 id 有序数组
 */
export function reorderScanImages(docId, ids) {
  return request({
    url: `/system/scan-doc/${docId}/order`,
    method: 'put',
    data: ids
  })
}

/**
 * 扫描文档整理：删除单张图片
 * @param {string} docId 工作区编号
 * @param {string} imageId 图片 id
 */
export function deleteScanImage(docId, imageId) {
  return request({
    url: `/system/scan-doc/${docId}/images/${imageId}`,
    method: 'delete'
  })
}

/**
 * 扫描文档整理：放弃整个工作区
 * @param {string} docId 工作区编号
 */
export function deleteScanDoc(docId) {
  return request({
    url: `/system/scan-doc/${docId}`,
    method: 'delete'
  })
}

/**
 * 扫描文档整理：完成归档（压缩合并生成 PDF）
 * @param {string} docId 工作区编号
 * @param {number} maxSide 降采样最长边像素
 * @param {number} quality JPEG 压缩质量
 */
export function finalizeScanDoc(docId, maxSide, quality) {
  return request({
    url: `/system/scan-doc/${docId}/finalize`,
    method: 'post',
    params: { maxSide, quality },
    timeout: 120000
  })
}
