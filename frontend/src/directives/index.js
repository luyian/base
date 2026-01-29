import permission from './permission'

/**
 * 全局自定义指令注册
 */
export default {
  install(app) {
    // 注册权限指令
    app.directive('permission', permission)
  }
}
