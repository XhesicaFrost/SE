<template>
  <TopNav :navInfo="navInfo" />
  <div class="admin-order">
    <!-- 筛选栏 -->
    <div class="filter-bar">
      <div class="filter-group">
        <label>订单状态:</label>
        <select v-model="statusFilter" class="filter-select">
          <option value="">全部</option>
          <option value="pending">待处理</option>
          <option value="preparing">准备中</option>
          <option value="completed">已完成</option>
          <option value="cancelled">已取消</option>
        </select>
      </div>
      
      <div class="filter-group">
        <label>时间范围:</label>
        <input type="date" v-model="startDate" class="date-input" />
        <span>至</span>
        <input type="date" v-model="endDate" class="date-input" />
      </div>
    </div>
    
    <!-- 订单列表 -->
    <div class="order-list">
      <div 
        class="order-card" 
        v-for="order in filteredOrders" 
        :key="order.id"
        @click="viewOrder(order.id)"
      >
        <div class="order-header">
          <div class="order-id">订单号: {{ order.id }}</div>
          <div class="order-status" :class="getStatusClass(order.status)">
            {{ getStatusText(order.status) }}
          </div>
        </div>
        
        <div class="order-details">
          <div class="detail-row">
            <span>店铺:</span>
            <span>{{ order.shopName }}</span>
          </div>
          <div class="detail-row">
            <span>时间:</span>
            <span>{{ formatDate(order.createTime) }}</span>
          </div>
          <div class="detail-row">
            <span>金额:</span>
            <span class="order-price">¥{{ order.totalAmount.toFixed(2) }}</span>
          </div>
          <div class="detail-row">
            <span>商品:</span>
            <span>{{ getItemSummary(order.items) }}</span>
          </div>
        </div>
      </div>
      
      <div v-if="filteredOrders.length === 0" class="empty-tip">
        没有符合条件的订单
      </div>
    </div>
    
    <!-- 底部导航 -->
    <BottomNav :navItems="navItems" />
  </div>
</template>

<script>
import BottomNav from '@/components/bottomNav.vue'
import TopNav from '@/components/topNav.vue'
import { BASE_URL, fetchWithTimeout } from '@/config.js'

export default {
  name: 'AdminOrder',
  components: { BottomNav, TopNav },
  data() {
    return {
      orders: [],
      statusFilter: '',
      startDate: '',
      endDate: '',
      navItems: [
        { label: '店铺管理', action: () => { this.$router.push('/admin/shops') } },
        { label: '商品管理', action: () => { this.$router.push('/admin/items') } },
        { label: '订单管理', action: () => { this.$router.push('/admin/orders') }, isActive: true }
      ],
      navInfo: { title: '订单管理', noReturn: true }
    }
  },
  computed: {
    filteredOrders() {
      return this.orders.filter(order => {
        // 状态筛选
        const matchesStatus = this.statusFilter ? order.status === this.statusFilter : true
        
        // 日期筛选
        let matchesDate = true
        if (this.startDate || this.endDate) {
          const orderDate = new Date(order.createTime)
          const start = this.startDate ? new Date(this.startDate) : new Date(0)
          const end = this.endDate ? new Date(this.endDate + 'T23:59:59') : new Date()
          matchesDate = orderDate >= start && orderDate <= end
        }
        
        return matchesStatus && matchesDate
      })
    }
  },
  methods: {
    async fetchOrders() {
      try {
        const response = await fetchWithTimeout(`${BASE_URL}/admin/orders`)
        const result = await response.json()
        if (result.success && Array.isArray(result.data)) {
          this.orders = result.data
        } else {
          this.orders = []
        }
      } catch (e) {
        console.error('获取订单列表失败:', e)
        this.orders = []
      }
    },
    getStatusText(status) {
      const statusMap = {
        'pending': '待处理',
        'preparing': '准备中',
        'completed': '已完成',
        'cancelled': '已取消'
      }
      return statusMap[status] || status
    },
    getStatusClass(status) {
      return {
        'pending': 'status-pending',
        'preparing': 'status-preparing',
        'completed': 'status-completed',
        'cancelled': 'status-cancelled'
      }[status] || ''
    },
    formatDate(timestamp) {
      const date = new Date(timestamp)
      return `${date.getFullYear()}-${(date.getMonth()+1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
    },
    getItemSummary(items) {
      return items.map(item => `${item.name} × ${item.quantity}`).join('，')
    },
    viewOrder(orderId) {
      this.$router.push(`/admin/order/${orderId}`)
    }
  },
  mounted() {
    this.fetchOrders()
  }
}
</script>

<style scoped>
.admin-order {
  max-width: 500px;
  margin: 48px auto 36px auto;
  padding: 1em;
  background: #fff;
  min-height: 100vh;
}

.filter-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 1em;
  margin-bottom: 1.2em;
  padding-bottom: 0.8em;
  border-bottom: 1px solid #eee;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 0.5em;
  flex: 1;
  min-width: 250px;
}

.filter-group label {
  font-size: 0.9em;
  white-space: nowrap;
}

.filter-select, .date-input {
  flex: 1;
  padding: 0.5em;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 0.95em;
}

.date-input {
  min-width: 120px;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 1em;
}

.order-card {
  background: #f9f9f9;
  border-radius: 8px;
  padding: 1.2em;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  cursor: pointer;
  transition: all 0.2s;
}

.order-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 3px 8px rgba(0,0,0,0.08);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.8em;
  padding-bottom: 0.5em;
  border-bottom: 1px solid #eee;
}

.order-id {
  font-weight: bold;
  font-size: 1.05em;
}

.order-status {
  font-size: 0.9em;
  padding: 0.2em 0.6em;
  border-radius: 12px;
}

.status-pending {
  background: #ffeb3b;
  color: #333;
}

.status-preparing {
  background: #2196f3;
  color: white;
}

.status-completed {
  background: #4caf50;
  color: white;
}

.status-cancelled {
  background: #f44336;
  color: white;
}

.order-details {
  display: flex;
  flex-direction: column;
  gap: 0.3em;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  font-size: 0.95em;
}

.detail-row > span:first-child {
  color: #666;
  min-width: 50px;
}

.order-price {
  color: #e53935;
  font-weight: bold;
}

.empty-tip {
  text-align: center;
  color: #888;
  padding: 2em 0;
  font-size: 1.1em;
}
</style>