// pages/fund/detail.js
const fundApi = require('../../api/fund');
const app = getApp();

Page({
  data: {
    fund: null,
    holdings: [],
    loading: true,
    themeClass: ''
  },

  onLoad(options) {
    this.applyTheme();
    this.loadDetail(options.id);
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
  setTheme(theme) {
    this.applyTheme();
  },

  loadDetail(fundId) {
    this.setData({ loading: true });
    fundApi.getFundDetail(fundId)
      .then(res => {
        const fund = res.data;
        fund.estimatedChangePercent = parseFloat(fund.estimatedChangePercent || 0).toFixed(3);
        fund.rawWeightedChange = parseFloat(fund.rawWeightedChange || 0).toFixed(3);
        fund.totalWeight = parseFloat(fund.totalWeight || 0).toFixed(2);
        this.setData({
          fund: fund,
          holdings: fund.quotes || [],
          loading: false
        });
      })
      .catch(() => this.setData({ loading: false }));
  }
});