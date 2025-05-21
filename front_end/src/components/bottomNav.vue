<!-- filepath: front_end/src/components/bottomNav.vue -->
<template>
  <div class="bottom-nav">
    <div
      v-for="(item, idx) in navItems"
      :key="idx"
      class="nav-item"
      @click="handleClick(item)"
    >
      {{ item.label }}
    </div>
  </div>
</template>

<script>
export default {
  name: 'BottomNav',
  props: {
    navItems: {
      type: Array,
      required: true
      // 每项格式: { label: '显示内容', action: Function }
    }
  },
  methods: {
    handleClick(item) {
      if (typeof item.action === 'function') {
        item.action()
      }
    }
  },
    mounted() {
        // 这里可以添加一些初始化逻辑
        console.log('BottomNav mounted', this.navItems)

    }   
}
</script>

<style scoped>
.bottom-nav {
  display: flex;
  justify-content: space-around;
  align-items: center;
  background: #fff;
  border-top: 1px solid #eee;
  height: 56px;
  position: fixed;
  left: 50%;
  bottom: 0;
  transform: translateX(-50%);
  width: 100%;
  max-width: 400px;
  box-sizing: border-box;
}
.nav-item {
  flex: 1;
  text-align: center;
  color: #3498db;
  font-size: 1.1em;
  cursor: pointer;
  padding: 0.7em 0;
  transition: background 0.2s;
}
.nav-item:hover {
  background: #f0f8ff;
}
</style>