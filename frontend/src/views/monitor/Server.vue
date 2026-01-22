<template>
  <div class="server-monitor">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>服务器监控</span>
          <el-button type="primary" size="small" @click="refreshData">刷新</el-button>
        </div>
      </template>

      <el-row :gutter="20" v-loading="loading">
        <!-- CPU信息 -->
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header>
              <div class="card-title">
                <i class="el-icon-cpu"></i>
                <span>CPU信息</span>
              </div>
            </template>
            <div v-if="serverInfo.cpu">
              <el-descriptions :column="1" border>
                <el-descriptions-item label="CPU型号">
                  {{ serverInfo.cpu.model }}
                </el-descriptions-item>
                <el-descriptions-item label="核心数">
                  {{ serverInfo.cpu.coreCount }}
                </el-descriptions-item>
                <el-descriptions-item label="CPU使用率">
                  <el-progress
                    :percentage="parseFloat(serverInfo.cpu.usedPercent)"
                    :color="getProgressColor(serverInfo.cpu.usedPercent)"
                  />
                </el-descriptions-item>
              </el-descriptions>
            </div>
          </el-card>
        </el-col>

        <!-- 内存信息 -->
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header>
              <div class="card-title">
                <i class="el-icon-memory-card"></i>
                <span>内存信息</span>
              </div>
            </template>
            <div v-if="serverInfo.memory">
              <el-descriptions :column="1" border>
                <el-descriptions-item label="总内存">
                  {{ serverInfo.memory.total }} GB
                </el-descriptions-item>
                <el-descriptions-item label="已使用">
                  {{ serverInfo.memory.used }} GB
                </el-descriptions-item>
                <el-descriptions-item label="可用">
                  {{ serverInfo.memory.available }} GB
                </el-descriptions-item>
                <el-descriptions-item label="使用率">
                  <el-progress
                    :percentage="parseFloat(serverInfo.memory.usedPercent)"
                    :color="getProgressColor(serverInfo.memory.usedPercent)"
                  />
                </el-descriptions-item>
              </el-descriptions>
            </div>
          </el-card>
        </el-col>

        <!-- JVM信息 -->
        <el-col :span="12" style="margin-top: 20px">
          <el-card shadow="hover">
            <template #header>
              <div class="card-title">
                <i class="el-icon-coffee-cup"></i>
                <span>JVM信息</span>
              </div>
            </template>
            <div v-if="serverInfo.jvm">
              <el-descriptions :column="1" border>
                <el-descriptions-item label="JVM版本">
                  {{ serverInfo.jvm.version }}
                </el-descriptions-item>
                <el-descriptions-item label="启动时间">
                  {{ serverInfo.jvm.startTime }}
                </el-descriptions-item>
                <el-descriptions-item label="运行时长">
                  {{ serverInfo.jvm.runTime }}
                </el-descriptions-item>
                <el-descriptions-item label="总内存">
                  {{ serverInfo.jvm.total }} MB
                </el-descriptions-item>
                <el-descriptions-item label="已使用">
                  {{ serverInfo.jvm.used }} MB
                </el-descriptions-item>
                <el-descriptions-item label="可用">
                  {{ serverInfo.jvm.available }} MB
                </el-descriptions-item>
                <el-descriptions-item label="使用率">
                  <el-progress
                    :percentage="parseFloat(serverInfo.jvm.usedPercent)"
                    :color="getProgressColor(serverInfo.jvm.usedPercent)"
                  />
                </el-descriptions-item>
              </el-descriptions>
            </div>
          </el-card>
        </el-col>

        <!-- 系统信息 -->
        <el-col :span="12" style="margin-top: 20px">
          <el-card shadow="hover">
            <template #header>
              <div class="card-title">
                <i class="el-icon-monitor"></i>
                <span>系统信息</span>
              </div>
            </template>
            <div v-if="serverInfo.system">
              <el-descriptions :column="1" border>
                <el-descriptions-item label="操作系统">
                  {{ serverInfo.system.os }}
                </el-descriptions-item>
                <el-descriptions-item label="系统架构">
                  {{ serverInfo.system.arch }}
                </el-descriptions-item>
                <el-descriptions-item label="主机名">
                  {{ serverInfo.system.hostName }}
                </el-descriptions-item>
                <el-descriptions-item label="IP地址">
                  {{ serverInfo.system.ipAddress }}
                </el-descriptions-item>
              </el-descriptions>
            </div>
          </el-card>
        </el-col>

        <!-- 磁盘信息 -->
        <el-col :span="24" style="margin-top: 20px">
          <el-card shadow="hover">
            <template #header>
              <div class="card-title">
                <i class="el-icon-folder"></i>
                <span>磁盘信息</span>
              </div>
            </template>
            <div v-if="serverInfo.disks && serverInfo.disks.length > 0">
              <el-table :data="serverInfo.disks" border style="width: 100%">
                <el-table-column prop="name" label="磁盘名称" width="200" />
                <el-table-column prop="path" label="挂载路径" width="200" />
                <el-table-column prop="total" label="总空间(GB)" width="120" />
                <el-table-column prop="used" label="已使用(GB)" width="120" />
                <el-table-column prop="available" label="可用(GB)" width="120" />
                <el-table-column label="使用率" min-width="200">
                  <template #default="scope">
                    <el-progress
                      :percentage="parseFloat(scope.row.usedPercent)"
                      :color="getProgressColor(scope.row.usedPercent)"
                    />
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script>
import { getServerInfo } from '@/api/monitor'

export default {
  name: 'ServerMonitor',
  data() {
    return {
      loading: false,
      serverInfo: {
        cpu: null,
        memory: null,
        jvm: null,
        system: null,
        disks: []
      },
      timer: null
    }
  },
  mounted() {
    this.loadServerInfo()
    // 每10秒自动刷新
    this.timer = setInterval(() => {
      this.loadServerInfo()
    }, 10000)
  },
  beforeUnmount() {
    if (this.timer) {
      clearInterval(this.timer)
    }
  },
  methods: {
    async loadServerInfo() {
      this.loading = true
      try {
        const res = await getServerInfo()
        if (res.code === 200) {
          this.serverInfo = res.data
        }
      } catch (error) {
        this.$message.error('获取服务器信息失败')
      } finally {
        this.loading = false
      }
    },
    refreshData() {
      this.loadServerInfo()
    },
    getProgressColor(percentage) {
      const percent = parseFloat(percentage)
      if (percent < 60) {
        return '#67c23a'
      } else if (percent < 80) {
        return '#e6a23c'
      } else {
        return '#f56c6c'
      }
    }
  }
}
</script>

<style scoped>
.server-monitor {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  display: flex;
  align-items: center;
  font-weight: bold;
}

.card-title i {
  margin-right: 8px;
  font-size: 18px;
}

.box-card {
  margin-bottom: 20px;
}
</style>
