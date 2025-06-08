<!-- filepath: front_end/src/components/bottomNav.vue -->
<template>
  <div class="bottom-nav">
    <button
      v-for="(item, idx) in navItems"
      :key="idx"
      @click="handleClick(item)"
      :class="{ 'active': item.isActive }"
    >
      {{ item.label }}
  </button>
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
      if (typeof item.action === 'function' && !item.isActive) {
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
button.active {
  background: #3498db;
  cursor: default;
}
button.active:hover {
  background: #3498db !important;
  cursor: default !important;
}
button {
  text-align: center;
  height: 100%;
  width: 100%;
  border: none;
  cursor: pointer;
  font-size: 17px;
  font-weight: bold;
  margin-right: -0.1%;
  transition: background 0.2s;
  color: #0909ff;
}
button:hover {
  background: #66ccff;
}
</style>