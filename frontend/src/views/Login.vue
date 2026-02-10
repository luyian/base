<template>
  <div class="login-container">
    <!-- 动态背景粒子 -->
    <div class="bg-particles">
      <div v-for="i in 20" :key="i" class="particle" :style="getParticleStyle(i)" />
    </div>

    <!-- 左侧品牌区域 -->
    <div class="login-brand">
      <div class="brand-content">
        <img src="/logo.svg" alt="Logo" class="brand-logo" />
        <h1 class="brand-title">后台管理系统</h1>
        <p class="brand-desc">高效 · 安全 · 可靠</p>
      </div>
    </div>

    <!-- 右侧登录区域 -->
    <div class="login-panel">
      <div class="login-box" :class="{ 'login-box-enter': mounted }">
        <div class="login-header">
          <h2 class="login-title">欢迎登录</h2>
          <p class="login-subtitle">请输入您的账号信息</p>
        </div>
        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
        >
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              prefix-icon="User"
              size="large"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              prefix-icon="Lock"
              size="large"
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>
          <el-form-item prop="captcha">
            <div class="captcha-box">
              <el-input
                v-model="loginForm.captcha"
                placeholder="请输入验证码"
                prefix-icon="Key"
                size="large"
                @keyup.enter="handleLogin"
              />
              <img
                :src="captchaUrl"
                class="captcha-img"
                @click="refreshCaptcha"
                alt="验证码"
              />
            </div>
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              class="login-button"
              @click="handleLogin"
            >
              <span v-if="!loading">登 录</span>
              <span v-else>登录中...</span>
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import { login, getCaptcha } from '@/api/auth'

const router = useRouter()
const userStore = useUserStore()

// 挂载动画控制
const mounted = ref(false)

// 表单引用
const loginFormRef = ref(null)

// 登录表单数据
const loginForm = reactive({
  username: '',
  password: '',
  captcha: '',
  captchaKey: ''
})

// 表单验证规则
const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  captcha: [
    { required: true, message: '请输入验证码', trigger: 'blur' }
  ]
}

// 验证码图片地址
const captchaUrl = ref('')

// 加载状态
const loading = ref(false)

/**
 * 生成粒子随机样式
 */
const getParticleStyle = (index) => {
  const size = 4 + Math.random() * 8
  const left = Math.random() * 100
  const delay = Math.random() * 15
  const duration = 15 + Math.random() * 20
  const opacity = 0.15 + Math.random() * 0.25
  return {
    width: `${size}px`,
    height: `${size}px`,
    left: `${left}%`,
    animationDelay: `${delay}s`,
    animationDuration: `${duration}s`,
    opacity
  }
}

// 获取验证码
const refreshCaptcha = async () => {
  try {
    const res = await getCaptcha()
    captchaUrl.value = res.data.captchaImage
    loginForm.captchaKey = res.data.captchaKey
  } catch (error) {
    console.error('获取验证码失败:', error)
  }
}

// 登录
const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await login({
          username: loginForm.username,
          password: loginForm.password,
          captcha: loginForm.captcha,
          captchaKey: loginForm.captchaKey
        })

        // 保存 token
        userStore.setToken(res.data.token)

        // 获取用户信息和权限
        await userStore.loadUserInfo()

        ElMessage.success('登录成功')
        router.push('/')
      } catch (error) {
        console.error('登录失败:', error)
        // 刷新验证码
        refreshCaptcha()
      } finally {
        loading.value = false
      }
    }
  })
}

// 组件挂载时获取验证码并触发入场动画
onMounted(() => {
  refreshCaptcha()
  requestAnimationFrame(() => {
    mounted.value = true
  })
})
</script>

<style scoped>
.login-container {
  display: flex;
  width: 100%;
  height: 100vh;
  overflow: hidden;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  position: relative;
}

/* ========== 动态粒子背景 ========== */
.bg-particles {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
  z-index: 0;
}

.particle {
  position: absolute;
  bottom: -20px;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 50%;
  animation: floatUp linear infinite;
}

@keyframes floatUp {
  0% {
    transform: translateY(0) scale(1);
    opacity: 0;
  }
  10% {
    opacity: 0.3;
  }
  90% {
    opacity: 0.1;
  }
  100% {
    transform: translateY(-100vh) scale(0.3);
    opacity: 0;
  }
}

/* ========== 左侧品牌区域 ========== */
.login-brand {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  z-index: 1;
}

.brand-content {
  text-align: center;
  animation: fadeInLeft 0.8s ease-out;
}

.brand-logo {
  width: 180px;
  height: 180px;
  object-fit: contain;
  margin-bottom: 24px;
  filter: drop-shadow(0 4px 20px rgba(100, 180, 255, 0.4));
  animation: logoFloat 4s ease-in-out infinite;
}

@keyframes logoFloat {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-12px);
  }
}

.brand-title {
  font-size: 36px;
  font-weight: 700;
  color: #fff;
  margin: 0 0 12px;
  letter-spacing: 4px;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
}

.brand-desc {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.6);
  margin: 0;
  letter-spacing: 8px;
}

@keyframes fadeInLeft {
  from {
    opacity: 0;
    transform: translateX(-40px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* ========== 右侧登录面板 ========== */
.login-panel {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 80px 40px 40px;
  position: relative;
  z-index: 1;
}

.login-box {
  width: 400px;
  padding: 48px 40px 36px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow:
    0 20px 60px rgba(0, 0, 0, 0.3),
    0 0 40px rgba(100, 180, 255, 0.1);
  backdrop-filter: blur(20px);
  opacity: 0;
  transform: translateY(30px);
  transition: all 0.6s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.login-box-enter {
  opacity: 1;
  transform: translateY(0);
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.login-logo {
  width: 56px;
  height: 56px;
  object-fit: contain;
  margin-bottom: 16px;
}

.login-title {
  font-size: 24px;
  font-weight: 600;
  color: #1a1a2e;
  margin: 0 0 8px;
}

.login-subtitle {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

/* ========== 表单样式 ========== */
.login-form {
  margin-top: 8px;
}

.login-form :deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 0 0 1px #dcdfe6 inset;
  transition: all 0.3s;
}

.login-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #409eff inset;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #409eff inset, 0 0 0 3px rgba(64, 158, 255, 0.15);
}

.captcha-box {
  display: flex;
  gap: 10px;
  width: 100%;
}

.captcha-img {
  width: 120px;
  height: 40px;
  cursor: pointer;
  border-radius: 8px;
  transition: opacity 0.2s;
  flex-shrink: 0;
}

.captcha-img:hover {
  opacity: 0.8;
}

.login-button {
  width: 100%;
  height: 44px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  letter-spacing: 6px;
  background: linear-gradient(135deg, #409eff 0%, #2563eb 100%);
  border: none;
  transition: all 0.3s;
}

.login-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(64, 158, 255, 0.4);
}

.login-button:active {
  transform: translateY(0);
}

/* ========== 响应式适配 ========== */
@media (max-width: 900px) {
  .login-brand {
    display: none;
  }

  .login-container {
    justify-content: center;
  }

  .login-panel {
    padding: 20px;
  }

  .login-box {
    width: 360px;
    padding: 40px 32px 28px;
  }
}
</style>
