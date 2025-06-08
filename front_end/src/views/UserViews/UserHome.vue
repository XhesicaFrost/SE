<template>
  <div class="user-home">
    <div class="shop-recommendations">
      <h2>推荐商店</h2>
      <div class="shop-group" v-for="(shop, index) in recommendedShops" :key="index">
        <button class="visit-btn" @click="goToShop(shop.id)">
          <div class="shop-header">
            <img :src="shop.image" alt="店铺图片" class="shop-img">
            <div class="shop-info">
              <h3>{{ shop.name }}</h3>
              <p class="shop-address">{{ shop.address }}</p>
            </div>
          </div>
        </button>
      </div>
    </div>
    <BottomNav :navItems="navItems" />
  </div>
</template>

<script>
import BottomNav from '@/components/bottomNav.vue'
import { BASE_URL, fetchWithTimeout } from '@/config.js'

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
      ],
      recommendedShops: [
      ]
    }
  },
  methods: {
    async fetchRecommendedShops() {
      try {
        const token = localStorage.getItem('token')
        if (!token) {
          alert('请先登录')
          this.$router.push('/login')
          return
        }

        const response = await fetchWithTimeout(`${BASE_URL}/user`, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        })
        const result = await response.json()
        if (result.success && result.code === 200) {
          this.recommendedShops = result.data
        } else {
          this.recommendedShops = []
          alert('获取推荐店铺失败')
        }
      } catch (error) {
        console.error('获取推荐商店失败:', error)
        this.recommendedShops = []
        alert('获取推荐商店失败，请检查网络连接')
      }
    },
    goToShop(shopId) {
      this.$router.push(`/user/shopping/${shopId}`)
    }
  },
  mounted() {
    this.fetchRecommendedShops()
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

.shop-recommendations {
  margin-bottom: 20px;
}

.shop-recommendations h2 {
  font-size: 1.3em;
  color: #333;
  margin-bottom: 15px;
  text-align: center;
}

.shop-group {
  height: 75px;
  width: 100%;
  background: #f8f8f8;
  overflow: hidden;
}

.visit-btn {
  background: none;
  border: none;
  outline: none;
  height: 100%;
  width: 100%;
}

.shop-header {
  border-radius: 10px;
  height: auto;
  width: auto;
  display: flex;
  align-items: center;
  padding: 10px 15px;
  background: #fff;
  border-bottom: 1px solid #eee;
}

.shop-img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 10px;
}

.shop-info h3 {
  margin: 0;
  font-size: 1.1em;
  color: #333;
}

.shop-address {
  margin: 3px 0 0;
  font-size: 0.8em;
  color: #888;
}
</style>