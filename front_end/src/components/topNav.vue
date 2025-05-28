<!-- filepath: front_end/src/components/topNav.vue -->
<template>
  <div class="top-bg"></div>
  <div class="top-nav">
    <div class="nav-return">
      <button
        class="return-bottom"
        @click="onClickReturn(navInfo)">
        ←
      </button>
    </div>
    <div class="nav-title">
      <p>{{ navInfo.title }}</p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'TopNav',
  props: {
    navInfo: {
      type: Element,
      required: true
      // 每项格式: { title: '显示内容', pageReturn: returnFunction }
    }
  },
  methods: {
    onClickReturn(info) {
      if (typeof info.pageReturn === 'function') {
       info.pageReturn();
      }
    }
  },
    mounted() {
        // 这里可以添加一些初始化逻辑
        console.log('TopNav mounted', this.navItems)
        
    }   
}
</script>

<style scoped>
.top-bg {
  display: flex;
  background: #eff6f0;
  height: 20px;
  position: fixed;
  left: 50%;
  top: 0;
  transform: translateX(-50%);
  width: 100%;
  max-width: 400px;
  box-sizing: border-box;
}
.top-nav {
  display: flex;
  background: rgba(240,240,240,1);
  height: 48px;
  position: fixed;
  left: 50%;
  top: 20px;
  transform: translateX(-50%);
  width: 100%;
  max-width: 400px;
  box-sizing: border-box;
}
.nav-return {
  position: absolute;
  bottom: 0;
  height: 48px;
  width: 48px;
}
.nav-return button {
  background-color: none;
  border-radius: 35%;
  height: 100%;
  width: 100%;
  text-align: center;
  border: none;
  cursor: pointer;
  font-size: 17px;
  transition: background 0.2s;
  color: #3498db;
}
.nav-return button:hover {
  background: #c0c0d0;
  background-color: rgba(192, 192, 208, 0.5);
}
.nav-title {
  position: absolute;
  left: 100px;
  bottom: 0;
  height: 48px;
  width: 200px;
}
.nav-title p {
  position: absolute;
  height: 20px;
  width: 200px;
  top: 9px;
  margin: 0 auto;
  padding: 0;
  font-size: 20px;
  text-align: center;
}
</style>