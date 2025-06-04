<template>
  <div class="seller-shop">
    <BottomNav :navItems="navItems" />
  </div>
</template>

<script>
import BottomNav from '@/components/bottomNav.vue'

export default {
  name: 'sellerShop',
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
.seller-shop {
  max-width: 400px;
  margin: 0 auto 36px auto;
  padding: 1em;
  background: #fff;
  min-height: 100vh;
}
</style>