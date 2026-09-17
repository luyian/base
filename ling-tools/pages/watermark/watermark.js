// pages/watermark/watermark.js - 图片去水印（自动识别 + 手动框选）
const api = require('../../api/fileConvert');

const app = getApp();

Page({
  data: {
    themeClass: '',
    // 源图：本地路径 + 真实像素尺寸
    srcPath: '',
    realW: 0,
    realH: 0,
    // 图片渲染尺寸（用于换算框选坐标）
    bdW: 0,
    bdH: 0,
    // 是否显示手动框选层
    showBox: false,
    // 框选标记（左上/右下）在容器内的位置 px
    boxTLX: 0,
    boxTLY: 0,
    boxBRX: 0,
    boxBRY: 0,
    // 结果（去水印后 COS URL）
    resultUrl: '',
    busy: false
  },

  onLoad() {
    this.applyTheme();
  },

  onShow() {
    this.applyTheme();
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
              showBox: false,
              resultUrl: ''
            });
          },
          fail: () => wx.showToast({ title: '读取图片失败', icon: 'none' })
        });
      }
    });
  },

  // 图片渲染完成，测量展示区实际尺寸并初始化框选标记
  onImgLoad() {
    this.createSelectorQuery()
      .select('.wm-img')
      .boundingClientRect((res) => {
        if (!res) return;
        const bdW = res.width;
        const bdH = res.height;
        // 标记初始落在图片右下 15% 区域（水印常见位置），用户可拖拽调整
        this.setData({
          bdW,
          bdH,
          boxTLX: Math.round(bdW * 0.62),
          boxTLY: 0,
          boxBRX: Math.round(bdW * 0.99),
          boxBRY: Math.round(bdH * 0.16)
        });
      })
      .exec();
  },

  // 自动识别并去除（不框选）
  autoClean() {
    this.doProcess('');
  },

  // 进入手动框选模式
  startBox() {
    if (this.data.busy) return;
    this.setData({ showBox: true, resultUrl: '' });
  },

  onTLMove(e) {
    this.setData({ boxTLX: e.detail.x, boxTLY: e.detail.y });
  },

  onBRMove(e) {
    this.setData({ boxBRX: e.detail.x, boxBRY: e.detail.y });
  },

  // 取消框选
  cancelBox() {
    this.setData({ showBox: false });
  },

  // 确认框选并擦除：将容器内坐标换算为源图像素 region x,y,w,h
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

  // 统一提交：region 为空走自动识别，否则手动擦除
  async doProcess(region) {
    if (this.data.busy || !this.data.srcPath) return;
    this.setData({ busy: true, showBox: false, resultUrl: '' });
    try {
      // wxUpload 成功 resolve 后端 data，失败已内部 toast
      const res = await api.removeWatermark(this.data.srcPath, region ? { region } : {});
      this.setData({ resultUrl: res.targetFile.fileUrl });
      wx.showToast({ title: '去水印完成', icon: 'success' });
    } catch (err) {
      // 网络/业务错误已由 wxUpload 提示
    } finally {
      this.setData({ busy: false });
    }
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
    this.setData({ srcPath: '', resultUrl: '', showBox: false });
  }
});