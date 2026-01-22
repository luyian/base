<template>
  <div class="cache-monitor">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>缓存监控</span>
          <el-button type="primary" size="small" @click="refreshData">刷新</el-button>
        </div>
      </template>

      <el-row :gutter="20" v-loading="loading">
        <!-- Redis信息 -->
        <el-col :span="8">
          <el-card shadow="hover">
            <template #header>
              <div class="card-title">
                <i class="el-icon-info"></i>
                <span>Redis信息</span>
              </div>
            </template>
            <div v-if="cacheInfo">
              <el-descriptions :column="1" border>
                <el-descriptions-item label="Redis版本">
                  {{ cacheInfo.version }}
                </el-descriptions-item>
                <el-descriptions-item label="运行模式">
                  {{ cacheInfo.mode }}
                </el-descriptions-item>
                <el-descriptions-item label="端口">
                  {{ cacheInfo.port }}
                </el-descriptions-item>
                <el-descriptions-item label="客户端连接数">
                  {{ cacheInfo.clients }}
                </el-descriptions-item>
              </el-descriptions>
            </div>
          </el-card>
        </el-col>

        <!-- 内存信息 -->
        <el-col :span="8">
          <el-card shadow="hover">
            <template #header>
              <div class="card-title">
                <i class="el-icon-memory-card"></i>
                <span>内存信息</span>
              </div>
            </template>
            <div v-if="cacheInfo">
              <el-descriptions :column="1" border>
                <el-descriptions-item label="已用内存">
                  {{ cacheInfo.usedMemory }} MB
                </el-descriptions-item>
                <el-descriptions-item label="最大内存">
                  {{ cacheInfo.maxMemory }} MB
                </el-descriptions-item>
                <el-descriptions-item label="内存使用率">
                  <el-progress
                    v-if="cacheInfo.memoryUsage"
                    :percentage="parseFloat(cacheInfo.memoryUsage)"
                    :color="getProgressColor(parseFloat(cacheInfo.memoryUsage))"
                  />
                  <span v-else>-</span>
                </el-descriptions-item>
              </el-descriptions>
            </div>
          </el-card>
        </el-col>

        <!-- 性能信息 -->
        <el-col :span="8">
          <el-card shadow="hover">
            <template #header>
              <div class="card-title">
                <i class="el-icon-data-line"></i>
                <span>性能信息</span>
              </div>
            </template>
            <div v-if="cacheInfo">
              <el-descriptions :column="1" border>
                <el-descriptions-item label="键总数">
                  {{ cacheInfo.keys }}
                </el-descriptions-item>
                <el-descriptions-item label="命中率">
                  {{ cacheInfo.hitRate }}%
                </el-descriptions-item>
                <el-descriptions-item label="运行时长">
                  {{ cacheInfo.uptime }}
                </el-descriptions-item>
              </el-descriptions>
            </div>
          </el-card>
        </el-col>

        <!-- 缓存键管理 -->
        <el-col :span="24" style="margin-top: 20px">
          <el-card shadow="hover">
            <template #header>
              <div class="card-title">
                <i class="el-icon-key"></i>
                <span>缓存键管理</span>
              </div>
            </template>

            <div class="search-bar">
              <el-input
                v-model="searchPattern"
                placeholder="请输入搜索模式（支持*通配符）"
                style="width: 300px; margin-right: 10px"
                @keyup.enter="searchKeys"
              >
                <template #prefix>
                  <i class="el-icon-search"></i>
                </template>
              </el-input>
              <el-button type="primary" @click="searchKeys">搜索</el-button>
              <el-button type="danger" @click="handleClearCache">清空缓存</el-button>
            </div>

            <el-table
              :data="cacheKeys"
              stripe
              style="width: 100%; margin-top: 20px"
              v-loading="keysLoading"
            >
              <el-table-column
                prop="key"
                label="缓存键"
                min-width="300"
                show-overflow-tooltip
              >
                <template #default="scope">
                  {{ scope.row }}
                </template>
              </el-table-column>
              <el-table-column
                label="操作"
                width="200"
                align="center"
              >
                <template #default="scope">
                  <el-button
                    type="text"
                    size="small"
                    @click="handleViewValue(scope.row)"
                  >
                    查看值
                  </el-button>
                  <el-button
                    type="text"
                    size="small"
                    style="color: #f56c6c"
                    @click="handleDeleteKey(scope.row)"
                  >
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <!-- 查看缓存值对话框 -->
    <el-dialog
      v-model="valueDialogVisible"
      title="缓存值详情"
      width="60%"
      :close-on-click-modal="false"
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item label="缓存键">
          {{ currentKey.key }}
        </el-descriptions-item>
        <el-descriptions-item label="过期时间">
          {{ currentKey.ttl === -1 ? '永久' : currentKey.ttl === -2 ? '已过期' : currentKey.ttl + '秒' }}
        </el-descriptions-item>
        <el-descriptions-item label="缓存值">
          <el-input
            v-model="currentKey.value"
            type="textarea"
            :rows="10"
            readonly
          />
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="valueDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { getCacheInfo, getCacheKeys, getCacheValue, deleteCacheKey, clearCache } from '@/api/monitor'

export default {
  name: 'CacheMonitor',
  data() {
    return {
      loading: false,
      keysLoading: false,
      cacheInfo: null,
      cacheKeys: [],
      searchPattern: '*',
      valueDialogVisible: false,
      currentKey: {
        key: '',
        value: '',
        ttl: -1
      },
      timer: null
    }
  },
  mounted() {
    this.loadCacheInfo()
    this.searchKeys()
    // 每10秒自动刷新
    this.timer = setInterval(() => {
      this.loadCacheInfo()
    }, 10000)
  },
  beforeUnmount() {
    if (this.timer) {
      clearInterval(this.timer)
    }
  },
  methods: {
    async loadCacheInfo() {
      this.loading = true
      try {
        const res = await getCacheInfo()
        if (res.code === 200) {
          this.cacheInfo = res.data
        }
      } catch (error) {
        this.$message.error('获取缓存信息失败')
      } finally {
        this.loading = false
      }
    },
    async searchKeys() {
      this.keysLoading = true
      try {
        const res = await getCacheKeys(this.searchPattern)
        if (res.code === 200) {
          this.cacheKeys = res.data || []
        }
      } catch (error) {
        this.$message.error('获取缓存键列表失败')
      } finally {
        this.keysLoading = false
      }
    },
    async handleViewValue(key) {
      try {
        const res = await getCacheValue(key)
        if (res.code === 200) {
          this.currentKey = res.data
          this.valueDialogVisible = true
        }
      } catch (error) {
        this.$message.error('获取缓存值失败')
      }
    },
    handleDeleteKey(key) {
      this.$confirm(`确定要删除缓存键 "${key}" 吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const res = await deleteCacheKey(key)
          if (res.code === 200) {
            this.$message.success('删除成功')
            this.searchKeys()
            this.loadCacheInfo()
          }
        } catch (error) {
          this.$message.error('删除失败')
        }
      }).catch(() => {})
    },
    handleClearCache() {
      this.$confirm('确定要清空所有缓存吗？此操作不可恢复！', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }).then(async () => {
        try {
          const res = await clearCache()
          if (res.code === 200) {
            this.$message.success('清空成功')
            this.searchKeys()
            this.loadCacheInfo()
          }
        } catch (error) {
          this.$message.error('清空失败')
        }
      }).catch(() => {})
    },
    refreshData() {
      this.loadCacheInfo()
      this.searchKeys()
    },
    getProgressColor(percentage) {
      if (percentage < 60) {
        return '#67c23a'
      } else if (percentage < 80) {
        return '#e6a23c'
      } else {
        return '#f56c6c'
      }
    }
  }
}
</script>

<style scoped>
.cache-monitor {
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

.search-bar {
  display: flex;
  align-items: center;
}
</style>
