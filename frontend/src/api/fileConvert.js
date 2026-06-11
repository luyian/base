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
