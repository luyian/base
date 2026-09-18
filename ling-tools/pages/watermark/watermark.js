// pages/watermark/watermark.js - 图片去水印（手动框选擦除）
// 交互：双指缩放 / 单指拖选框整体移动 / 拖角调整选区大小 / 放大后可把框移到水印处
const api = require('../../api/fileConvert');

const app = getApp();

Page({
  data: {
    themeClass: '',
    // 源图：本地路径 + 真实像素尺寸
    srcPath: '',
    realW: 0,
    realH: 0,
    // 图片渲染基准尺寸（未缩放，widthFix 满宽）
    bdW: 0,
    bdH: 0,
    // 画布缩放/平移（transform: translate(tx,ty) scale(s)，origin 左上）
    s: 1,
    tx: 0,
    ty: 0,
    // 选区矩形（逻辑坐标，0..bdW × 0..bdH）
    boxTLX: 0,
    boxTLY: 0,
    boxBRX: 0,
    boxBRY: 0,
    boxW: 0,
    boxH: 0,
    // 结果
    resultUrl: '',
    busy: false
  },

  onLoad() {
    this._scrollTop = 0;
    this.applyTheme();
  },

  onShow() {
    this.applyTheme();
  },

  // 页面滚动：记录位移，供 viewport 坐标实时换算
  onPageScroll(e) {
    this._scrollTop = e.scrollTop;
  },

  applyTheme() {
    this.setData({
      themeClass: app.getTheme() === 'dark' ? 'dark-theme' : 'light-theme'
    });
  },

  setTheme() {
    this.applyTheme();
  },

  // 选择图片
  chooseImage() {
    if (this.data.busy) return;
    wx.chooseImage({
      count: 1,
      sizeType: ['original'],
      success: (res) => {
        const path = res.tempFilePaths[0];
        wx.getImageInfo({
          src: path,
          success: (info) => {
            this.setData({
              srcPath: path,
              realW: info.width,
              realH: info.height,
              resultUrl: ''
            });
          },
          fail: () => wx.showToast({ title: '读取图片失败', icon: 'none' })
        });
      }
    });
  },

  // 图片渲染完成：测量视口宽度 + 初始化画布与选区
  onImgLoad() {
    this.createSelectorQuery()
      .select('.canvas-viewport')
      .boundingClientRect((res) => {
        if (!res) return;
        const bdW = res.width;
        // 图片 widthFix 满宽，高度 = 宽 × 原图宽高比（确定性强，不依赖首次加载未稳定的高度）
        const bdH = Math.round((bdW * this.data.realH) / this.data.realW);
        this._vpLeft = res.left;
        // 记录 viewport 相对文档顶部的坐标，滚动时可按 scrollTop 实时还原当前视口位置
        this._docTop = res.top + (this._scrollTop || 0);
        this.setData({ bdW, bdH });
        // 初始选区：紧凑包裹右下角水印（常见位置），留出大片空白供平移画面
        this._updateBox(Math.round(bdW * 0.72), Math.round(bdH * 0.78), Math.round(bdW * 0.96), Math.round(bdH * 0.97));
      })
      .exec();
  },

  // ==================== 触摸手势 ====================

  // 当前 viewport 相对视口顶部的 Y 坐标（按滚动位移实时换算）
  _getVpTop() {
    return this._docTop - (this._scrollTop || 0);
  },

  // 触摸物理坐标 → 画布逻辑坐标（除以缩放、减平移）
  _logic(e) {
    const { tx, ty, s } = this.data;
    const t = e.touches[0];
    return [
      (t.clientX - this._vpLeft - tx) / s,
      (t.clientY - this._getVpTop() - ty) / s
    ];
  },

  // 两点距离
  _dist(t0, t1) {
    return Math.hypot(t1.clientX - t0.clientX, t1.clientY - t0.clientY);
  },

  onCanvasTouchStart(e) {
    const t = e.touches;
    if (t.length >= 2) {
      const d = this.data;
      this._gesture = {
        type: 'pinch',
        startS: d.s,
        startTx: d.tx,
        startTy: d.ty,
        startDist: this._dist(t[0], t[1]),
        startMid: this._mid(t)
      };
      return;
    }
    // 单指：落在选区内＝整体移动选框；否则＝平移画布
    const [lx, ly] = this._logic(e);
    const d = this.data;
    const inBox = lx >= d.boxTLX && lx <= d.boxBRX && ly >= d.boxTLY && ly <= d.boxBRY;
    this._gesture = {
      type: 'single',
      mode: inBox ? 'box' : 'pan',
      lastX: t[0].clientX,
      lastY: t[0].clientY
    };
  },

  onCanvasTouchMove(e) {
    const t = e.touches;
    if (!this._gesture) return;

    if (t.length >= 2) {
      // 双指：以「起始中点」为锚缩放并平移——两指距离变 = 缩放，两指整体移动 = 拖动画布
      const d = this.data;
      const g = this._gesture;
      const ratio = this._dist(t[0], t[1]) / g.startDist;
      const newS = Math.min(5, Math.max(1, g.startS * ratio));
      const mid = this._mid(t);
      // 起始中点对应的逻辑坐标（用起始时的 t 与 s，保证 s 不变时 tx 随中点位移平移）
      const lx = (g.startMid.x - this._vpLeft - g.startTx) / g.startS;
      const ly = (g.startMid.y - this._getVpTop() - g.startTy) / g.startS;
      this._setPan(
        mid.x - this._vpLeft - newS * lx,
        mid.y - this._getVpTop() - newS * ly,
        newS
      );
      return;
    }

    // 单指（或缩放后保留单指）
    const g = this._gesture;
    const dx = t[0].clientX - g.lastX;
    const dy = t[0].clientY - g.lastY;
    if (g.type !== 'single') {
      // 从双指落下到单指，重新判定
      this.onCanvasTouchStart(e);
      return;
    }
    if (g.mode === 'box') {
      // 整体移动选框
      this._moveBox(dx / this.data.s, dy / this.data.s);
    } else {
      this._setPan(this.data.tx + dx, this.data.ty + dy, this.data.s);
    }
    g.lastX = t[0].clientX;
    g.lastY = t[0].clientY;
  },

  onCanvasTouchEnd() {
    this._gesture = null;
  },

  // 两点中点
  _mid(t) {
    return {
      x: (t[0].clientX + t[1].clientX) / 2,
      y: (t[0].clientY + t[1].clientY) / 2
    };
  },

  // 设置平移/缩放，并把平移限制在不留空洞、不无限拖出的范围
  _setPan(nx, ny, ns) {
    const { bdW, bdH } = this.data;
    const minX = bdW * (1 - ns); // <=0
    const minY = bdH * (1 - ns);
    const tx = Math.min(0, Math.max(minX, nx));
    const ty = Math.min(0, Math.max(minY, ny));
    this.setData({ tx, ty, s: ns });
  },

  // 整体移动选框（逻辑坐标，dx/dy 为逻辑增量）
  _moveBox(dx, dy) {
    const { boxTLX, boxTLY, boxW, boxH, bdW, bdH } = this.data;
    const nx = Math.min(Math.max(boxTLX + dx, 0), bdW - boxW);
    const ny = Math.min(Math.max(boxTLY + dy, 0), bdH - boxH);
    this._updateBox(nx, ny, nx + boxW, ny + boxH);
  },

  // ==================== 角标拖拽（改大小） ====================

  onTLStart(e) {
    this.onTLMove(e);
  },

  // 拖左上角：不越过右下角
  onTLMove(e) {
    const { boxBRX, boxBRY } = this.data;
    const [x, y] = this._logic(e);
    const lx = Math.min(Math.max(x, 0), boxBRX);
    const ly = Math.min(Math.max(y, 0), boxBRY);
    this._updateBox(lx, ly, boxBRX, boxBRY);
  },

  onBRStart(e) {
    this.onBRMove(e);
  },

  // 拖右下角：不越过左上角
  onBRMove(e) {
    const { boxTLX, boxTLY, bdW, bdH } = this.data;
    const [x, y] = this._logic(e);
    const rx = Math.min(Math.max(x, boxTLX), bdW);
    const ry = Math.min(Math.max(y, boxTLY), bdH);
    this._updateBox(boxTLX, boxTLY, rx, ry);
  },

  // 更新选区四角 + 矩形宽高
  _updateBox(tlx, tly, brx, bry) {
    this.setData({
      boxTLX: Math.round(tlx),
      boxTLY: Math.round(tly),
      boxBRX: Math.round(brx),
      boxBRY: Math.round(bry),
      boxW: Math.round(brx - tlx),
      boxH: Math.round(bry - tly)
    });
  },

  // 重置视图：回到原始缩放，并把选区复位到右下角
  resetView() {
    if (this.data.busy) return;
    const { bdW, bdH } = this.data;
    this._updateBox(Math.round(bdW * 0.72), Math.round(bdH * 0.78), Math.round(bdW * 0.96), Math.round(bdH * 0.97));
    this.setData({ s: 1, tx: 0, ty: 0 });
  },

  // 确认框选并擦除：将逻辑坐标换算为源图像素 region x,y,w,h
  confirmBox() {
    const {
      boxTLX, boxTLY, boxBRX, boxBRY, bdW, bdH, realW, realH
    } = this.data;
    if (!bdW || !bdH || !realW || !realH) {
      wx.showToast({ title: '图片信息未就绪', icon: 'none' });
      return;
    }
    const x1 = Math.round((boxTLX / bdW) * realW);
    const y1 = Math.round((boxTLY / bdH) * realH);
    const x2 = Math.round((boxBRX / bdW) * realW);
    const y2 = Math.round((boxBRY / bdH) * realH);
    const w = x2 - x1;
    const h = y2 - y1;
    if (w <= 0 || h <= 0) {
      wx.showToast({ title: '请拖出有效的框选区域', icon: 'none' });
      return;
    }
    this.doProcess(`${x1},${y1},${w},${h}`);
  },

  // 提交手动框选擦除
  async doProcess(region) {
    if (this.data.busy || !this.data.srcPath) return;
    this.setData({ busy: true, resultUrl: '' });
    try {
      // wxUpload 成功 resolve 后端 data，失败已内部 toast
      const res = await api.removeWatermark(this.data.srcPath, { region, backend: 'cv2' });
      this.setData({ resultUrl: res.targetFile.fileUrl });
      wx.showToast({ title: '去水印完成', icon: 'success' });
    } catch (err) {
      // 网络/业务错误已由 wxUpload 提示
    } finally {
      this.setData({ busy: false });
    }
  },

  // 预览结果大图
  previewResult() {
    if (!this.data.resultUrl) return;
    wx.previewImage({ urls: [this.data.resultUrl] });
  },

  // 保存结果到相册
  saveImage() {
    if (!this.data.resultUrl) return;
    wx.downloadFile({
      url: this.data.resultUrl,
      success: (r) => {
        wx.saveImageToPhotosAlbum({
          filePath: r.tempFilePath,
          success: () => wx.showToast({ title: '已保存到相册', icon: 'success' }),
          fail: () => {
            wx.showToast({ title: '保存失败，请检查相册权限', icon: 'none' });
          }
        });
      },
      fail: () => wx.showToast({ title: '下载图片失败', icon: 'none' })
    });
  },

  // 再处理一张
  processAnother() {
    if (this.data.busy) return;
    this.setData({ srcPath: '', resultUrl: '', s: 1, tx: 0, ty: 0 });
  }
});