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

  // 进入文件转换指定 tab（普通页导航，用 storage 传递目标 tab）
  goToFileTab(e) {
    const tab = e.currentTarget.dataset.tab;
    wx.setStorageSync('pendingFileConvertTab', tab);
    wx.navigateTo({ url: '/pages/fileconvert/fileconvert' });
  }
});