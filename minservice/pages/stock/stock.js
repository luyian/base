// pages/stock/stock.js
const stockApi = require('../../api/stock');

Page({
  data: {
    stocks: [],
    loading: true,
    page: 1,
    size: 20,
    hasMore: true,
    keyword: '',
    industry: '',
    industries: []
  },

  onLoad() {
    this.loadStocks();
    this.loadIndustryOptions();
  },

  loadIndustryOptions() {
    stockApi.getIndustryOptions()
      .then(res => {
        this.setData({ industries: res.data || [] });
      })
      .catch(() => {});
  },

  loadStocks(loadMore = false) {
    if (!loadMore) {
      this.setData({ loading: true, page: 1 });
    }
    
    const data = {
      page: this.data.page,
      size: this.data.size
    };
    
    if (this.data.keyword) {
      data.keyword = this.data.keyword;
    }
    if (this.data.industry) {
      data.industry = this.data.industry;
    }
    
    stockApi.getStockList(data)
      .then(res => {
        const list = res.data?.records || [];
        this.setData({
          stocks: loadMore ? [...this.data.stocks, ...list] : list,
          loading: false,
          hasMore: list.length >= this.data.size,
          page: this.data.page + 1
        });
      })
      .catch(() => {
        this.setData({ loading: false });
      });
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadStocks(true);
    }
  },

  onPullDownRefresh() {
    this.setData({ page: 1, hasMore: true });
    this.loadStocks().finally(() => {
      wx.stopPullDownRefresh();
    });
  },

  onSearch(e) {
    this.setData({ keyword: e.detail.value, page: 1 });
    this.loadStocks();
  },

  onIndustryChange(e) {
    this.setData({ industry: e.detail.value, page: 1 });
    this.loadStocks();
  },

  goToDetail(e) {
    const stockCode = e.currentTarget.dataset.code;
    wx.navigateTo({ url: `/pages/stock/detail?code=${stockCode}` });
  }
});