// app.js
App({
  globalData: {
    userInfo: null,
    token: null,
    baseUrl: 'http://119.45.176.101/api',
    theme: 'light'
  },

  onLaunch() {
    const token = wx.getStorageSync('token');
    const userInfo = wx.getStorageSync('userInfo');
    if (token) {
      this.globalData.token = token;
      this.globalData.userInfo = userInfo;
    }
    
    const savedTheme = wx.getStorageSync('theme');
    if (savedTheme) {
      this.globalData.theme = savedTheme;
    } else {
      wx.setStorageSync('theme', 'light');
      this.globalData.theme = 'light';
    }
    
    // 延迟一点执行，确保app完全初始化
    setTimeout(() => {
      this._syncNavigationBarColor();
    }, 100);
  },

  // 同步主题颜色
  _syncNavigationBarColor() {
    try {
      const theme = this.globalData.theme || 'light';
      console.log('[Theme] 同步主题:', theme);
      
      // 导航栏
      const navBgColor = theme === 'dark' ? '#0F172A' : '#FFFFFF';
      const navTextStyle = theme === 'dark' ? 'white' : 'black';
      wx.setNavigationBarColor({
        frontColor: navTextStyle,
        backgroundColor: navBgColor,
        animation: { duration: 0 }
      });
      console.log('[Theme] 导航栏:', navBgColor, navTextStyle);
      
      // TabBar
      const tabBgColor = theme === 'dark' ? '#1E293B' : '#FFFFFF';
      const tabBorderColor = theme === 'dark' ? '#334155' : '#E2E8F0';
      const tabTextColor = theme === 'dark' ? '#94A3B8' : '#94A3B8';
      const tabSelectedColor = theme === 'dark' ? '#60A5FA' : '#3B82F6';
      wx.setTabBarStyle({
        color: tabTextColor,
        selectedColor: tabSelectedColor,
        backgroundColor: tabBgColor,
        borderColor: tabBorderColor
      });
      
      // 页面背景
      const bgColor = theme === 'dark' ? '#0F172A' : '#F5F7FA';
      wx.setBackgroundColor({
        backgroundColor: bgColor,
        backgroundColorTop: bgColor,
        backgroundColorBottom: bgColor
      });
      
      console.log('[Theme] 同步完成');
    } catch (e) {
      console.error('[Theme] 同步失败:', e);
    }
  },

  onShow() {
    this._syncNavigationBarColor();
  },

  setTheme(theme) {
    this.globalData.theme = theme;
    wx.setStorageSync('theme', theme);
    this._syncNavigationBarColor();
    
    const pages = getCurrentPages();
    pages.forEach(page => {
      if (page && page.setTheme) {
        page.setTheme(theme);
      }
    });
  },

  toggleTheme() {
    const newTheme = this.globalData.theme === 'dark' ? 'light' : 'dark';
    this.setTheme(newTheme);
    return newTheme;
  },

  getTheme() {
    return this.globalData.theme || 'light';
  },

  isLoggedIn() {
    return !!this.globalData.token;
  },

  isAdmin() {
    return this.globalData.userInfo && this.globalData.userInfo.role === 'ADMIN';
  },

  logout() {
    this.globalData.token = null;
    this.globalData.userInfo = null;
    wx.removeStorageSync('token');
    wx.removeStorageSync('userInfo');
  },

  login(code) {
    return new Promise((resolve, reject) => {
      wx.request({
        url: `${this.globalData.baseUrl}/auth/login`,
        method: 'POST',
        data: { code },
        header: { 'Content-Type': 'application/json' },
        success: (res) => {
          if (res.data.code === 200) {
            const token = res.data.data.token;
            const userInfo = res.data.data.user;
            this.globalData.token = token;
            this.globalData.userInfo = userInfo;
            wx.setStorageSync('token', token);
            wx.setStorageSync('userInfo', userInfo);
            resolve(userInfo);
          } else {
            reject(new Error(res.data.message));
          }
        },
        fail: reject
      });
    });
  }
});
