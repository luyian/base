import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

// 创建 axios 实例
const service = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 401 状态防抖：避免多个请求同时触发多次"未授权"提示
let isHandling401 = false

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 从 localStorage 获取 token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    // Blob 响应（文件下载）直接返回，不走 code 判断
    if (response.data instanceof Blob) {
      return response.data
    }

    const res = response.data

    // 如果返回的状态码不是 200，则认为是错误
    if (res.code !== 200) {
      // 401: 未授权，防抖处理（只提示一次并跳转登录页）
      if (res.code === 401) {
        if (!isHandling401) {
          isHandling401 = true
          ElMessage.error(res.message || '未授权，请重新登录')
          localStorage.removeItem('token')
          router.push('/login')
          setTimeout(() => { isHandling401 = false }, 2000)
        }
      } else {
        ElMessage.error(res.message || '请求失败')
      }

      return Promise.reject(new Error(res.message || '请求失败'))
    } else {
      return res
    }
  },
  error => {
    console.error('响应错误:', error)

    if (error.response) {
      const bodyMessage = error.response.data?.message
      switch (error.response.status) {
        case 401:
          if (!isHandling401) {
            isHandling401 = true
            ElMessage.error(bodyMessage || '未授权，请重新登录')
            localStorage.removeItem('token')
            router.push('/login')
            setTimeout(() => { isHandling401 = false }, 2000)
          }
          break
        case 403:
          ElMessage.error(bodyMessage || '拒绝访问')
          break
        case 404:
          ElMessage.error(bodyMessage || '请求的资源不存在')
          break
        case 500:
          ElMessage.error(bodyMessage || '服务器内部错误')
          break
        default:
          ElMessage.error(bodyMessage || '请求失败')
      }
    } else {
      ElMessage.error('网络连接失败')
    }

    return Promise.reject(error)
  }
)

export default service
