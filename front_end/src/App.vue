<template>
  <router-view />
</template>

<script>
import * as config from '@/config.js'

export default {
  name: 'App',
  mounted() {
    console.log('🚀 App 组件已挂载')
    
    // 打印调试信息
    Object.entries(config)
      .filter(([key]) => key.startsWith('debug'))
      .forEach(([key, value]) => {
        console.log(`${key}:`, value)
      })
    
    // ✅ 检查数据恢复情况
    this.$nextTick(() => {
      const userInfo = this.$store.state.userStore.userInfo
      if (userInfo.userId) {
        console.log('✅ 用户信息已恢复:', userInfo.userName)
      } else {
        console.log('📝 当前无登录用户')
      }
    })
  }
}
</script>

<style>
body {
  background: #eff6f0;
  min-height: 100vh;
  margin: 0;
}

#app {
  user-select: none;
  max-width: 400px;
  margin: 0 auto;
  padding: 20px;
}

.form-group {
  margin-bottom: 1rem;
}

.error-message {
  color: red;
  margin-top: 0.5rem;
}
</style>