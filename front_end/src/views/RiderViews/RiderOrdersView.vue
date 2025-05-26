<template>
  <div class="rider-orders-view">
    <!-- 订单过滤器 -->
    <div class="filter-section">
      <div class="filter-row">
        <label>商家名称：</label>
        <input type="text" v-model="sellerNameFilter" placeholder="输入商家名称" />
      </div>
      <div class="filter-row">
        <label>用户地址：</label>
        <input type="text" v-model="userAddressFilter" placeholder="输入用户地址" />
      </div>
      <div class="filter-actions">
        <button @click="applyFilter" class="filter-btn">搜索</button>
        <button @click="clearFilter" class="clear-btn">清空</button>
      </div>
    </div>

    <!-- 订单列表 -->
    <div class="order-list">
      <div class="order-item" v-for="order in pagedOrders" :key="order.id" @click="goToOrderDetail(order.id)">
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
      <div v-if="pagedOrders.length === 0" class="empty-tip">暂无可抢订单</div>
    </div>

    <!-- 分页 -->
    <div class="pagination-bar">
      <button :disabled="page === 1" @click="changePage(page - 1)">上一页</button>
      <span>第 {{ page }} 页 / 共 {{ totalPages }} 页</span>
      <button :disabled="page === totalPages" @click="changePage(page + 1)">下一页</button>
      <span>
        跳转到
        <input type="number" v-model.number="jumpPage" min="1" :max="totalPages" style="width: 50px;" />
        <button @click="changePage(jumpPage)">GO</button>
      </span>
    </div>

    <!-- 底部导航栏 -->
    <BottomNav :navItems="navItems" />
  </div>
</template>

<script>
import BottomNav from '@/components/bottomNav.vue'
import { BASE_URL, fetchWithTimeout } from '@/config.js'
import { mapState, mapActions } from 'vuex'

export default {
  name: 'RiderOrdersView',
  components: { BottomNav },
  data() {
    return {
      allOrders: [],
      sellerNameFilter: '',
      userAddressFilter: '',
      page: 1,
      pageSize: 10,
      jumpPage: 1,
      navItems: [
        { label: '回到主页面', action: () => { this.$router.push('/rider') } }
      ]
    }
  },
  computed: {
    ...mapState('userStore', ['userId']),
    filteredOrders() {
      return this.allOrders.filter(order => {
        const matchseller = !this.sellerNameFilter || 
          order.sellerName.toLowerCase().includes(this.sellerNameFilter.toLowerCase())
        const matchAddress = !this.userAddressFilter || 
          order.userAddress.toLowerCase().includes(this.userAddressFilter.toLowerCase())
        return matchseller && matchAddress
      })
    },
    pagedOrders() {
      const start = (this.page - 1) * this.pageSize
      return this.filteredOrders.slice(start, start + this.pageSize)
    },
    totalPages() {
      return Math.ceil(this.filteredOrders.length / this.pageSize) || 1
    }
  },
  methods: {
    ...mapActions('locationStore', ['startLocationTracking', 'stopLocationTracking']),
    
    goToOrderDetail(orderId) {
      this.$router.push(`/rider/order/${orderId}`)
    },
    
    applyFilter() {
      this.page = 1
      this.jumpPage = 1
      this.fetchOrders()
    },
    
    clearFilter() {
      this.sellerNameFilter = ''
      this.userAddressFilter = ''
      this.page = 1
      this.jumpPage = 1
      this.fetchOrders()
    },
    
    changePage(p) {
      if (p < 1) p = 1
      if (p > this.totalPages) p = this.totalPages
      this.page = p
      this.jumpPage = p
    },
    
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
          // 抢单成功后开始位置追踪
          this.startLocationTracking()
          this.fetchOrders()
        } else {
          alert('抢单失败：' + (result.message || '该订单可能已被其他骑手接取'))
        }
      } catch (e) {
        alert('网络错误，抢单失败')
      }
    },
    
    async fetchOrders() {
      try {
        const params = new URLSearchParams({
          userId: this.userId,
          sellerName: this.sellerNameFilter || '',
          userAddress: this.userAddressFilter || ''
        }).toString()
        
        const response = await fetchWithTimeout(`${BASE_URL}/rider/orderfiltered?${params}`)
        const result = await response.json()
        if (result.success && Array.isArray(result.data)) {
          this.allOrders = result.data
        } else {
          this.allOrders = []
        }
      } catch (e) {
        console.error('获取订单失败', e)
        this.allOrders = []
      }
    }
  },
  
  mounted() {
    this.fetchOrders()
  }
}
</script>

<style scoped>
.rider-orders-view {
  max-width: 500px;
  margin: 0 auto 70px auto;
  padding: 1em;
  background: #f8f8f8;
  min-height: 100vh;
}

.filter-section {
  background: #fff;
  border-radius: 8px;
  padding: 1em;
  margin-bottom: 1em;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.filter-row {
  display: flex;
  align-items: center;
  margin-bottom: 0.7em;
  gap: 0.5em;
}

.filter-row label {
  min-width: 80px;
  font-weight: bold;
  color: #333;
}

.filter-row input {
  flex: 1;
  padding: 0.5em;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1em;
}

.filter-actions {
  display: flex;
  gap: 0.5em;
  justify-content: flex-end;
}

.filter-btn {
  background: #2196f3;
  color: #fff;
  border: none;
  border-radius: 6px;
  padding: 0.5em 1.5em;
  cursor: pointer;
  transition: background 0.2s;
}

.filter-btn:hover {
  background: #1976d2;
}

.clear-btn {
  background: #f44336;
  color: #fff;
  border: none;
  border-radius: 6px;
  padding: 0.5em 1.5em;
  cursor: pointer;
  transition: background 0.2s;
}

.clear-btn:hover {
  background: #d32f2f;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 0.7em;
  margin-bottom: 1.5em;
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

.pagination-bar {
  margin-top: 1.5em;
  display: flex;
  align-items: center;
  gap: 1em;
  flex-wrap: wrap;
  font-size: 1em;
  background: #fff;
  padding: 1em;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.pagination-bar button {
  background: #2196f3;
  color: #fff;
  border: none;
  border-radius: 4px;
  padding: 0.4em 0.8em;
  cursor: pointer;
  transition: background 0.2s;
}

.pagination-bar button:disabled {
  background: #bdbdbd;
  cursor: not-allowed;
}

.pagination-bar button:hover:not(:disabled) {
  background: #1976d2;
}

.pagination-bar input {
  padding: 0.3em;
  border: 1px solid #ddd;
  border-radius: 4px;
  text-align: center;
}
</style>