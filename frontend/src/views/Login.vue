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
          <el-form-item v-if="captchaEnabled" prop="captcha">
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

        <!-- 第三方登录 -->
        <div v-if="oauthEnabled" class="oauth-section">
          <el-divider>其他登录方式</el-divider>
          <div class="oauth-buttons">
            <el-tooltip content="GitHub 登录" placement="bottom">
              <div class="oauth-btn" @click="handleGithubLogin" :class="{ 'oauth-btn-loading': githubLoading }">
                <svg viewBox="0 0 16 16" width="24" height="24" fill="currentColor">
                  <path d="M8 0C3.58 0 0 3.58 0 8c0 3.54 2.29 6.53 5.47 7.59.4.07.55-.17.55-.38 0-.19-.01-.82-.01-1.49-2.01.37-2.53-.49-2.69-.94-.09-.23-.48-.94-.82-1.13-.28-.15-.68-.52-.01-.53.63-.01 1.08.58 1.23.82.72 1.21 1.87.87 2.33.66.07-.52.28-.87.51-1.07-1.78-.2-3.64-.89-3.64-3.95 0-.87.31-1.59.82-2.15-.08-.2-.36-1.02.08-2.12 0 0 .67-.21 2.2.82.64-.18 1.32-.27 2-.27.68 0 1.36.09 2 .27 1.53-1.04 2.2-.82 2.2-.82.44 1.1.16 1.92.08 2.12.51.56.82 1.27.82 2.15 0 3.07-1.87 3.75-3.65 3.95.29.25.54.73.54 1.48 0 1.07-.01 1.93-.01 2.2 0 .21.15.46.55.38A8.013 8.013 0 0016 8c0-4.42-3.58-8-8-8z"/>
                </svg>
              </div>
            </el-tooltip>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import { login, getCaptcha } from '@/api/auth'
import { getGithubAuthUrl, isOauthEnabled } from '@/api/oauth'

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
const loginRules = computed(() => ({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  ...(captchaEnabled.value ? {
    captcha: [
      { required: true, message: '请输入验证码', trigger: 'blur' }
    ]
  } : {})
}))

// 验证码是否启用
const captchaEnabled = ref(true)

// 验证码图片地址
const captchaUrl = ref('')

// 加载状态
const loading = ref(false)

// GitHub 登录加载状态
const githubLoading = ref(false)

// 第三方登录是否启用
const oauthEnabled = ref(false)

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
    captchaEnabled.value = res.data.enabled !== false
    if (captchaEnabled.value) {
      captchaUrl.value = res.data.captchaImage
      loginForm.captchaKey = res.data.captchaKey
    }
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
  checkOauthEnabled()
  requestAnimationFrame(() => {
    mounted.value = true
  })
})

// 查询第三方登录是否启用
const checkOauthEnabled = async () => {
  try {
    const res = await isOauthEnabled()
    oauthEnabled.value = res.data === true
  } catch (error) {
    oauthEnabled.value = false
  }
}

// GitHub 登录
const handleGithubLogin = async () => {
  if (githubLoading.value) return
  githubLoading.value = true
  try {
    const res = await getGithubAuthUrl()
    window.location.href = res.data
  } catch (err) {
    ElMessage.error('获取 GitHub 授权地址失败')
  } finally {
    githubLoading.value = false
  }
}
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

/* ========== 第三方登录 ========== */
.oauth-section {
  margin-top: 8px;
}

.oauth-section :deep(.el-divider__text) {
  font-size: 13px;
  color: #909399;
  background: rgba(255, 255, 255, 0.95);
}

.oauth-buttons {
  display: flex;
  justify-content: center;
  gap: 16px;
}

.oauth-btn {
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  border: 1px solid #dcdfe6;
  cursor: pointer;
  color: #606266;
  transition: all 0.3s;
}

.oauth-btn:hover {
  color: #409eff;
  border-color: #409eff;
  background: rgba(64, 158, 255, 0.05);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
}

.oauth-btn-loading {
  opacity: 0.6;
  pointer-events: none;
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
