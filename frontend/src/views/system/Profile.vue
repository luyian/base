<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
        </div>
      </template>

      <el-tabs v-model="activeTab">
        <!-- 基本信息 -->
        <el-tab-pane label="基本信息" name="basic">
          <div class="profile-content">
            <div class="avatar-section">
              <el-avatar :size="120" :src="profileForm.avatar || defaultAvatar" />
              <el-upload
                class="avatar-uploader"
                action="/api/system/profile/avatar"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
                :before-upload="beforeAvatarUpload"
                :headers="uploadHeaders"
              >
                <el-button type="primary" size="small" style="margin-top: 10px">更换头像</el-button>
              </el-upload>
            </div>

            <el-form
              ref="profileFormRef"
              :model="profileForm"
              :rules="profileRules"
              label-width="100px"
              class="profile-form"
            >
              <el-form-item label="用户名">
                <el-input v-model="profileForm.username" disabled />
              </el-form-item>
              <el-form-item label="昵称" prop="nickname">
                <el-input v-model="profileForm.nickname" placeholder="请输入昵称" />
              </el-form-item>
              <el-form-item label="真实姓名" prop="realName">
                <el-input v-model="profileForm.realName" placeholder="请输入真实姓名" />
              </el-form-item>
              <el-form-item label="邮箱" prop="email">
                <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
              </el-form-item>
              <el-form-item label="手机号" prop="phone">
                <el-input v-model="profileForm.phone" placeholder="请输入手机号" />
              </el-form-item>
              <el-form-item label="性别" prop="gender">
                <el-radio-group v-model="profileForm.gender">
                  <el-radio :value="0">女</el-radio>
                  <el-radio :value="1">男</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="部门">
                <el-input v-model="profileForm.departmentName" disabled />
              </el-form-item>
              <el-form-item label="创建时间">
                <el-input v-model="profileForm.createTime" disabled />
              </el-form-item>
              <el-form-item label="最后登录">
                <el-input v-model="profileForm.lastLoginTime" disabled />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleUpdateProfile" :loading="submitLoading">保存修改</el-button>
                <el-button @click="handleReset">重置</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>

        <!-- 修改密码 -->
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
              />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input
                v-model="passwordForm.newPassword"
                type="password"
                placeholder="请输入新密码"
                show-password
              />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                v-model="passwordForm.confirmPassword"
                type="password"
                placeholder="请再次输入新密码"
                show-password
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleUpdatePassword" :loading="passwordLoading">修改密码</el-button>
              <el-button @click="handlePasswordReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getProfile, updateProfile, updatePassword, updateAvatar } from '@/api/profile'

// 默认头像
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 上传请求头
const uploadHeaders = {
  Authorization: `Bearer ${localStorage.getItem('token')}`
}

// 当前标签页
const activeTab = ref('basic')

// 个人信息表单
const profileFormRef = ref(null)
const profileForm = reactive({
  id: null,
  username: '',
  nickname: '',
  realName: '',
  email: '',
  phone: '',
  gender: null,
  avatar: '',
  departmentId: null,
  departmentName: '',
  createTime: '',
  lastLoginTime: ''
})

// 原始数据备份
const originalProfile = reactive({})

// 个人信息表单验证规则
const profileRules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度必须在2-20个字符之间', trigger: 'blur' }
  ],
  realName: [
    { max: 20, message: '真实姓名长度不能超过20个字符', trigger: 'blur' }
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
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
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

const submitLoading = ref(false)
const passwordLoading = ref(false)

// 获取个人信息
const loadProfile = async () => {
  try {
    const res = await getProfile()
    Object.assign(profileForm, res.data)
    Object.assign(originalProfile, res.data)
  } catch (error) {
    ElMessage.error('获取个人信息失败')
  }
}

// 更新个人信息
const handleUpdateProfile = async () => {
  if (!profileFormRef.value) return
  await profileFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        await updateProfile({
          nickname: profileForm.nickname,
          realName: profileForm.realName,
          email: profileForm.email,
          phone: profileForm.phone,
          gender: profileForm.gender,
          avatar: profileForm.avatar
        })
        ElMessage.success('更新成功')
        loadProfile()
      } catch (error) {
        ElMessage.error('更新失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 重置个人信息表单
const handleReset = () => {
  Object.assign(profileForm, originalProfile)
}

// 修改密码
const handleUpdatePassword = async () => {
  if (!passwordFormRef.value) return
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      passwordLoading.value = true
      try {
        await updatePassword(passwordForm)
        ElMessage.success('密码修改成功，请重新登录')
        // 清空表单
        handlePasswordReset()
        // 延迟跳转到登录页
        setTimeout(() => {
          localStorage.removeItem('token')
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
const handlePasswordReset = () => {
  passwordFormRef.value?.resetFields()
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
}

// 头像上传成功
const handleAvatarSuccess = (response) => {
  if (response.code === 200) {
    profileForm.avatar = response.data
    ElMessage.success('头像更新成功')
    loadProfile()
  } else {
    ElMessage.error(response.message || '头像上传失败')
  }
}

// 头像上传前验证
const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB')
    return false
  }
  return true
}

// 初始化
onMounted(() => {
  loadProfile()
})
</script>

<style scoped>
.profile-container {
  padding: 20px;
}

.profile-card {
  max-width: 800px;
  margin: 0 auto;
}

.card-header {
  font-size: 18px;
  font-weight: 500;
}

.profile-content {
  display: flex;
  gap: 40px;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
}

.profile-form {
  flex: 1;
}

.password-form {
  max-width: 500px;
  padding: 20px;
}
</style>
