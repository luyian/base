import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'
import compression from 'vite-plugin-compression'

// Docker / 小内存机器构建时设置 VITE_LOW_MEM=1：esbuild 压缩 + 跳过构建期 gzip（Nginx 已开启 gzip）
const lowMem = process.env.VITE_LOW_MEM === '1'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    ...(lowMem
      ? []
      : [
          compression({
            algorithm: 'gzip',
            ext: '.gz',
            threshold: 10240
          })
        ])
  ],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  },
  build: {
    // 代码分割策略
    rollupOptions: {
      output: {
        // 分割代码块
        manualChunks(id) {
          if (!id.includes('node_modules')) {
            return
          }
          if (id.includes('element-plus')) {
            return 'element-plus'
          }
          if (id.includes('@element-plus/icons-vue')) {
            return 'element-plus-icons'
          }
          if (id.includes('echarts')) {
            return 'echarts'
          }
          if (id.includes('jspdf')) {
            return 'jspdf'
          }
          if (id.includes('vue-router')) {
            return 'vue-router'
          }
          if (id.includes('pinia')) {
            return 'pinia'
          }
          if (id.includes('vue')) {
            return 'vue-vendor'
          }
          if (id.includes('axios')) {
            return 'axios'
          }
          if (id.includes('dayjs')) {
            return 'dayjs'
          }
          return 'vendor'
        },
        // 静态资源分类打包
        chunkFileNames: 'assets/js/[name]-[hash].js',
        entryFileNames: 'assets/js/[name]-[hash].js',
        assetFileNames: 'assets/[ext]/[name]-[hash].[ext]'
      }
    },
    // 启用 CSS 代码分割
    cssCodeSplit: true,
    // terser 内存占用高；Docker/小内存务必 VITE_LOW_MEM=1 走 esbuild
    minify: lowMem ? 'esbuild' : 'terser',
    esbuild: lowMem
      ? { drop: ['console', 'debugger'] }
      : undefined,
    ...(!lowMem
      ? {
          terserOptions: {
            compress: {
              drop_console: true,
              drop_debugger: true
            }
          }
        }
      : {}),
    // 启用源码映射（生产环境可关闭）
    sourcemap: false,
    // chunk 大小警告阈值
    chunkSizeWarningLimit: 1300
  }
})
