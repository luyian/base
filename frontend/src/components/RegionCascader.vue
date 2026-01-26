<template>
  <el-cascader
    v-model="selectedValue"
    :options="options"
    :props="cascaderProps"
    :placeholder="placeholder"
    :show-all-levels="showAllLevels"
    clearable
    filterable
    @change="handleChange"
  />
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { getCascadeNodes } from '@/api/region'

// Props 定义
const props = defineProps({
  // 绑定值（区划代码数组）
  modelValue: {
    type: Array,
    default: () => []
  },
  // 最大层级（1-省、2-市、3-区、4-街道）
  level: {
    type: Number,
    default: 4,
    validator: (value) => value >= 1 && value <= 4
  },
  // 是否显示完整路径
  showAllLevels: {
    type: Boolean,
    default: true
  },
  // 占位符
  placeholder: {
    type: String,
    default: '请选择地址'
  }
})

// Emits 定义
const emit = defineEmits(['update:modelValue', 'change'])

// 选中的值
const selectedValue = ref([])

// 级联选择器选项
const options = ref([])

// 级联选择器配置
const cascaderProps = computed(() => ({
  value: 'regionCode',
  label: 'regionName',
  children: 'children',
  lazy: true,
  lazyLoad: async (node, resolve) => {
    const { level, value } = node

    // 如果已达到最大层级，不再加载子节点
    if (level >= props.level) {
      resolve([])
      return
    }

    try {
      // 根节点加载省级数据
      const parentId = level === 0 ? 0 : value
      const { data } = await getCascadeNodes(parentId)

      // 如果当前层级是最大层级-1，则子节点设置为叶子节点
      const nodes = data.map(item => ({
        ...item,
        leaf: level + 1 >= props.level
      }))

      resolve(nodes)
    } catch (error) {
      console.error('加载区划数据失败:', error)
      resolve([])
    }
  }
}))

// 监听 modelValue 变化
watch(
  () => props.modelValue,
  (newValue) => {
    selectedValue.value = newValue || []
  },
  { immediate: true }
)

// 监听选中值变化
watch(selectedValue, (newValue) => {
  emit('update:modelValue', newValue)
})

// 处理选择变化
const handleChange = (value) => {
  emit('change', value)
}

// 初始化根节点数据
const initRootNodes = async () => {
  try {
    const { data } = await getCascadeNodes(0)
    options.value = data.map(item => ({
      ...item,
      leaf: props.level === 1
    }))
  } catch (error) {
    console.error('初始化区划数据失败:', error)
  }
}

// 组件挂载时初始化
initRootNodes()
</script>

<style scoped>
/* 组件样式 */
</style>
