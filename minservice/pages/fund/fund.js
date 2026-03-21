// pages/fund/fund.js
const fundApi = require('../../api/fund');

Page({
  data: {
    funds: [],
    loading: true,
    activeTab: 'all'
  },

  onLoad() {
    this.loadFunds();
  },

  loadFunds() {
    this.setData({ loading: true });
    fundApi.getFundList()
      .then(res => {
        this.setData({ funds: res.data || [], loading: false });
      })
      .catch(() => this.setData({ loading: false }));
  },

  goToDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/fund/detail?id=${id}` });
  }
});