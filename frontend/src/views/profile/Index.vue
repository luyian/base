<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <!-- 左侧个人信息卡片 -->
      <el-col :span="6">
        <el-card class="user-info-card">
          <div class="user-avatar-section">
            <div class="avatar-wrapper" @click="handleAvatarClick">
              <el-avatar :size="120" :src="userInfo.avatar || defaultAvatar" />
              <div class="avatar-overlay">
                <el-icon :size="30"><Upload /></el-icon>
                <div class="upload-text">点击上传</div>
              </div>
            </div>
            <input
              ref="avatarInputRef"
              type="file"
              accept="image/*"
              style="display: none"
              @change="handleAvatarChange"
            />
          </div>

          <div class="user-info-content">
            <div class="info-item">
              <div class="info-label">用户名</div>
              <div class="info-value">{{ userInfo.username }}</div>
            </div>
            <div class="info-item">
              <div class="info-label">昵称</div>
              <div class="info-value">{{ userInfo.nickname || '-' }}</div>
            </div>
            <div class="info-item">
              <div class="info-label">部门</div>
              <div class="info-value">{{ userInfo.departmentName || '-' }}</div>
            </div>
            <div class="info-item">
              <div class="info-label">角色</div>
              <div class="info-value">
                <el-tag
                  v-for="role in userInfo.roles"
                  :key="role.id"
                  size="small"
                  style="margin-right: 5px; margin-bottom: 5px"
                >
                  {{ role.roleName }}
                </el-tag>
                <span v-if="!userInfo.roles || userInfo.roles.length === 0">-</span>
              </div>
            </div>
            <div class="info-item">
              <div class="info-label">注册时间</div>
              <div class="info-value">{{ userInfo.createTime || '-' }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧标签页 -->
      <el-col :span="18">
        <el-card class="tabs-card">
          <el-tabs v-model="activeTab">
            <!-- 基本信息标签页 -->
            <el-tab-pane label="基本信息" name="basic">
              <el-form
                ref="profileFormRef"
                :model="profileForm"
                :rules="profileRules"
                label-width="100px"
                class="profile-form"
              >
                <el-form-item label="昵称" prop="nickname">
                  <el-input
                    v-model="profileForm.nickname"
                    placeholder="请输入昵称"
                    clearable
                  />
                </el-form-item>
                <el-form-item label="邮箱" prop="email">
                  <el-input
                    v-model="profileForm.email"
                    placeholder="请输入邮箱"
                    clearable
                  />
                </el-form-item>
                <el-form-item label="手机号" prop="phone">
                  <el-input
                    v-model="profileForm.phone"
                    placeholder="请输入手机号"
                    clearable
                  />
                </el-form-item>
                <el-form-item label="性别" prop="gender">
                  <el-radio-group v-model="profileForm.gender">
                    <el-radio :value="1">男</el-radio>
                    <el-radio :value="0">女</el-radio>
                    <el-radio :value="2">未知</el-radio>
                  </el-radio-group>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="handleSaveProfile" :loading="saveLoading">
                    保存
                  </el-button>
                  <el-button @click="handleResetProfile">重置</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>

            <!-- 修改密码标签页 -->
            <el-tab-pane label="修改密码" name="password">
              <el-form
                ref="passwordFormRef"
                :model="passwordForm"
                :rules="passwordRules"
                label-width="100px"
                class="password-form"
              >
                <el-form-item label="旧密码" prop="oldPassword">
                  <el-input
                    v-model="passwordForm.oldPassword"
                    type="password"
                    placeholder="请输入旧密码"
                    show-password
                    clearable
                  />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                  <el-input
                    v-model="passwordForm.newPassword"
                    type="password"
                    placeholder="请输入新密码（6-20位）"
                    show-password
                    clearable
                  />
                </el-form-item>
                <el-form-item label="确认密码" prop="confirmPassword">
                  <el-input
                    v-model="passwordForm.confirmPassword"
                    type="password"
                    placeholder="请再次输入新密码"
                    show-password
                    clearable
                  />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="handleUpdatePassword" :loading="passwordLoading">
                    修改
                  </el-button>
                  <el-button @click="handleResetPassword">重置</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>

            <!-- 第三方账号标签页 -->
            <el-tab-pane label="第三方账号" name="oauth">
              <div class="oauth-manage">
                <div class="oauth-item">
                  <div class="oauth-item-left">
                    <svg viewBox="0 0 16 16" width="28" height="28" fill="#24292f" class="oauth-icon">
                      <path d="M8 0C3.58 0 0 3.58 0 8c0 3.54 2.29 6.53 5.47 7.59.4.07.55-.17.55-.38 0-.19-.01-.82-.01-1.49-2.01.37-2.53-.49-2.69-.94-.09-.23-.48-.94-.82-1.13-.28-.15-.68-.52-.01-.53.63-.01 1.08.58 1.23.82.72 1.21 1.87.87 2.33.66.07-.52.28-.87.51-1.07-1.78-.2-3.64-.89-3.64-3.95 0-.87.31-1.59.82-2.15-.08-.2-.36-1.02.08-2.12 0 0 .67-.21 2.2.82.64-.18 1.32-.27 2-.27.68 0 1.36.09 2 .27 1.53-1.04 2.2-.82 2.2-.82.44 1.1.16 1.92.08 2.12.51.56.82 1.27.82 2.15 0 3.07-1.87 3.75-3.65 3.95.29.25.54.73.54 1.48 0 1.07-.01 1.93-.01 2.2 0 .21.15.46.55.38A8.013 8.013 0 0016 8c0-4.42-3.58-8-8-8z"/>
                    </svg>
                    <div class="oauth-item-info">
                      <div class="oauth-item-name">GitHub</div>
                      <div class="oauth-item-status" v-if="githubBinding">
                        已绑定：{{ githubBinding.oauthName }}
                      </div>
                      <div class="oauth-item-status unbound" v-else>未绑定</div>
                    </div>
                  </div>
                  <el-button
                    v-if="githubBinding"
                    type="danger"
                    size="small"
                    plain
                    :loading="unbindLoading"
                    @click="handleUnbind('github')"
                  >
                    解绑
                  </el-button>
                  <el-button
                    v-else
                    type="primary"
                    size="small"
                    plain
                    :loading="bindGithubLoading"
                    @click="handleBindGithub"
                  >
                    绑定
                  </el-button>
                </div>

                <!-- 飞书绑定 -->
                <div class="oauth-item">
                  <div class="oauth-item-left">
                    <svg viewBox="0 0 24 24" width="28" height="28" class="oauth-icon">
                      <path d="M3.576 5.856l5.808 4.896-5.808 7.392V5.856z" fill="#00D6B9"/>
                      <path d="M3.576 18.144l5.808-7.392 4.032 3.408-5.088 6.48a2.88 2.88 0 01-4.752-2.496z" fill="#3370FF"/>
                      <path d="M13.416 14.16l4.032-5.136a2.88 2.88 0 014.752 2.496V18.144l-8.784-3.984z" fill="#3370FF"/>
                      <path d="M22.2 11.52a2.88 2.88 0 00-4.752-2.496l-4.032 5.136 8.784 3.984V11.52z" fill="#00D6B9"/>
                    </svg>
                    <div class="oauth-item-info">
                      <div class="oauth-item-name">飞书</div>
                      <div class="oauth-item-status" v-if="feishuBindInfo">
                        已绑定：{{ feishuBindInfo.feishuName || feishuBindInfo.openId }}
                      </div>
                      <div class="oauth-item-status unbound" v-else>未绑定</div>
                    </div>
                  </div>
                  <el-button
                    v-if="feishuBindInfo"
                    type="danger"
                    size="small"
                    plain
                    :loading="unbindFeishuLoading"
                    @click="handleUnbindFeishu"
                  >
                    解绑
                  </el-button>
                  <el-button
                    v-else
                    type="primary"
                    size="small"
                    plain
                    :loading="bindFeishuLoading"
                    @click="handleBindFeishu"
                  >
                    绑定
                  </el-button>
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Upload } from '@element-plus/icons-vue'
import { getProfile, updateProfile, updatePassword } from '@/api/profile'
import { uploadAvatar } from '@/api/file'
import { listOauthBindings, unbindOauth } from '@/api/oauth'
import { getGithubAuthUrl } from '@/api/oauth'
import { getFeishuBindInfo, getFeishuAuthUrl, unbindFeishu } from '@/api/feishu'
import { useUserStore } from '@/store/user'

// 默认头像
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 用户 store
const userStore = useUserStore()

// 当前标签页
const activeTab = ref('basic')

// 用户信息
const userInfo = reactive({
  id: null,
  username: '',
  nickname: '',
  email: '',
  phone: '',
  gender: null,
  avatar: '',
  departmentId: null,
  departmentName: '',
  roles: [],
  createTime: ''
})

// 个人信息表单
const profileFormRef = ref(null)
const profileForm = reactive({
  nickname: '',
  email: '',
  phone: '',
  gender: null
})

// 原始个人信息备份
const originalProfileForm = reactive({})

// 个人信息表单验证规则
const profileRules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度必须在2-20个字符之间', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ]
}

// 修改密码表单
const passwordFormRef = ref(null)
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 密码表单验证规则
const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入旧密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度必须在6-20位之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const saveLoading = ref(false)
const passwordLoading = ref(false)

// 头像输入框引用
const avatarInputRef = ref(null)

// 获取个人信息
const loadProfile = async () => {
  try {
    const res = await getProfile()
    Object.assign(userInfo, res.data)

    // 初始化表单数据
    profileForm.nickname = res.data.nickname || ''
    profileForm.email = res.data.email || ''
    profileForm.phone = res.data.phone || ''
    profileForm.gender = res.data.gender

    // 备份原始数据
    Object.assign(originalProfileForm, profileForm)

    // 更新 store 中的用户信息
    userStore.setUserInfo(res.data)
  } catch (error) {
    ElMessage.error('获取个人信息失败')
  }
}

// 个人信息
const handleSaveProfile = async () => {
  if (!profileFormRef.value) return

  await profileFormRef.value.validate(async (valid) => {
    if (valid) {
      saveLoading.value = true
      try {
        await updateProfile({
          nickname: profileForm.nickname,
          email: profileForm.email,
          phone: profileForm.phone,
          gender: profileForm.gender
        })
        ElMessage.success('保存成功')
        await loadProfile()
      } catch (error) {
        ElMessage.error(error.message || '保存失败')
      } finally {
        saveLoading.value = false
      }
    }
  })
}

// 重置个人信息表单
const handleResetProfile = () => {
  Object.assign(profileForm, originalProfileForm)
  profileFormRef.value?.clearValidate()
}

// 修改密码
const handleUpdatePassword = async () => {
  if (!passwordFormRef.value) return

  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      passwordLoading.value = true
      try {
        await updatePassword({
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword,
          confirmPassword: passwordForm.confirmPassword
        })
        ElMessage.success('密码修改成功，请重新登录')

        // 清空表单
        handleResetPassword()

        // 延迟跳转到登录页
        setTimeout(() => {
          userStore.logout()
          window.location.href = '/login'
        }, 1500)
      } catch (error) {
        ElMessage.error(error.message || '密码修改失败')
      } finally {
        passwordLoading.value = false
      }
    }
  })
}

// 重置密码表单
const handleResetPassword = () => {
  passwordFormRef.value?.resetFields()
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
}

// 点击头像
const handleAvatarClick = () => {
  avatarInputRef.value?.click()
}

// 头像文件改变
const handleAvatarChange = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  // 验证文件类型
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return
  }

  // 验证文件大小
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB')
    return
  }

  // 上传头像
  try {
    const res = await uploadAvatar(file)
    if (res.code === 200) {
      ElMessage.success('头像上传成功')
      await loadProfile()
    } else {
      ElMessage.error(res.message || '头像上传失败')
    }
  } catch (error) {
    ElMessage.error(error.message || '头像上传失败')
  }

  // 清空 input 值，以便可以重复选择同一文件
  event.target.value = ''
}

// 初始化
onMounted(() => {
  loadProfile()
  loadOauthBindings()
  loadFeishuBindInfo()
})

// 第三方账号绑定列表
const oauthBindings = ref([])
const githubBinding = ref(null)
const unbindLoading = ref(false)
const bindGithubLoading = ref(false)

// 加载第三方账号绑定列表
const loadOauthBindings = async () => {
  try {
    const res = await listOauthBindings()
    oauthBindings.value = res.data || []
    githubBinding.value = oauthBindings.value.find(item => item.oauthType === 'github') || null
  } catch (error) {
    console.error('获取第三方账号绑定列表失败:', error)
  }
}

// 解绑第三方账号
const handleUnbind = async (oauthType) => {
  try {
    await ElMessageBox.confirm('确定要解绑该第三方账号吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    unbindLoading.value = true
    await unbindOauth(oauthType)
    ElMessage.success('解绑成功')
    await loadOauthBindings()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '解绑失败')
    }
  } finally {
    unbindLoading.value = false
  }
}

// 绑定 GitHub
const handleBindGithub = async () => {
  bindGithubLoading.value = true
  try {
    const res = await getGithubAuthUrl()
    window.location.href = res.data
  } catch (error) {
    ElMessage.error('获取 GitHub 授权地址失败')
  } finally {
    bindGithubLoading.value = false
  }
}

// 飞书绑定相关
const feishuBindInfo = ref(null)
const bindFeishuLoading = ref(false)
const unbindFeishuLoading = ref(false)

// 加载飞书绑定信息
const loadFeishuBindInfo = async () => {
  try {
    const res = await getFeishuBindInfo()
    feishuBindInfo.value = res.data || null
  } catch (error) {
    console.error('获取飞书绑定信息失败:', error)
  }
}

// 绑定飞书（OAuth 跳转）
const handleBindFeishu = async () => {
  bindFeishuLoading.value = true
  try {
    const res = await getFeishuAuthUrl()
    window.location.href = res.data
  } catch (error) {
    ElMessage.error('获取飞书授权地址失败')
  } finally {
    bindFeishuLoading.value = false
  }
}

// 解绑飞书
const handleUnbindFeishu = async () => {
  try {
    await ElMessageBox.confirm('确定要解绑飞书账号吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    unbindFeishuLoading.value = true
    await unbindFeishu()
    ElMessage.success('解绑成功')
    feishuBindInfo.value = null
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '解绑失败')
    }
  } finally {
    unbindFeishuLoading.value = false
  }
}
</script>

<style scoped>
.profile-container {
  padding: 20px;
}

.user-info-card {
  height: 100%;
}

.user-avatar-section {
  display: flex;
  justify-content: center;
  padding: 20px 0;
  border-bottom: 1px solid #ebeef5;
}

.avatar-wrapper {
  position: relative;
  cursor: pointer;
  border-radius: 50%;
  overflow: hidden;
}

.avatar-wrapper:hover .avatar-overlay {
  opacity: 1;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #fff;
  opacity: 0;
  transition: opacity 0.3s;
}

.upload-text {
  margin-top: 5px;
  font-size: 12px;
}

.user-info-content {
  padding: 20px 0;
}

.info-item {
  display: flex;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-item:last-child {
  border-bottom: none;
}

.info-label {
  width: 80px;
  color: #909399;
  font-size: 14px;
  flex-shrink: 0;
}

.info-value {
  flex: 1;
  color: #303133;
  font-size: 14px;
  word-break: break-all;
}

.tabs-card {
  min-height: 500px;
}

.profile-form,
.password-form {
  max-width: 600px;
  padding: 20px;
}

/* ========== 第三方账号管理 ========== */
.oauth-manage {
  padding: 20px;
}

.oauth-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  margin-bottom: 12px;
}

.oauth-item-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.oauth-icon {
  flex-shrink: 0;
}

.oauth-item-name {
  font-size: 15px;
  font-weight: 500;
  color: #303133;
}

.oauth-item-status {
  font-size: 13px;
  color: #67c23a;
  margin-top: 4px;
}

.oauth-item-status.unbound {
  color: #909399;
}
</style>
