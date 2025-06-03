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
      { label: '店铺管理', action: () => { this.$router.push('/admin/shops') } },
      { label: '商品管理', action: () => { this.$router.push('/admin/items') }, isActive: true },
      { label: '订单管理', action: () => { this.$router.push('/admin/orders') } }
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