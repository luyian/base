<template>
  <div class="oauth-bind-container">
    <div class="bind-box">
      <!-- 第三方账号信息 -->
      <div class="oauth-info">
        <el-avatar :size="64" :src="oauthAvatar || defaultAvatar" />
        <div class="oauth-name">{{ oauthName }}</div>
        <div class="oauth-tip">首次使用 GitHub 登录，请选择绑定方式</div>
      </div>

      <el-tabs v-model="activeTab" class="bind-tabs">
        <!-- 创建新账号 -->
        <el-tab-pane label="创建新账号" name="new">
          <div class="tab-content">
            <p class="tab-desc">系统将自动为您创建一个新账号，并与 GitHub 账号绑定。</p>
            <el-button
              type="primary"
              size="large"
              :loading="bindNewLoading"
              class="bind-button"
              @click="handleBindNew"
            >
              一键创建并登录
            </el-button>
          </div>
        </el-tab-pane>

        <!-- 绑定已有账号 -->
        <el-tab-pane label="绑定已有账号" name="exist">
          <div class="tab-content">
            <p class="tab-desc">输入已有账号的用户名和密码，将 GitHub 账号与其绑定。</p>
            <el-form
              ref="bindFormRef"
              :model="bindForm"
              :rules="bindRules"
              label-width="0"
            >
              <el-form-item prop="username">
                <el-input
                  v-model="bindForm.username"
                  placeholder="请输入用户名"
                  prefix-icon="User"
                  size="large"
                />
              </el-form-item>
              <el-form-item prop="password">
                <el-input
                  v-model="bindForm.password"
                  type="password"
                  placeholder="请输入密码"
                  prefix-icon="Lock"
                  size="large"
                  show-password
                  @keyup.enter="handleBindExist"
                />
              </el-form-item>
              <el-form-item>
                <el-button
                  type="primary"
                  size="large"
                  :loading="bindExistLoading"
                  class="bind-button"
                  @click="handleBindExist"
                >
                  绑定并登录
                </el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>
      </el-tabs>

      <div class="back-login">
        <el-button text @click="goLogin">返回登录页</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import { bindNewUser, bindExistUser } from '@/api/oauth'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const oauthToken = ref('')
const oauthName = ref('')
const oauthAvatar = ref('')
const activeTab = ref('new')
const bindNewLoading = ref(false)
const bindExistLoading = ref(false)

const bindFormRef = ref(null)
const bindForm = reactive({
  username: '',
  password: ''
})

const bindRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

const goLogin = () => {
  router.push('/login')
}

/**
 * 登录成功后的通用处理
 */
const handleLoginSuccess = async (token) => {
  userStore.setToken(token)
  await userStore.loadUserInfo()
  ElMessage.success('登录成功')
  router.push('/')
}

/**
 * 创建新账号并绑定
 */
const handleBindNew = async () => {
  bindNewLoading.value = true
  try {
    const res = await bindNewUser({ oauthToken: oauthToken.value })
    await handleLoginSuccess(res.data.token)
  } catch (err) {
    ElMessage.error(err.message || '创建账号失败')
  } finally {
    bindNewLoading.value = false
  }
}

/**
 * 绑定已有账号
 */
const handleBindExist = async () => {
  if (!bindFormRef.value) return

  await bindFormRef.value.validate(async (valid) => {
    if (valid) {
      bindExistLoading.value = true
      try {
        const res = await bindExistUser({
          oauthToken: oauthToken.value,
          username: bindForm.username,
          password: bindForm.password
        })
        await handleLoginSuccess(res.data.token)
      } catch (err) {
        ElMessage.error(err.message || '绑定失败')
      } finally {
        bindExistLoading.value = false
      }
    }
  })
}

onMounted(() => {
  oauthToken.value = route.query.oauthToken || ''
  oauthName.value = route.query.oauthName || ''
  oauthAvatar.value = route.query.oauthAvatar || ''

  if (!oauthToken.value) {
    ElMessage.error('绑定凭证缺失，请重新授权')
    router.push('/login')
  }
})
</script>

<style scoped>
.oauth-bind-container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
}

.bind-box {
  width: 440px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.oauth-info {
  text-align: center;
  margin-bottom: 24px;
}

.oauth-name {
  margin-top: 12px;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.oauth-tip {
  margin-top: 8px;
  font-size: 13px;
  color: #909399;
}

.bind-tabs :deep(.el-tabs__header) {
  margin-bottom: 16px;
}

.tab-content {
  padding: 8px 0;
}

.tab-desc {
  font-size: 13px;
  color: #909399;
  margin: 0 0 20px;
}

.bind-button {
  width: 100%;
  height: 44px;
  border-radius: 8px;
  font-size: 15px;
}

.back-login {
  text-align: center;
  margin-top: 16px;
}
</style>
