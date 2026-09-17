import request from '@/utils/request'

/**
 * 去除图片水印
 * @param {File} file 图片文件（JPG/PNG/BMP/WebP）
 * @param {string} [region] 手动框选区域 "x,y,w,h"（像素坐标），为空走自动识别
 * @param {string} [backend] 后端算法，默认 cv2
 */
export function removeWatermark(file, region, backend = 'cv2') {
  const formData = new FormData()
  formData.append('file', file)
  if (region) {
    formData.append('region', region)
  }
  formData.append('backend', backend)
  return request({
    url: '/system/image/remove-watermark',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' },
    timeout: 120000
  })
}