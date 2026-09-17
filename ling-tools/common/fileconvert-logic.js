// common/fileconvert-logic.js - 文件转换三功能（转换/压缩/整理）共享逻辑工厂
// 三个独立页面（pdfconvert/pdfcompress/pdfarrange）各自传入固定 mode，
// 只装配对应功能的 data 与方法，页面间互不携带其他功能。
const app = getApp();
const api = require('../api/fileConvert');
const authApi = require('../api/auth');

// 预计压缩系数：dpi 越低压得越狠（clamp 0.15 ~ 0.85）
function estFactor(dpi) {
  let f = 0.15 + 0.70 * Math.pow(dpi / 200, 1.8);
  return Math.max(0.15, Math.min(0.85, f));
}
// 由 dpi 推导 JPEG 质量（与前端一致）
function dpiToQuality(dpi) {
  return Math.max(15, Math.min(85, Math.round(dpi * 0.35)));
}

// ==================== 公共方法（生命周期 + 各功能通用的工具/预览/保存） ====================
const COMMON_METHODS = {
  onLoad() {
    this.applyTheme();
    // 进入功能页即校验登录态：未登录直接去登录页，避免选完图/填完名才被后端 401 踢回
    this._ensureLogin();
  },
  onShow() {
    this.applyTheme();
  },
  // 未登录跳登录页；已登录但 token 失效时由 request 统一 401 → 提前跳登录
  _ensureLogin() {
    const token = wx.getStorageSync('token') || app.globalData.token;
    if (!token) {
      wx.redirectTo({ url: '/pages/login/login' });
      return;
    }
    // 用一次轻量请求确认 token 有效（401 时 request 内部会清 token 并跳登录）
    authApi.getUserInfo().catch(() => {});
  },
  onUnload() {
    // 整理功能：离开时清理本地图片路径引用（后端保留可二次整理）
    this._imgPaths = {};
    this._localStack = [];
  },
  applyTheme() {
    this.setData({ themeClass: app.getTheme() === 'dark' ? 'dark-theme' : 'light-theme' });
  },
  setTheme() {
    this.applyTheme();
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
  previewResult(e) {
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
  // 相册/相机选图：微信隐私协议未同意时 chooseMedia 会静默失败，这里先引导授权再选择，
  // 任何失败都以 toast 明确指出，避免「点击没反应」。
  _pickMedia({ count, sourceType }) {
    const that = this;
    const doPick = () => {
      // 用兼容性最好的 wx.chooseImage（返回 res.tempFilePaths 字符串数组，各基础库一致），
      // 替代 wx.chooseMedia，避免不同基础库返回结构差异导致选图后取不到路径。
      wx.chooseImage({
        count,
        sourceType,
        success(res) {
          const paths = res.tempFilePaths || [];
          if (!paths.length) {
            wx.showToast({ title: '未获取到图片路径，请重试', icon: 'none' });
            return;
          }
          that._addImages(paths);
        },
        fail(err) {
          const msg = err && err.errMsg;
          if (msg && msg.indexOf('privacy') >= 0) {
            wx.showToast({ title: '需同意隐私协议才能使用相册/相机，请在小程序「设置」中开启', icon: 'none' });
          } else if (msg && msg.indexOf('cancel') >= 0) {
            wx.showToast({ title: '已取消', icon: 'none' });
          } else if (msg && (msg.indexOf('auth') >= 0 || msg.indexOf('permission') >= 0 || msg.indexOf('denied') >= 0)) {
            wx.showToast({ title: '未获得相册/相机权限，请在设置中开启', icon: 'none' });
          } else {
            wx.showToast({ title: '无法打开相册/相机：' + (msg || '未知原因'), icon: 'none' });
          }
          console.error('chooseImage fail:', msg);
        }
      });
    };
    // 隐私协议：需要时引导授权
    if (wx.getPrivacySetting && wx.requirePrivacyAuthorize) {
      wx.getPrivacySetting({
        success(r) {
          if (r && r.needAuthorization && r.privacyContractName) {
            wx.showModal({
              title: '隐私授权',
              content: '需要使用相册/相机上传图片，是否同意《' + r.privacyContractName + '》？',
              success(m) {
                if (m.confirm) {
                  wx.requirePrivacyAuthorize({ success: doPick, fail: () => wx.showToast({ title: '未同意，无法使用相册/相机', icon: 'none' }) });
                } else {
                  wx.showToast({ title: '未同意，无法使用相册/相机', icon: 'none' });
                }
              }
            });
          } else {
            doPick();
          }
        },
        fail: doPick
      });
    } else {
      doPick();
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
  }
};

// 选择 PDF（转换/压缩共用）
const choosePdfBehavior = {
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
  }
};

// ==================== 三个功能各自的 data + 方法 ====================
const FEATURES = {
  convert: {
    data: {
      convertFile: null,
      convertFormat: 'word', // word | markdown
      convertLoading: false,
      convertResult: null
    },
    methods: {
      ...choosePdfBehavior,
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
      }
    }
  },

  compress: {
    data: {
      compressFile: null,
      compressDpi: 150,
      sliderPos: 100,      // 滑块位置（0~210），compressDpi = 250 - sliderPos：左清晰、右极致
      estimatedSize: '--',
      compressLoading: false,
      compressResult: null,
      compressionRatio: '--'
    },
    methods: {
      ...choosePdfBehavior,
      // 滑块：向右 = 更极致压缩（DPI 更低、文件更小），与下方标注方向一致
      onCompressDpiChg(e) {
        const pos = Number(e.detail.value);
        const dpi = 250 - pos;
        this.setData({ sliderPos: pos, compressDpi: dpi });
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
      }
    }
  },

  arrange: {
    data: {
      arrangeName: '',
      arrangeLevel: 'standard', // high | standard | compact
      creatingWork: false,
      finalizingWork: false,
      workDoc: null,       // { docId, docNo, docName }
      workImages: [],      // [{_k, id, fileName, size, localPath}]
      arrangeResult: null,
      planChecked: true    // 微信基础库支持拍照能力（按需调整）
    },
    methods: {
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
        this._localStack = [];
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
        this._pickMedia({ count: 9, sourceType: ['album', 'camera'] });
      },
      // 拍照
      cameraArrange() {
        const docId = this.data.workDoc && this.data.workDoc.docId;
        if (!docId) return;
        this._pickMedia({ count: 1, sourceType: ['camera'] });
      },
      // 追加图片：逐张上传，返回全量 images；本地路径按上传顺序暂存，供缩略图/原图查看
      async _addImages(paths) {
        const docId = this.data.workDoc.docId;
        if (!docId) return;
        // 记录本次新增的本地路径（按顺序，与后端 images 按 sort 顺序一致），供 _syncImages 配 localPath
        this._localStack = (this._localStack || []).concat(paths);
        wx.showLoading({ title: '上传中...', mask: true });
        try {
          const res = await api.pushScanImages(docId, paths);
          const images = res.images || res || [];
          this._syncImages(images);
        } catch (e) {
          // 上传失败：从栈里回退本次路径，避免后续错位
          this._localStack.splice(this._localStack.length - paths.length, paths.length);
          // api 已提示
        } finally {
          wx.hideLoading();
        }
      },
      // 用后端全量 images 重建本地列表：旧图按 fileName 复用上一轮 localPath，新图按顺序取 _localStack
      _syncImages(images) {
        const list = images || [];
        const prev = this.data.workImages || [];
        const prevByFile = {};
        prev.forEach((i) => { if (i.fileName) prevByFile[i.fileName] = i; });
        const stack = this._localStack || [];
        const next = list.map((it) => {
          const old = prevByFile[it.fileName];
          let localPath = '';
          if (old) {
            localPath = old.localPath || '';
          } else if (stack.length) {
            localPath = stack.shift() || '';
          }
          return {
            _k: (old && old._k) || this.genKey(),
            id: it.id,
            fileName: it.fileName,
            size: it.size,
            localPath
          };
        });
        this._localStack = stack;
        this.setData({ workImages: next });
      },
      // 点击缩略图查看原图
      previewImage(e) {
        const url = e.currentTarget.dataset.url;
        const urls = (this.data.workImages || []).map((i) => i.localPath).filter(Boolean);
        wx.previewImage({ urls: urls.length ? urls : [url], current: url || urls[0] });
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
          // 2) 完成归档（后端返回 targetFile 嵌套结构）
          const p = this.levelParams();
          const res = await api.finalizeScanDoc(docId, p.maxSide, p.quality);
          const tf = res.targetFile || {};
          this.setData({
            arrangeResult: { fileName: tf.fileName, fileSize: tf.fileSize, fileUrl: tf.fileUrl, count: list.length }
          });
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
    }
  }
};

/**
 * 生成某一功能页面的 Page 配置（只含该功能的 data 与方法）。
 * @param {'convert'|'compress'|'arrange'} mode 功能模式
 */
function createPageConfig(mode) {
  const feature = FEATURES[mode];
  if (!feature) {
    throw new Error('未知功能模式: ' + mode);
  }
  return {
    data: { themeClass: '', activeTab: mode, ...feature.data },
    ...COMMON_METHODS,
    ...feature.methods
  };
}

module.exports = { createPageConfig };
