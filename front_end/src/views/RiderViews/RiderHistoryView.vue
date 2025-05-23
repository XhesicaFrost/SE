<template>
  <div class="rider-history">
    <!-- 页面标题 -->
    <div class="header-section">
      <h2 class="page-title">历史订单</h2>
    </div>

    <!-- 订单列表 -->
    <div class="history-list">
      <div 
        class="history-item" 
        v-for="order in historyOrders" 
        :key="order.id"
        @click="goToOrderDetail(order.id)"
      >
        <div class="order-card">
          <div class="order-header">
            <div class="order-id">订单号：{{ order.id }}</div>
            <div class="complete-time">{{ order.completeTime }}</div>
          </div>
          
          <div class="order-content">
            <div class="merchant-info">
              <div class="merchant-name">{{ order.merchantName }}</div>
              <div class="merchant-address">商家：{{ order.merchantAddress }}</div>
            </div>
            
            <div class="delivery-info">
              <div class="user-address">送达：{{ order.userAddress }}</div>
              <div class="user-phone">联系：{{ order.userPhone }}</div>
            </div>
          </div>
          
          <div class="order-footer">
            <div class="create-time">下单时间：{{ order.createTime }}</div>
            <div class="status-badge completed">
              已完成
            </div>
          </div>
        </div>
      </div>
      
      <!-- 空状态 -->
      <div v-if="historyOrders.length === 0 && !loading" class="empty-state">
        <div class="empty-icon">📦</div>
        <div class="empty-text">暂无历史订单</div>
      </div>
      
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-state">
        <div class="loading-text">加载中...</div>
      </div>
    </div>

    <!-- 分页控制 -->
    <div class="pagination" v-if="totalPages > 1">
      <button 
        class="page-btn prev-btn" 
        @click="prevPage" 
        :disabled="currentPage === 1"
      >
        上一页
      </button>
      
      <div class="page-info">
        {{ currentPage }} / {{ totalPages }}
      </div>
      
      <button 
        class="page-btn next-btn" 
        @click="nextPage" 
        :disabled="currentPage === totalPages"
      >
        下一页
      </button>
    </div>

    <!-- 底部导航栏 -->
    <BottomNav :navItems="navItems" />
  </div>
</template>

<script>
import BottomNav from '@/components/bottomNav.vue'
import { BASE_URL, fetchWithTimeout } from '@/config.js'
import { mapState } from 'vuex'

export default {
  name: 'RiderHistory',
  components: { BottomNav },
  data() {
    return {
      historyOrders: [],
      loading: false,
      currentPage: 1,
      pageSize: 10,
      totalPages: 1,
      totalCount: 0,
      navItems: [
        { label: '返回主页', action: () => { this.$router.push('/rider') } }
      ]
    }
  },
  computed: {
    ...mapState('userStore', ['userId'])
  },
  methods: {
    // 获取历史订单列表
    async fetchHistoryOrders() {
      if (!this.userId) {
        console.error('用户ID不存在')
        return
      }

      this.loading = true
      try {
        const params = new URLSearchParams({
          userId: this.userId,
          page: this.currentPage,
          pageSize: this.pageSize
        }).toString()
        
        const response = await fetchWithTimeout(`${BASE_URL}/rider/history?${params}`)
        const result = await response.json()
        
        if (result.success && result.data) {
          this.historyOrders = result.data.orders || []
          this.totalCount = result.data.total || 0
          this.totalPages = Math.ceil(this.totalCount / this.pageSize)
          
          console.log('历史订单获取成功:', {
            orders: this.historyOrders.length,
            page: this.currentPage,
            total: this.totalCount
          })
        } else {
          console.error('获取历史订单失败:', result.message)
          this.historyOrders = []
        }
      } catch (e) {
        console.error('网络错误，获取历史订单失败:', e)
        this.historyOrders = []
      } finally {
        this.loading = false
      }
    },

    // 跳转到订单详情页
    goToOrderDetail(orderId) {
      this.$router.push(`/rider/order/${orderId}`)
    },

    // 上一页
    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--
        this.fetchHistoryOrders()
      }
    },

    // 下一页
    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++
        this.fetchHistoryOrders()
      }
    },

    // 刷新列表
    refreshList() {
      this.currentPage = 1
      this.fetchHistoryOrders()
    }
  },

  async mounted() {
    console.log('历史订单页面挂载，用户ID:', this.userId)
    await this.fetchHistoryOrders()
  }
}
</script>

<style scoped>
.rider-history {
  max-width: 500px;
  margin: 0 auto 70px auto;
  padding: 1em;
  background: #f8f8f8;
  min-height: 100vh;
}

.header-section {
  margin-bottom: 1.5em;
}

.page-title {
  font-size: 1.4em;
  font-weight: bold;
  color: #333;
  text-align: center;
  margin: 0;
  padding: 1em 0;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.history-list {
  margin-bottom: 1.5em;
}

.history-item {
  margin-bottom: 1em;
  cursor: pointer;
  transition: transform 0.2s;
}

.history-item:hover {
  transform: translateY(-2px);
}

.order-card {
  background: #fff;
  border-radius: 8px;
  padding: 1.2em;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  border-left: 4px solid #4caf50;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1em;
  padding-bottom: 0.8em;
  border-bottom: 1px solid #eee;
}

.order-id {
  font-weight: bold;
  color: #333;
  font-size: 1em;
}

.complete-time {
  color: #666;
  font-size: 0.9em;
}

.order-content {
  margin-bottom: 1em;
}

.merchant-info, .delivery-info {
  margin-bottom: 0.8em;
}

.merchant-name {
  font-weight: bold;
  color: #333;
  margin-bottom: 0.3em;
}

.merchant-address, .user-address, .user-phone {
  color: #666;
  font-size: 0.9em;
  margin-bottom: 0.2em;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 0.8em;
  border-top: 1px solid #eee;
}

.create-time {
  color: #888;
  font-size: 0.8em;
}

.status-badge {
  padding: 0.3em 0.8em;
  border-radius: 20px;
  font-size: 0.8em;
  font-weight: bold;
}

.status-badge.completed {
  background: #e8f5e9;
  color: #4caf50;
}

.empty-state {
  text-align: center;
  padding: 3em 1em;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.empty-icon {
  font-size: 3em;
  margin-bottom: 0.5em;
}

.empty-text {
  color: #666;
  font-size: 1.1em;
}

.loading-state {
  text-align: center;
  padding: 2em;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.loading-text {
  color: #666;
  font-size: 1em;
}

.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  padding: 1em;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  margin-bottom: 1em;
}

.page-btn {
  padding: 0.6em 1.2em;
  border: none;
  border-radius: 6px;
  font-size: 0.9em;
  cursor: pointer;
  transition: background 0.2s;
}

.page-btn:not(:disabled) {
  background: #2196f3;
  color: #fff;
}

.page-btn:not(:disabled):hover {
  background: #1976d2;
}

.page-btn:disabled {
  background: #ddd;
  color: #999;
  cursor: not-allowed;
}

.page-info {
  font-weight: bold;
  color: #333;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .rider-history {
    padding: 0.5em;
  }
  
  .order-card {
    padding: 1em;
  }
  
  .order-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5em;
  }
  
  .pagination {
    padding: 0.8em;
  }
  
  .page-btn {
    padding: 0.5em 1em;
    font-size: 0.8em;
  }
}
</style>