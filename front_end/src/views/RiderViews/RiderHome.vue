<template>
  <div class="rider-home">
    <!-- 已接订单 -->
    <div class="section">
      <h3 class="section-title">已接订单</h3>
      <div class="order-list">
        <div class="order-item" v-for="order in acceptedOrders" :key="order.id" @click="goToOrderDetail(order.id)">
          <div class="order-info">
            <div class="order-detail">
              <div class="seller-name">{{ order.sellerName }}</div>
              <div class="seller-address">商家地址：{{ order.sellerAddress }}</div>
              <div class="user-address">用户地址：{{ order.userAddress }}</div>
              <div class="create-time">创建时间：{{ order.createTime }}</div>
            </div>
          </div>
          <button 
            class="action-btn" 
            :class="{ 'pickup-btn': order.status === 'accepted', 'complete-btn': order.status === 'picked' }"
            @click.stop="updateOrderStatus(order.id)"
          >
            {{ order.status === 'accepted' ? '接餐' : '完成' }}
          </button>
        </div>
        <div v-if="acceptedOrders.length === 0" class="empty-tip">暂无已接订单</div>
      </div>
    </div>

    <!-- 系统推荐订单 -->
    <div class="section">
      <div class="section-header">
        <h3 class="section-title">系统推荐订单</h3>
        <button class="refresh-btn" @click="refreshRecommendedOrders">刷新</button>
      </div>
      <div class="order-list">
        <div class="order-item" v-for="order in recommendedOrders" :key="order.id" @click="goToOrderDetail(order.id)">
          <div class="order-info">
            <div class="order-detail">
              <div class="seller-name">{{ order.sellerName }}</div>
              <div class="seller-address">商家地址：{{ order.sellerAddress }}</div>
              <div class="user-address">用户地址：{{ order.userAddress }}</div>
              <div class="create-time">创建时间：{{ order.createTime }}</div>
            </div>
          </div>
          <button class="action-btn grab-btn" @click.stop="grabOrder(order.id)">
            抢单
          </button>
        </div>
        <div v-if="recommendedOrders.length === 0" class="empty-tip">暂无推荐订单</div>
      </div>
    </div>

    <!-- 底部导航栏 -->
    <BottomNav :navItems="navItems" />
  </div>
</template>

<script>
import BottomNav from '@/components/bottomNav.vue'
import { BASE_URL, fetchWithTimeout } from '@/config.js'
//import { onBeforeUnmount } from 'vue'
import { mapState, mapActions } from 'vuex'

export default {
  name: 'RiderHome',
  components: { BottomNav },
  data() {
    return {
      acceptedOrders: [],
      recommendedOrders: [],
      userLocation: null,
      navItems: [
        { label: '订单搜索', action: () => { this.$router.push('/rider/orders') } },
        { label: '历史订单', action: () => { this.$router.push('/rider/history') } },
        { label: '个人中心', action: () => { /* 暂时不跳转 */ } }
      ]
    }
  },
  computed: {
    ...mapState('userStore', ['userId'])
  },
  methods: {
    ...mapActions('locationStore', ['startLocationTracking', 'stopLocationTracking']),
    
    goToOrderDetail(orderId) {
      this.$router.push(`/rider/order/${orderId}`)
    },
    
    // 获取当前地理位置
    getCurrentLocation() {
      return new Promise((resolve, reject) => {
        if (!navigator.geolocation) {
          reject(new Error('浏览器不支持地理位置'))
          return
        }
        
        navigator.geolocation.getCurrentPosition(
          (position) => {
            resolve({
              latitude: position.coords.latitude,
              longitude: position.coords.longitude
            })
          },
          (error) => {
            reject(error)
          },
          {
            enableHighAccuracy: true,
            timeout: 10000,
            maximumAge: 600000
          }
        )
      })
    },

    // 检查并更新位置追踪状态
    checkAndUpdateLocationTracking() {
      if (this.acceptedOrders.length > 0) {
        // 如果有已接订单，开始位置追踪
        this.startLocationTracking()
      } else {
        // 如果没有已接订单，停止位置追踪
        this.stopLocationTracking()
      }
    },

    // 获取已接订单
    async fetchAcceptedOrders() {
      try {
        const params = new URLSearchParams({ riderId: this.userId }).toString()
        const response = await fetchWithTimeout(`${BASE_URL}/rider/acceptedorders?${params}`)
        const result = await response.json()
        if (result.success && Array.isArray(result.data)) {
          this.acceptedOrders = result.data
        } else {
          this.acceptedOrders = []
        }
        // 检查并更新位置追踪状态
        this.checkAndUpdateLocationTracking()
      } catch (e) {
        console.error('获取已接订单失败', e)
        this.acceptedOrders = []
        this.checkAndUpdateLocationTracking()
      }
    },

    // 获取推荐订单
    async fetchRecommendedOrders() {
      try {
        if (!this.userLocation) {
          this.userLocation = await this.getCurrentLocation()
        }
        
        const params = new URLSearchParams({
          riderId: this.userId,
          latitude: this.userLocation.latitude,
          longitude: this.userLocation.longitude
        }).toString()
        
        const response = await fetchWithTimeout(`${BASE_URL}/rider/recommendedorders?${params}`)
        const result = await response.json()
        if (result.success && Array.isArray(result.data)) {
          this.recommendedOrders = result.data.slice(0, 10) // 最多10条
        } else {
          this.recommendedOrders = []
        }
      } catch (e) {
        console.error('获取推荐订单失败', e)
        if (e.message.includes('地理位置')) {
          alert('无法获取地理位置，请允许浏览器访问位置信息')
        }
        this.recommendedOrders = []
      }
    },

    // 刷新推荐订单
    async refreshRecommendedOrders() {
      this.userLocation = null // 重新获取位置
      await this.fetchRecommendedOrders()
    },

    // 更新订单状态
    async updateOrderStatus(orderId) {
      const order = this.acceptedOrders.find(o => o.id === orderId)
      if (!order) return

      const nextStatus = order.status === 'accepted' ? 'picked' : 'completed'
      
      try {
        const response = await fetchWithTimeout(`${BASE_URL}/rider/updateorder`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ 
            riderId: this.userId,
            orderId,
            status: nextStatus
          })
        })
        const result = await response.json()
        if (result.success) {
          if (nextStatus === 'picked') {
            order.status = 'picked'
          } else {
            // 订单完成，从已接订单中移除
            const index = this.acceptedOrders.findIndex(o => o.id === orderId)
            if (index !== -1) {
              this.acceptedOrders.splice(index, 1)
            }
          }
          // 重新获取订单列表
          await this.fetchAcceptedOrders()
          await this.fetchRecommendedOrders()
        } else {
          alert('状态更新失败：' + (result.message || '未知错误'))
        }
      } catch (e) {
        alert('网络错误，状态更新失败')
      }
    },

    // 抢单
    async grabOrder(orderId) {
      try {
        const response = await fetchWithTimeout(`${BASE_URL}/rider/chooseorder`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ 
            riderId: this.userId,
            orderId 
          })
        })
        const result = await response.json()
        if (result.success) {
          alert('抢单成功！')
          // 重新获取订单列表
          await this.fetchAcceptedOrders()
          await this.fetchRecommendedOrders()
        } else {
          alert('抢单失败：' + (result.message || '该订单可能已被其他骑手接取'))
        }
      } catch (e) {
        alert('网络错误，抢单失败')
      }
    }
  },
  
  async mounted() {
    await this.fetchAcceptedOrders()
    await this.fetchRecommendedOrders()
  },
  
//   onBeforeUnmount() {
//     // 组件销毁时不停止位置追踪，因为用户可能去到其他骑手页面
//     // 如果需要在退出骑手相关页面时停止追踪，应该在App.vue或路由守卫中处理
//   }
}
</script>

<style scoped>
.rider-home {
  max-width: 500px;
  margin: 0 auto 36px auto;
  padding: 1em;
  background: #f8f8f8;
  min-height: 100vh;
}
.section {
  margin-bottom: 1.5em;
}
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.8em;
}
.section-title {
  font-size: 1.3em;
  font-weight: bold;
  color: #333;
  padding-left: 0.5em;
  border-left: 4px solid #2196f3;
  margin: 0;
}
.refresh-btn {
  background: #4caf50;
  color: #fff;
  border: none;
  border-radius: 6px;
  padding: 0.4em 1em;
  font-size: 0.9em;
  cursor: pointer;
  transition: background 0.2s;
}
.refresh-btn:hover {
  background: #43a047;
}
.order-list {
  display: flex;
  flex-direction: column;
  gap: 0.7em;
}
.order-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-radius: 8px;
  padding: 1em;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  cursor: pointer;
  transition: transform 0.2s;
}
.order-item:hover {
  transform: translateY(-1px);
}
.order-info {
  flex: 1;
}
.order-detail {
  display: flex;
  flex-direction: column;
  gap: 0.3em;
}
.seller-name {
  font-weight: bold;
  color: #333;
  font-size: 1.1em;
}
.seller-address {
  color: #666;
  font-size: 0.95em;
}
.user-address {
  color: #666;
  font-size: 0.95em;
}
.create-time {
  color: #888;
  font-size: 0.9em;
}
.action-btn {
  border: none;
  border-radius: 6px;
  padding: 0.6em 1.5em;
  font-size: 1em;
  cursor: pointer;
  transition: background 0.2s;
  margin-left: 1em;
}
.pickup-btn {
  background: #ff9800;
  color: #fff;
}
.pickup-btn:hover {
  background: #f57c00;
}
.complete-btn {
  background: #4caf50;
  color: #fff;
}
.complete-btn:hover {
  background: #43a047;
}
.grab-btn {
  background: #2196f3;
  color: #fff;
}
.grab-btn:hover {
  background: #1976d2;
}
.empty-tip {
  color: #aaa;
  font-size: 0.95em;
  margin: 1em 0;
  text-align: center;
  padding: 2em;
  background: #fff;
  border-radius: 8px;
}
</style>