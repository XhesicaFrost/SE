<template>
  <div class="user-home">
    <BottomNav :navItems="navItems" />
  </div>
</template>

<script>
import BottomNav from '@/components/bottomNav.vue'

export default {
  name: 'userHome',
  components: { BottomNav },
  data() {
    return {
      navItems: [
        { label: '首页', action: () => { this.$router.push('/user') }, isActive: true },
        { label: '搜索', action: () => { this.$router.push('/user/search') } },
        { label: '购物车', action: () => { this.$router.push('/user/shopcart') } },
        { label: '我的', action: () => { this.$router.push('/user/personal') } }
      ]
    }
  }
}
</script>

<style scoped>
.user-home {
  max-width: 400px;
  margin: 0 auto 36px auto;
  padding: 1em;
  background: #fff;
  min-height: 100vh;
}
</style>