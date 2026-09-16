// pages/index/index.js - 元灵工具箱首页（手机桌面式工具宫格）
const app = getApp();

Page({
  data: {
    themeClass: ''
  },

  onLoad() {
    this.applyTheme();
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
      convert: '/pages/pdfconvert/pdfconvert'
    };
    wx.navigateTo({ url: urls[tab] || urls.convert });
  }
});