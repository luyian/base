// pages/index/index.js - 元灵工具箱首页（手机桌面式工具宫格）
const app = getApp();
const authApi = require('../../api/auth');

Page({
  data: {
    themeClass: ''
  },

  onLoad() {
    this.applyTheme();
    this.checkLogin();
  },

  // 进入主页前校验登录态：无 token 或 token 已失效 → 去登录页
  // （token 失效时 getUserInfo 返回 401，request.js 已兜底清 token 并跳登录）
  checkLogin() {
    if (!wx.getStorageSync('token')) {
      wx.reLaunch({ url: '/pages/login/login' });
      return;
    }
    authApi.getUserInfo().catch(() => {
      // 401 已由 request.js 统一处理
    });
  },

  onShow() {
    this.applyTheme();
  },

  // 应用主题
  applyTheme() {
    const theme = app.getTheme();
    this.setData({
      themeClass: theme === 'dark' ? 'dark-theme' : 'light-theme'
    });
  },

  // 页面样式设置（供 app.js 调用）
  setTheme() {
    this.applyTheme();
  },

  // 首页桌面图标 → 三个独立功能页
  goToFileTab(e) {
    const tab = e.currentTarget.dataset.tab;
    const urls = {
      compress: '/pages/pdfcompress/pdfcompress',
      arrange: '/pages/pdfarrange/pdfarrange',
      convert: '/pages/pdfconvert/pdfconvert',
      watermark: '/pages/watermark/watermark'
    };
    wx.navigateTo({ url: urls[tab] || urls.convert });
  },

  // 条码工具 → 扫码 + 编辑页
  goToBarcode() {
    wx.navigateTo({ url: '/pages/barcode/barcode' });
  }
});