<template>
  <router-view />
</template>

<script>

export default {
  name: 'App',
  mounted() {
    
    
    this.$nextTick(() => {
      const userInfo = this.$store.state.userStore.userInfo
      if (userInfo.userId) {
        console.log('用户信息已恢复:', userInfo.userName)
      } else {
        console.log('当前无登录用户')
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