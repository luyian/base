// pages/fund/detail.js
const fundApi = require('../../api/fund');

Page({
  data: {
    fund: null,
    holdings: [],
    loading: true
  },

  onLoad(options) {
    this.loadDetail(options.id);
  },

  loadDetail(fundId) {
    this.setData({ loading: true });
    fundApi.getFundDetail(fundId)
      .then(res => {
        this.setData({
          fund: res.data.fundConfig,
          holdings: res.data.quotes || [],
          loading: false
        });
      })
      .catch(() => this.setData({ loading: false }));
  }
});