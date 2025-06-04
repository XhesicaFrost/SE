<template>
  <div class="user-shopcart">
    <BottomNav :navItems="navItems" />
  </div>
</template>

<script>
import BottomNav from '@/components/bottomNav.vue'

export default {
  name: 'userShopCart',
  components: { BottomNav },
  data() {
    return {
      navItems: [
        { label: '首页', action: () => { this.$router.push('/user') } },
        { label: '搜索', action: () => { this.$router.push('/user/search') } },
        { label: '购物车', action: () => { this.$router.push('/user/shopcart') }, isActive: true },
        { label: '我的', action: () => { this.$router.push('/user/personal') } }
      ]
    }
  }
}
</script>

<style scoped>
.user-shopcart {
  max-width: 400px;
  margin: 0 auto 36px auto;
  padding: 1em;
  background: #fff;
  min-height: 100vh;
}
</style>