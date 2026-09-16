// api/fileConvert.js - 文件转换 API（元灵工具箱）
// PDF 转 Word / Markdown / 压缩 + 扫描整理，全部走 wx.uploadFile 或复用 request.js
const request = require('../utils/request');

const app = () => getApp();

/**
 * 通用 wx.uploadFile 封装
 * @param {string} url  接口路径（相对，baseUrl 前缀自动加）
 * @param {string} filePath  本地文件路径
 * @param {string} name  后端字段名（默认 file 或 files）
 * @param {object} formData  额外的表单字段
 */
function wxUpload(url, filePath, name, formData = {}) {
  return new Promise((resolve, reject) => {
    const token = wx.getStorageSync('token');
    wx.uploadFile({
      url: app().globalData.baseUrl + url,
      filePath,
      name,
      formData,
      header: {
        Authorization: token ? `Bearer ${token}` : ''
      },
      success: (res) => {
        let data;
        try {
          data = typeof res.data === 'string' ? JSON.parse(res.data) : res.data;
        } catch (e) {
          reject(new Error('响应解析失败'));
          return;
        }
        // 后端 Result 结构 {code, message, data}
        if (data.code === 200) {
          resolve(data.data);
        } else if (data.code === 401) {
          app().logout();
          wx.reLaunch({ url: '/pages/login/login' });
          reject(data);
        } else {
          wx.showToast({ title: data.message || '请求失败', icon: 'none' });
          reject(data);
        }
      },
      fail: (err) => {
        console.error('upload fail', err);
        wx.showToast({ title: '网络请求失败', icon: 'none' });
        reject(err);
      }
    });
  });
}

module.exports = {
  // ==================== PDF 转换（转 Word / Markdown / 压缩） ====================

  /**
   * PDF 转 Word
   */
  pdfToWord: (filePath) => wxUpload('/system/file-convert/pdf-to-word', filePath, 'file'),

  /**
   * PDF 转 Markdown
   */
  pdfToMarkdown: (filePath) => wxUpload('/system/file-convert/pdf-to-markdown', filePath, 'file'),

  /**
   * PDF 压缩（dpi/quality 透传）
   * @param {string} filePath PDF 本地路径
   * @param {number} dpi 目标 DPI（40~250）
   * @param {number} quality JPEG 质量（15~85）
   */
  pdfCompress: (filePath, dpi, quality) =>
    wxUpload('/system/file-convert/pdf-compress', filePath, 'file', {
      dpi: String(dpi),
      quality: String(quality)
    }),

  // ==================== 扫描整理（scan-doc） ====================

  /**
   * 创建工作区
   * @param {string} docName 文档命名
   * @returns {Promise<{docId, docNo, docName}>}
   */
  createScanDoc: (docName) => {
    const url = '/system/scan-doc/create' + (docName ? `?docName=${encodeURIComponent(docName)}` : '');
    return request.post(url, {});
  },

  /**
   * 逐张上传图片到工作区（后端返回全量 images）
   * @param {string} docId
   * @param {string[]} filePaths 本地图片路径数组
   * @returns {Promise<{images}>}  最近一次追加后的全量 images
   */
  async pushScanImages(docId, filePaths) {
    let lastData = null;
    for (const fp of filePaths) {
      // 后端字段名 files 对应 @RequestParam("files")
      lastData = await wxUpload(`/system/scan-doc/${docId}/images`, fp, 'files');
    }
    return lastData;
  },

  /**
   * 调整图片顺序（body 有序 ids[]，JSON）
   */
  reorderScanImages: (docId, ids) => request.put(`/system/scan-doc/${docId}/order`, ids),

  /**
   * 删除单张图片
   */
  deleteScanImage: (docId, imageId) => request.delete(`/system/scan-doc/${docId}/images/${imageId}`),

  /**
   * 放弃启动整个工作区
   */
  deleteScanDoc: (docId) => request.delete(`/system/scan-doc/${docId}`),

  /**
   * 完成归档（压缩合并生成 PDF）
   * @param {string} docId
   * @param {number} maxSide 降采样最长边
   * @param {number} quality JPEG 质量
   */
  finalizeScanDoc: (docId, maxSide, quality) =>
    request.post(`/system/scan-doc/${docId}/finalize?maxSide=${maxSide}&quality=${quality}`, {})
};