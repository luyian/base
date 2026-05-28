<template>
  <div class="not-found">
    <div class="not-found__content">
      <div class="not-found__code">404</div>
      <div class="not-found__title">页面不存在</div>
      <div class="not-found__desc">你访问的页面可能已被删除或暂时不可用。</div>
      <el-button type="primary" @click="handleBack">返回首页</el-button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const handleBack = () => {
  router.push('/')
}
</script>

<style scoped>
.not-found {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: #f5f7fa;
}

.not-found__content {
  text-align: center;
}

.not-found__code {
  font-size: 72px;
  font-weight: 700;
  color: #409eff;
}

.not-found__title {
  margin-top: 8px;
  font-size: 20px;
  color: #303133;
}

.not-found__desc {
  margin: 12px 0 20px;
  color: #909399;
}
</style>
