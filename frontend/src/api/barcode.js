import request from '@/utils/request'

/**
 * 生成条码/二维码（生成即落库：写 COS + 记 sys_file + 记 t_barcode）
 * @param {string} content 条码内容
 * @param {string} type CODE128（条形码）/ QR（二维码）
 * @returns {Promise<{code:number,data:object}>} data: { id, fileId, filePath, fileUrl, bound, exists }
 */
export function generateBarcode(content, type = 'CODE128') {
  return request({
    url: '/barcode/generate',
    method: 'post',
    data: { content, type },
    timeout: 30000
  })
}

/**
 * 识别图片中的条码/二维码（返回原始内容）
 * @param {File} file 图片文件
 */
export function decodeImage(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/barcode/decode-image',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' },
    timeout: 60000
  })
}

/**
 * 批量生成条码/二维码（选择数量，内容由系统自动生成唯一编号，生成即入库）
 * @param {string} type CODE128（条形码）/ QR（二维码）
 * @param {number} count 生成数量（1~200）
 * @returns {Promise<{code:number,data:Array}>} data: 每项 { id, fileId, filePath, fileUrl, bound, exists }
 */
export function generateBarcodeBatch(type, count) {
  return request({
    url: '/barcode/generate-batch',
    method: 'post',
    data: { type, count },
    timeout: 60000
  })
}

/**
 * 分页查询当前用户的条码/二维码列表（可过滤类型与绑定态）
 * @param {object} params { type, bound, page, size }
 * @returns {Promise<{code:number,data:object}>} data: 分页 { records, total, ... }
 */
export function listBarcodes(params) {
  return request({
    url: '/barcode/list',
    method: 'get',
    params
  })
}

/**
 * 删除一条条码记录（仅本人）
 * @param {number} id 条码记录ID
 */
export function deleteBarcodeRecord(id) {
  return request({
    url: `/barcode/code-record/${id}`,
    method: 'delete'
  })
}