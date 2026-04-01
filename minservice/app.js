// app.js
App({
  globalData: {
    userInfo: null,
    token: null,
    baseUrl: 'http://119.45.176.101/api',  // 测试用HTTP，生产环境需要HTTPS + SSL证书
    theme: 'light'  // 默认亮色主题
  },

  onLaunch() {
    // Check if user is logged in
    const token = wx.getStorageSync('token');
    const userInfo = wx.getStorageSync('userInfo');
    if (token) {
      this.globalData.token = token;
      this.globalData.userInfo = userInfo;
    }
    
    // Load saved theme preference
    const savedTheme = wx.getStorageSync('theme');
    if (savedTheme) {
      this.globalData.theme = savedTheme;
      this.setTheme(savedTheme);
    }
  },

  onShow() {
    // 每次 app 显示时同步导航栏和 TabBar 颜色
    const theme = this.globalData.theme || 'light';
    const navBgColor = theme === 'dark' ? '#0F172A' : '#FFFFFF';
    const navTextStyle = theme === 'dark' ? 'white' : 'black';
    wx.setNavigationBarColor({
      frontColor: navTextStyle,
      backgroundColor: navBgColor,
      animation: { duration: 0 }
    });
    
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
  },

  // 切换主题
  setTheme(theme) {
    this.globalData.theme = theme;
    wx.setStorageSync('theme', theme);
    
    // 设置导航栏颜色
    
    // 设置TabBar颜色
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
    const navBgColor = theme === 'dark' ? '#0F172A' : '#FFFFFF';
    const navTextStyle = theme === 'dark' ? 'white' : 'black';
    wx.setNavigationBarColor({
      frontColor: navTextStyle,
      backgroundColor: navBgColor,
      animation: { duration: 300 }
    });
    
    // 设置页面栈中所有页面的样式
    const pages = getCurrentPages();
    pages.forEach(page => {
      if (page && page.setTheme) {
        page.setTheme(theme);
      }
    });
    
    // 触发主题切换事件
    wx.eventCenter && wx.eventCenter.trigger('themeChange', theme);
  },

  // 切换到亮色主题
  setLightTheme() {
    this.setTheme('light');
  },

  // 切换到暗色主题
  setDarkTheme() {
    this.setTheme('dark');
  },

  // 切换主题（toggle）
  toggleTheme() {
    const newTheme = this.globalData.theme === 'light' ? 'dark' : 'light';
    this.setTheme(newTheme);
    return newTheme;
  },

  // 获取当前主题
  getTheme() {
    return this.globalData.theme;
  },

  // 判断是否为暗色主题
  isDarkTheme() {
    return this.globalData.theme === 'dark';
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
    // Check both storage and globalData
    const token = wx.getStorageSync('token');
    if (token && !this.globalData.token) {
      this.globalData.token = token;
    }
    return !!this.globalData.token || !!wx.getStorageSync('token');
  },

  // Check if user is admin (has fund management permission)
  // Check if user is admin (has fund management permission or SUPER_ADMIN role)
  isAdmin() {
    const userInfo = this.globalData.userInfo || wx.getStorageSync('userInfo');
    if (!userInfo) return false;
    // Check roles first
    const roles = userInfo.roles || [];
    if (roles.includes('SUPER_ADMIN')) return true;
    // Check permissions
    const permissions = userInfo.permissions || [];
    return permissions.some(p => 
      p === 'stock:fund:add' || 
      p === 'stock:fund:edit' || 
      p === 'stock:fund:delete'
    );
  }
});
