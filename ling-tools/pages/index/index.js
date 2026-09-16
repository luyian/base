// pages/index/index.js - 元灵工具箱首页（工具宫格入口）
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

  // 进入文件转换（tab 页，switchTab）
  goToFileConvert() {
    wx.switchTab({ url: '/pages/fileconvert/fileconvert' });
  },

  // 进入打卡（tab 页，switchTab）
  goToCheckin() {
    wx.switchTab({ url: '/pages/checkin/checkin' });
  }
});