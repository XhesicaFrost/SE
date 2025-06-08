<template>
  <router-view />
</template>
<script>
import * as config from '@/config.js'

export default {
  mounted() {
    // 只打印以 debug 开头的变量和值
    Object.entries(config)
      .filter(([key]) => key.startsWith('debug'))
      .forEach(([key, value]) => {
        console.log(`${key}:`, value)
      })
  }
}
</script>
<style>
body {
  background: #eff6f0; /* 浅绿色 */
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