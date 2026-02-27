<template>
  <div class="oauth-callback">
    <div class="callback-box">
      <el-icon v-if="!error" class="loading-icon" :size="48"><Loading /></el-icon>
      <el-icon v-else class="error-icon" :size="48"><CircleCloseFilled /></el-icon>
      <p class="callback-text">{{ message }}</p>
      <el-button v-if="error" type="primary" @click="goLogin">返回登录</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Loading, CircleCloseFilled } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { handleGithubCallback } from '@/api/oauth'
import { handleFeishuCallback } from '@/api/feishu'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const message = ref('正在处理授权，请稍候...')
const error = ref(false)

const goLogin = () => {
  router.push('/login')
}

/**
 * 处理飞书 OAuth 回调
 * 飞书绑定场景：用户已登录，回调后绑定到当前账号，跳回个人中心
 */
const processFeishuCallback = async (code, state) => {
  try {
    await handleFeishuCallback({ code, state })
    message.value = '绑定成功，正在跳转...'
    setTimeout(() => router.push('/profile'), 500)
  } catch (err) {
    message.value = err.message || '飞书绑定失败，请重试'
    error.value = true
  }
}

/**
 * 处理 GitHub OAuth 回调
 * GitHub 登录场景：可能未登录，回调后登录或跳转绑定页
 */
const processGithubCallback = async (code, state) => {
  try {
    const res = await handleGithubCallback({ code, state })
    const data = res.data

    if (!data.needBind) {
      userStore.setToken(data.token)
      await userStore.loadUserInfo()
      message.value = '登录成功，正在跳转...'
      setTimeout(() => router.push('/'), 500)
    } else {
      router.push({
        path: '/oauth/bind',
        query: {
          oauthToken: data.oauthToken,
          oauthName: data.oauthName,
          oauthAvatar: data.oauthAvatar
        }
      })
    }
  } catch (err) {
    message.value = err.message || '授权处理失败，请重试'
    error.value = true
  }
}

onMounted(async () => {
  const code = route.query.code
  const state = route.query.state
  const platform = route.query.platform

  if (!code || !state) {
    message.value = '授权参数缺失，请重新登录'
    error.value = true
    return
  }

  if (platform === 'feishu') {
    await processFeishuCallback(code, state)
  } else {
    await processGithubCallback(code, state)
  }
})
</script>

<style scoped>
.oauth-callback {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
}

.callback-box {
  text-align: center;
  padding: 60px 80px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.loading-icon {
  color: #409eff;
  animation: spin 1.5s linear infinite;
}

.error-icon {
  color: #f56c6c;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.callback-text {
  margin: 24px 0;
  font-size: 16px;
  color: #303133;
}
</style>
