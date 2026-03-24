// app.js
App({
  globalData: {
    userInfo: null,
    token: null,
    baseUrl: 'https://119.45.176.101/api'
  },

  onLaunch() {
    // Check if user is logged in
    const token = wx.getStorageSync('token');
    const userInfo = wx.getStorageSync('userInfo');
    if (token) {
      this.globalData.token = token;
      this.globalData.userInfo = userInfo;
    }
  },

  // Login method
  login(code) {
    return new Promise((resolve, reject) => {
      wx.request({
        url: `${this.globalData.baseUrl}/auth/login`,
        method: 'POST',
        data: { code },
        success: (res) => {
          if (res.data.code === 200) {
            this.globalData.token = res.data.data.token;
            this.globalData.userInfo = res.data.data.user;
            wx.setStorageSync('token', res.data.data.token);
            wx.setStorageSync('userInfo', res.data.data.user);
            resolve(res.data);
          } else {
            reject(res.data);
          }
        },
        fail: reject
      });
    });
  },

  // Logout method
  logout() {
    this.globalData.token = null;
    this.globalData.userInfo = null;
    wx.removeStorageSync('token');
    wx.removeStorageSync('userInfo');
  },

  // Check if logged in
  isLoggedIn() {
    return !!this.globalData.token;
  }
});