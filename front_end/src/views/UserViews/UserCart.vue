<template>
  <!-- No changes to template section -->
</template>

<script>
export default {
  methods: {
    async fetchCart() {
      try {
        const token = localStorage.getItem('token')
        if (!token) {
          alert('请先登录')
          this.$router.push('/login')
          return
        }

        const userId = parseInt(this.$store.state.userStore.userInfo.userId)
        if (isNaN(userId)) {
          alert('用户信息无效')
          this.$router.push('/login')
          return
        }

        const response = await fetchWithTimeout(`${BASE_URL}/shopcart`, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        })
        const result = await response.json()
        if (result.success && result.code === 200) {
          this.cartItems = result.data
        } else {
          alert('获取购物车信息失败')
        }
      } catch (error) {
        console.error('获取购物车信息失败:', error)
        alert('获取购物车信息失败，请检查网络连接')
      }
    }
  }
}
</script>

<style>
  /* No changes to style section */
</style> 