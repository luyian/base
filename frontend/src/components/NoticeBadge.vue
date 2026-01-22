<template>
  <el-badge :value="unreadCount" :hidden="unreadCount === 0" :max="99" class="notice-badge">
    <el-icon :size="20" @click="goToMyNotice" style="cursor: pointer;">
      <Bell />
    </el-icon>
  </el-badge>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { Bell } from '@element-plus/icons-vue'
import { getUnreadCount } from '@/api/notice'

const router = useRouter()
const unreadCount = ref(0)
let timer = null

// 获取未读通知数量
const fetchUnreadCount = async () => {
  try {
    const res = await getUnreadCount()
    unreadCount.value = res.data
  } catch (error) {
    console.error('获取未读通知数量失败', error)
  }
}

// 跳转到我的通知页面
const goToMyNotice = () => {
  router.push('/system/my-notice')
}

// 定时刷新未读数量（每30秒）
onMounted(() => {
  fetchUnreadCount()
  timer = setInterval(fetchUnreadCount, 30000)
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
})

// 暴露刷新方法，供外部调用
defineExpose({
  refresh: fetchUnreadCount
})
</script>

<style scoped>
.notice-badge {
  margin-right: 20px;
}

.notice-badge :deep(.el-badge__content) {
  background-color: #f56c6c;
}
</style>
