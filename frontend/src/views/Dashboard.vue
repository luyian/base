<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon user">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">1,234</div>
              <div class="stat-label">用户总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon role">
              <el-icon><UserFilled /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">56</div>
              <div class="stat-label">角色总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon permission">
              <el-icon><Lock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">89</div>
              <div class="stat-label">权限总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon dept">
              <el-icon><OfficeBuilding /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">23</div>
              <div class="stat-label">部门总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>最近登录</span>
          </template>
          <el-table :data="recentLogins" style="width: 100%">
            <el-table-column prop="username" label="用户名" />
            <el-table-column prop="ip" label="IP地址" />
            <el-table-column prop="time" label="登录时间" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>系统通知</span>
          </template>
          <el-timeline>
            <el-timeline-item
              v-for="notice in notices"
              :key="notice.id"
              :timestamp="notice.time"
            >
              {{ notice.content }}
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { User, UserFilled, Lock, OfficeBuilding } from '@element-plus/icons-vue'

// 最近登录数据
const recentLogins = ref([
  { username: 'admin', ip: '192.168.1.100', time: '2026-01-12 10:30:00' },
  { username: 'test', ip: '192.168.1.101', time: '2026-01-12 09:15:00' },
  { username: 'user1', ip: '192.168.1.102', time: '2026-01-12 08:45:00' }
])

// 系统通知数据
const notices = ref([
  { id: 1, content: '系统将于今晚22:00进行维护', time: '2026-01-12 10:00' },
  { id: 2, content: '新版本功能已上线', time: '2026-01-11 15:30' },
  { id: 3, content: '欢迎使用后台管理系统', time: '2026-01-10 09:00' }
])
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.stat-card {
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30px;
  color: #fff;
  margin-right: 20px;
}

.stat-icon.user {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.role {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.permission {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.dept {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}
</style>
