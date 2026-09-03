<template>
  <div class="json-node">
    <!-- 对象/数组节点 -->
    <div v-if="isExpandable" class="node-line">
      <span class="toggle" @click="expanded = !expanded">
        <el-icon v-if="expanded"><ArrowDown /></el-icon>
        <el-icon v-else><ArrowRight /></el-icon>
      </span>
      <span v-if="keyName !== undefined" class="node-key">{{ displayKey }}:</span>
      <template v-if="!expanded">
        <span class="node-bracket" @click="expanded = true">{{ openBracket }}</span>
        <span class="node-ellipsis" @click="expanded = true">…</span>
        <span class="node-bracket" @click="expanded = true">{{ closeBracket }}</span>
        <span class="node-count">{{ childCount }} {{ childCount > 1 ? 'items' : 'item' }}</span>
      </template>
      <span v-else class="node-bracket">{{ openBracket }}</span>
    </div>

    <!-- 展开的子节点 -->
    <div v-if="isExpandable && expanded" class="node-children">
      <json-node
        v-for="(child, index) in childEntries"
        :key="child.key"
        :key-name="child.key"
        :value="child.value"
        :depth="depth + 1"
        :is-array-item="isArray"
        :index="index"
        :expand-signal="expandSignal"
        :collapse-signal="collapseSignal"
      />
      <div class="node-line node-close">
        <span class="toggle-placeholder"></span>
        <span class="node-bracket">{{ closeBracket }}</span>
      </div>
    </div>

    <!-- 原始值节点 -->
    <div v-if="!isExpandable" class="node-line">
      <span class="toggle-placeholder"></span>
      <span v-if="keyName !== undefined" class="node-key">{{ displayKey }}:</span>
      <span :class="valueClass">{{ displayValue }}</span>
    </div>
  </div>
</template>

<script>
import { ArrowDown, ArrowRight } from '@element-plus/icons-vue'

/**
 * JSON 树形节点组件（递归）
 * 仿 Chrome DevTools Preview 效果：对象/数组可展开收起，原始值按类型高亮
 */
export default {
  name: 'JsonNode',
  components: { ArrowDown, ArrowRight },
  props: {
    /** 节点键名（根节点为 undefined） */
    keyName: {
      type: [String, Number],
      default: undefined
    },
    /** 节点值 */
    value: {
      type: null,
      default: null
    },
    /** 当前深度，根节点为 0 */
    depth: {
      type: Number,
      default: 0
    },
    /** 是否为数组元素（影响键名展示样式） */
    isArrayItem: {
      type: Boolean,
      default: false
    },
    /** 数组下标 */
    index: {
      type: Number,
      default: 0
    },
    /** 展开全部信号（递增触发） */
    expandSignal: {
      type: Number,
      default: 0
    },
    /** 收起全部信号（递增触发） */
    collapseSignal: {
      type: Number,
      default: 0
    }
  },
  data() {
    return {
      // 默认展开前两层，与 Chrome Preview 体验一致
      expanded: this.depth < 2
    }
  },
  computed: {
    isArray() {
      return Array.isArray(this.value)
    },
    isExpandable() {
      return this.value !== null && typeof this.value === 'object'
    },
    displayKey() {
      return this.isArrayItem ? String(this.keyName) : `"${this.keyName}"`
    },
    openBracket() {
      return this.isArray ? '[' : '{'
    },
    closeBracket() {
      return this.isArray ? ']' : '}'
    },
    childEntries() {
      if (this.isArray) {
        return this.value.map((item, idx) => ({ key: idx, value: item }))
      }
      return Object.keys(this.value).map(key => ({ key, value: this.value[key] }))
    },
    childCount() {
      return this.childEntries.length
    },
    valueClass() {
      const v = this.value
      if (v === null) {
        return 'value-null'
      }
      switch (typeof v) {
        case 'string':
          return 'value-string'
        case 'number':
          return 'value-number'
        case 'boolean':
          return 'value-boolean'
        default:
          return ''
      }
    },
    displayValue() {
      const v = this.value
      if (v === null) {
        return 'null'
      }
      if (typeof v === 'string') {
        return `"${v}"`
      }
      return String(v)
    }
  },
  watch: {
    expandSignal() {
      this.expanded = true
    },
    collapseSignal() {
      // 收起全部时根节点保持展开，避免整棵树消失
      this.expanded = this.depth === 0
    }
  }
}
</script>

<style scoped>
.json-node {
  font-family: 'JetBrains Mono', Consolas, 'Courier New', monospace;
  font-size: 13px;
  line-height: 1.7;
}

.node-line {
  display: flex;
  align-items: center;
  white-space: nowrap;
}

.toggle {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 18px;
  height: 18px;
  margin-right: 2px;
  cursor: pointer;
  color: var(--json-text-secondary);
  flex-shrink: 0;
}

.toggle:hover {
  color: var(--json-accent);
}

.toggle-placeholder {
  display: inline-block;
  width: 20px;
  flex-shrink: 0;
}

.node-key {
  color: var(--json-key);
  margin-right: 6px;
}

.node-bracket {
  color: var(--json-text);
  font-weight: 600;
  cursor: default;
}

.node-ellipsis {
  color: var(--json-text-secondary);
  cursor: pointer;
  padding: 0 2px;
}

.node-ellipsis:hover {
  background: var(--json-hover-bg);
  border-radius: 3px;
}

.node-count {
  color: var(--json-text-secondary);
  font-size: 12px;
  margin-left: 8px;
  cursor: default;
}

.node-children {
  margin-left: 11px;
  padding-left: 12px;
  border-left: 1px solid var(--json-border);
}

.node-close {
  margin-left: -23px;
  padding-left: 0;
}

.value-string {
  color: var(--json-string);
  word-break: break-all;
  white-space: pre-wrap;
}

.value-number {
  color: var(--json-number);
}

.value-boolean {
  color: var(--json-boolean);
}

.value-null {
  color: var(--json-null);
}
</style>
