<template>
  <div class="message-container">
    <el-card shadow="never">
      <template #header>
        <span>消息订阅管理</span>
        <el-button
          v-if="hasPushPermission"
          type="primary"
          size="small"
          :loading="pushing"
          @click="handleManualPush"
          style="float: right"
        >
          手动触发推送
        </el-button>
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

    <!-- 手动推送弹框 -->
    <el-dialog v-model="dialogVisible" title="手动触发推送" width="400px">
      <el-form label-width="100px">
        <el-form-item label="订阅类型">
          <el-select v-model="selectedSubType" placeholder="请选择订阅类型" style="width: 100%">
            <el-option
              v-for="item in subTypes"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="pushing" @click="confirmPush">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { listSubscriptions, toggleSubscription, manualPush, getSubscriptionTypes } from '@/api/subscription'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const subscriptions = ref([])
const subTypes = ref([])
const loading = ref(false)
const pushing = ref(false)
const dialogVisible = ref(false)
const selectedSubType = ref('')

const hasPushPermission = computed(() => {
  return userStore.permissions?.includes('message:push:execute')
})

onMounted(() => {
  fetchSubscriptions()
  fetchSubTypes()
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

const fetchSubTypes = async () => {
  try {
    const res = await getSubscriptionTypes()
    subTypes.value = res.data || []
  } catch (error) {
    console.error('获取订阅类型失败', error)
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

const handleManualPush = async () => {
  selectedSubType.value = ''
  dialogVisible.value = true
}

const confirmPush = async () => {
  if (!selectedSubType.value) {
    ElMessage.warning('请选择订阅类型')
    return
  }
  pushing.value = true
  try {
    await manualPush(selectedSubType.value)
    ElMessage.success('推送已触发')
    dialogVisible.value = false
  } catch (error) {
    console.error('手动推送失败', error)
    ElMessage.error('推送失败')
  } finally {
    pushing.value = false
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
