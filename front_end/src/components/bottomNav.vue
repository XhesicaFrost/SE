<!-- filepath: front_end/src/components/bottomNav.vue -->
<template>
  <div class="bottom-nav">
    <div
      v-for="(item, idx) in navItems"
      :key="idx"
      class="nav-item"
      @click="handleClick(item)"
    >
      <p>{{ item.label }}</p>
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
  place-items: center;
  background: #fff;
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
  font-size: 17px;
  cursor: pointer;
  height: 100%;
  width: 100%;
  padding-bottom: 1%;
  margin-right: -0.1%;
  transition: background 0.2s;
}
.nav-item:hover {
  background: #f0f8ff;
}
</style>