// pages/fileconvert/fileconvert.js - 文件转换（转换/压缩/整理 三 tab）
const app = getApp();
const api = require('../../api/fileConvert');

// 预计压缩系数：dpi 越低压得越狠（clamp 0.15 ~ 0.85）
function estFactor(dpi) {
  let f = 0.15 + 0.70 * Math.pow(dpi / 200, 1.8);
  return Math.max(0.15, Math.min(0.85, f));
}
// 由 dpi 推导 JPEG 质量（与前端一致）
function dpiToQuality(dpi) {
  return Math.max(15, Math.min(85, Math.round(dpi * 0.35)));
}

Page({
  data: {
    themeClass: '',
    activeTab: 'convert',

    // ----- 转换 tab -----
    convertFile: null,
    convertFormat: 'word', // word | markdown
    convertLoading: false,
    convertResult: null,

    // ----- 压缩 tab -----
    compressFile: null,
    compressDpi: 150,
    estimatedSize: '--',
    compressLoading: false,
    compressResult: null,
    compressionRatio: '--',

    // ----- 整理 tab -----
    arrangeName: '',
    arrangeLevel: 'standard', // high | standard | compact
    creatingWork: false,
    finalizingWork: false,
    workDoc: null,       // { docId, docNo, docName }
    workImages: [],      // [{_k, id, fileName, size, localPath}]
    arrangeResult: null,
    planChecked: true,   // 微信基础库支持拍照能力（按需调整）
  },

  onLoad() {
    this.applyTheme();
  },
  onShow() {
    this.applyTheme();
  },
  onUnload() {
    // 离开时若存在未完成工作区，清理本地引用（后端保留可二次整理）
    this._imgPaths = {};
  },
  applyTheme() {
    this.setData({ themeClass: app.getTheme() === 'dark' ? 'dark-theme' : 'light-theme' });
  },
  setTheme() {
    this.applyTheme();
  },

  // ==================== 通用 ====================
  switchTab(e) {
    this.setData({ activeTab: e.currentTarget.dataset.tab });
  },
  genKey() {
    return Date.now().toString(36) + Math.random().toString(36).slice(2, 7);
  },
  formatSize(bytes) {
    if (bytes === null || bytes === undefined) return '--';
    const b = Number(bytes);
    if (b < 1024) return b + ' B';
    if (b < 1024 * 1024) return (b / 1024).toFixed(1) + ' KB';
    if (b < 1024 * 1024 * 1024) return (b / (1024 * 1024)).toFixed(1) + ' MB';
    return (b / (1024 * 1024 * 1024)).toFixed(2) + ' GB';
  },
  // 解析后端返回的 fileUrl → 完整可下载地址
  resolveUrl(url) {
    if (!url) return url;
    if (/^https?:\/\//.test(url)) return url;
    return app.globalData.baseUrl + url;
  },
  // 下载到本地
  download(url) {
    return new Promise((resolve, reject) => {
      wx.downloadFile({
        url: this.resolveUrl(url),
        success: (r) => (r.statusCode === 200 ? resolve(r.tempFilePath) : reject(new Error('下载失败 ' + r.statusCode))),
        fail: reject
      });
    });
  },
  extOf(name) {
    const m = /\.(\w+)$/.exec(name || '');
    return m ? m[1].toLowerCase() : '';
  },

  // 预览：下载并 openDocument
  async previewResult(e) {
    const { url, name, type } = e.currentTarget.dataset;
    this._openDoc(url, name, type);
  },
  async _openDoc(url, name) {
    wx.showLoading({ title: '打开中...', mask: true });
    try {
      const filePath = await this.download(url);
      let fileType = this.extOf(name);
      if (fileType === 'md') fileType = 'txt';
      wx.hideLoading();
      wx.openDocument({
        filePath,
        fileType,
        showMenu: true,
        fail: (err) => wx.showToast({ title: '无法预览该文件', icon: 'none' })
      });
    } catch (e) {
      wx.hideLoading();
      wx.showToast({ title: e.message || '下载失败', icon: 'none' });
    }
  },
  // 保存：下载并写入本地缓存（微信保存文件到磁盘能力有限，返回临时文件已存小程序缓存）
  async saveResult(e) {
    const { url, name } = e.currentTarget.dataset;
    wx.showLoading({ title: '保存中...', mask: true });
    try {
      const filePath = await this.download(url);
      wx.hideLoading();
      if (wx.saveFileToDisk) {
        wx.saveFileToDisk({
          filePath,
          success: () => wx.showToast({ title: '已保存到文件', icon: 'success' }),
          fail: () => wx.saveFile({ filePath, success: () => wx.showToast({ title: '已保存到缓存', icon: 'success' }) })
        });
      } else {
        wx.saveFile({
          filePath,
          success: () => wx.showToast({ title: '已保存到缓存', icon: 'success' }),
          fail: () => wx.showToast({ title: '保存失败', icon: 'none' })
        });
      }
    } catch (e) {
      wx.hideLoading();
      wx.showToast({ title: e.message || '下载失败', icon: 'none' });
    }
  },

  // 选择 PDF（转换/压缩共用）
  choosePdf(e) {
    const tab = e.currentTarget.dataset.tab;
    const that = this;
    wx.chooseMessageFile({
      count: 1,
      type: 'file',
      extension: ['pdf'],
      success(res) {
        const f = res.tempFiles && res.tempFiles[0];
        if (!f) return;
        if (that.extOf(f.name) !== 'pdf') {
          wx.showToast({ title: '请选择 PDF 文件', icon: 'none' });
          return;
        }
        const file = { path: f.path, name: f.name, size: f.size };
        if (tab === 'convert') {
          that.setData({ convertFile: file, convertResult: null });
        } else {
          that.setData({ compressFile: file, compressResult: null, estimatedSize: that._estimate(that.data.compressDpi, f.size) });
        }
      }
    });
  },

  // ==================== 转换 tab ====================
  setConvertFormat(e) {
    this.setData({ convertFormat: e.currentTarget.dataset.fmt });
  },
  async doConvert() {
    const file = this.data.convertFile;
    if (!file) return;
    this.setData({ convertLoading: true, convertResult: null });
    try {
      const fn = this.data.convertFormat === 'word' ? api.pdfToWord : api.pdfToMarkdown;
      const res = await fn(file.path);
      this.setData({ convertResult: res });
      wx.showToast({ title: '转换完成', icon: 'success' });
    } catch (e) {
      // api 内部已提示
    } finally {
      this.setData({ convertLoading: false });
    }
  },

  // ==================== 压缩 tab ====================
  onCompressDpiChg(e) {
    const dpi = Number(e.detail.value);
    this.setData({ compressDpi: dpi });
    if (this.data.compressFile) {
      this.setData({ estimatedSize: this._estimate(dpi, this.data.compressFile.size) });
    }
  },
  _estimate(dpi, size) {
    if (!size) return '--';
    return this.formatSize(Math.round(size * estFactor(dpi)));
  },
  async doCompress() {
    const file = this.data.compressFile;
    if (!file) return;
    const dpi = this.data.compressDpi;
    const quality = dpiToQuality(dpi);
    this.setData({ compressLoading: true, compressResult: null });
    try {
      const res = await api.pdfCompress(file.path, dpi, quality);
      const ratio = this._ratio(res.sourceFile.fileSize, res.targetFile.fileSize);
      this.setData({ compressResult: res, compressionRatio: ratio });
      wx.showToast({ title: '压缩完成', icon: 'success' });
    } catch (e) {
      // api 内部已提示
    } finally {
      this.setData({ compressLoading: false });
    }
  },
  _ratio(src, tgt) {
    if (!src || !tgt) return '--';
    const saved = (1 - tgt / src) * 100;
    return (saved > 0 ? '↓ ' : '↑ ') + Math.abs(saved).toFixed(1) + '%';
  },

  // ==================== 整理 tab ====================
  onArrangeNameInput(e) {
    this.setData({ arrangeName: e.detail.value });
  },
  setArrangeLevel(e) {
    this.setData({ arrangeLevel: e.currentTarget.dataset.lv });
  },
  // 档位 → finalize 参数
  levelParams() {
    const lv = this.data.arrangeLevel;
    if (lv === 'high') return { maxSide: 2000, quality: 90 };
    if (lv === 'compact') return { maxSide: 1000, quality: 60 };
    return { maxSide: 1500, quality: 75 };
  },
  async startArrange() {
    const docName = (this.data.arrangeName || '').trim();
    if (!docName) {
      wx.showToast({ title: '请填写文档命名', icon: 'none' });
      return;
    }
    this.setData({ creatingWork: true });
    this._imgPaths = {};
    try {
      const doc = await api.createScanDoc(docName);
      this.setData({
        workDoc: { docId: doc.docId, docNo: doc.docNo, docName: doc.docName },
        workImages: [],
        arrangeResult: null
      });
    } catch (e) {
      // api 内部已提示
    } finally {
      this.setData({ creatingWork: false });
    }
  },
  // 选图（相册/文件）
  chooseArrangeImages() {
    const docId = this.data.workDoc && this.data.workDoc.docId;
    if (!docId) return;
    const that = this;
    wx.chooseMedia({
      count: 9,
      mediaType: ['image'],
      sourceType: ['album', 'camera'],
      success(res) {
        const paths = (res.tempFiles || []).map((t) => t.tempFilePath);
        that._addImages(paths);
      }
    });
  },
  // 拍照
  cameraArrange() {
    const docId = this.data.workDoc && this.data.workDoc.docId;
    if (!docId) return;
    const that = this;
    wx.chooseMedia({
      count: 1,
      mediaType: ['image'],
      sourceType: ['camera'],
      success(res) {
        const paths = (res.tempFiles || []).map((t) => t.tempFilePath);
        that._addImages(paths);
      }
    });
  },
  // 追加图片：逐张上传，返回 sync
  async _addImages(paths) {
    const docId = this.data.workDoc.docId;
    const map = this._imgPaths || (this._imgPaths = {});
    for (const p of paths) {
      const fileName = 'img_' + Date.now() + '_' + Math.floor(Math.random() * 1000) + '.jpg';
      map[fileName] = p;
    }
    wx.showLoading({ title: '上传中...', mask: true });
    try {
      // 上传后返回全量有序 images
      const res = await api.pushScanImages(docId, paths);
      const images = res.images || res || [];
      this._syncImages(images);
    } catch (e) {
      // api 已提示
    } finally {
      wx.hideLoading();
    }
  },
  // 用后端全量 images 重建本地列表（保留本地缩略图路径）
  _syncImages(images) {
    const map = this._imgPaths || (this._imgPaths = {});
    const prev = this.data.workImages;
    const prevKey = {};
    (prev || []).forEach((i) => { prevKey[i.fileName] = i._k; });
    const next = (images || []).map((it) => {
      const fileName = it.fileName;
      return {
        _k: prevKey[fileName] || this.genKey(),
        id: it.id,
        fileName,
        size: it.size,
        localPath: map[fileName] || ''
      };
    });
    this.setData({ workImages: next });
  },
  arrangeMove(e) {
    const { mode, index } = e.currentTarget.dataset;
    const idx = Number(index);
    const list = this.data.workImages.slice();
    const swap = mode === 'up' ? idx - 1 : idx + 1;
    if (swap < 0 || swap >= list.length) return;
    [list[idx], list[swap]] = [list[swap], list[idx]];
    this.setData({ workImages: list });
  },
  arrangeRemove(e) {
    const { id } = e.currentTarget.dataset;
    const docId = this.data.workDoc.docId;
    const list = this.data.workImages.filter((i) => i.id !== id);
    this.setData({ workImages: list });
    api.deleteScanImage(docId, id).catch(() => {});
  },
  async finalizeArrange() {
    const docId = this.data.workDoc.docId;
    const list = this.data.workImages;
    if (!list.length) {
      wx.showToast({ title: '请先添加图片', icon: 'none' });
      return;
    }
    this.setData({ finalizingWork: true });
    try {
      // 1) 按当前本地顺序落库
      const ids = list.filter((i) => i.id).map((i) => i.id);
      if (ids.length) {
        await api.reorderScanImages(docId, ids);
      }
      // 2) 完成归档
      const p = this.levelParams();
      const res = await api.finalizeScanDoc(docId, p.maxSide, p.quality);
      this.setData({ arrangeResult: res });
      wx.showToast({ title: '已生成 PDF', icon: 'success' });
    } catch (e) {
      // api 已提示
    } finally {
      this.setData({ finalizingWork: false });
    }
  },
  abortArrange() {
    const that = this;
    wx.showModal({
      title: '放弃整理',
      content: '将删除该工作区及已上传图片，确定放弃？',
      confirmColor: '#C6402E',
      success(r) {
        if (!r.confirm) return;
        const docId = that.data.workDoc && that.data.workDoc.docId;
        that._imgPaths = {};
        that.setData({ workDoc: null, workImages: [], arrangeResult: null });
        if (docId) api.deleteScanDoc(docId).catch(() => {});
      }
    });
  }
});