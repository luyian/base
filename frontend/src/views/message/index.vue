<template>
  <div class="message-container">
    <el-card shadow="never">
      <template #header>
        <span>消息订阅管理</span>
      </template>

      <el-row :gutter="20" v-loading="loading">
        <el-col :xs="24" :sm="12" :md="8" v-for="sub in subscriptions" :key="sub.subType + sub.channel">
          <el-card class="sub-card" shadow="hover">
            <div class="sub-header">
              <span class="sub-title">{{ sub.subTypeDesc }}</span>
              <el-switch
                v-model="sub.enabled"
                @change="(val) => handleToggle(sub, val)"
                :loading="sub.toggling"
              />
            </div>
            <div class="sub-info">
              <div class="sub-row">
                <span class="label">推送渠道</span>
                <el-tag size="small">{{ sub.channelDesc }}</el-tag>
              </div>
              <div class="sub-row" v-if="sub.lastPushTime">
                <span class="label">最后推送</span>
                <span class="value">{{ sub.lastPushTime }}</span>
              </div>
              <div class="sub-row" v-if="sub.lastPushStatus !== null && sub.lastPushStatus !== undefined">
                <span class="label">推送状态</span>
                <el-tag :type="sub.lastPushStatus === 1 ? 'success' : 'danger'" size="small">
                  {{ sub.lastPushStatus === 1 ? '成功' : '失败' }}
                </el-tag>
              </div>
              <div class="sub-row" v-if="sub.lastPushMsg">
                <span class="label">推送结果</span>
                <span class="value msg-text">{{ sub.lastPushMsg }}</span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-empty v-if="!loading && subscriptions.length === 0" description="暂无可用订阅" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { listSubscriptions, toggleSubscription } from '@/api/subscription'

const subscriptions = ref([])
const loading = ref(false)

onMounted(() => {
  fetchSubscriptions()
})

const fetchSubscriptions = async () => {
  loading.value = true
  try {
    const res = await listSubscriptions()
    subscriptions.value = (res.data || []).map(s => ({ ...s, toggling: false }))
  } catch (error) {
    console.error('获取订阅列表失败', error)
    ElMessage.error('获取订阅列表失败')
  } finally {
    loading.value = false
  }
}

const handleToggle = async (sub, enabled) => {
  sub.toggling = true
  try {
    await toggleSubscription(sub.subType, sub.channel, enabled)
    ElMessage.success(enabled ? '已开启订阅' : '已关闭订阅')
  } catch (error) {
    console.error('切换订阅失败', error)
    ElMessage.error('操作失败')
    sub.enabled = !enabled
  } finally {
    sub.toggling = false
  }
}
</script>

<style scoped>
.message-container {
  padding: 20px;
}

.sub-card {
  margin-bottom: 20px;
}

.sub-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.sub-title {
  font-size: 16px;
  font-weight: bold;
}

.sub-info {
  padding-top: 10px;
  border-top: 1px solid #eee;
}

.sub-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.sub-row .label {
  color: #909399;
  flex-shrink: 0;
}

.sub-row .value {
  text-align: right;
  color: #606266;
}

.msg-text {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
