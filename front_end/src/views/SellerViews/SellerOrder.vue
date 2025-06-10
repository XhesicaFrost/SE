<template>
  <TopNav :navInfo="navInfo" />
  <div class="seller-order-view">
    <!-- ✅ 新增：状态选择栏 -->
    <div class="status-filter">
     <button
        v-for="status in statusOptions"
        :key="status.value"
        :class="['status-btn', { active: selectedStatus === status.value }]"
        @click="selectStatus(status.value)"
      >
        {{ status.label }}
        <span v-if="getOrderCountByStatus(status.value) > 0" class="count-badge">
          {{ getOrderCountByStatus(status.value) }}
        </span>
      </button>
    </div>

    <div class="order-list">
      <div
        class="order-item"
        v-for="order in pagedOrders"
        :key="order.id"
      >
        <div class="order-main" @click="toggleExpand(order.id)">
          <div class="order-info">
            <div class="order-id">订单编号：{{ order.id }}</div>
            <div class="order-price">总价：￥{{ order.totalPrice }}</div>
            <div class="order-status">状态：{{ getStatusLabel(order.status) }}</div>
          </div>
          <!-- ✅ 修改：根据状态显示不同的按钮 -->
          <button
            :class="['action-btn', getStatusButtonClass(order.status)]"
            @click.stop="handleOrderAction(order)"
            :disabled="!isActionable(order.status)"
          >
            {{ getActionButtonText(order.status) }}
          </button>
        </div>
        <transition name="expand">
          <div
            class="order-detail"
            v-if="expandedOrderId === order.id"
          >
            <div
              class="order-detail-item"
              v-for="(item, idx) in order.items"
              :key="idx"
            >
              {{ item.name }} × {{ item.count }}：￥{{ (item.price * item.count).toFixed(2) }}
            </div>
          </div>
        </transition>
      </div>
      <div v-if="pagedOrders.length === 0" class="empty-tip">
        {{ selectedStatus ? `暂无${getStatusLabel(selectedStatus)}订单` : '暂无订单' }}
      </div>
    </div>

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
    <BottomNav :navItems="navItems" />
  </div>
</template>

<script>
import BottomNav from '@/components/bottomNav.vue'
import TopNav from '@/components/topNav.vue'
import { BASE_URL, fetchWithTimeout } from '@/config.js'
import { mapState } from 'vuex'

export default {
  name: 'sellerOrder',
  components: { BottomNav, TopNav },
  data() {
    return {
      orders: [],
      page: 1,
      pageSize: 10,
      jumpPage: 1,
      alertTimer: null,
      selectedStatus: 'PREPARING', // ✅ 默认显示备餐中的订单
      navItems: [
        { label: '管理店铺', action: () => { this.$router.push('/seller/shop') } },
        { label: '管理订单', action: () => { this.$router.push('/seller/order') }, isActive: true },
        { label: '查看数据', action: () => { this.$router.push('/seller/data') } }
      ],
      navInfo: { title: '管理订单', pageReturn: () => { this.$router.push('/seller') } },
      expandedOrderId: null,
      // ✅ 修改：废弃 PAID 字段，调整状态配置
      statusOptions: [
        { value: null, label: '全部' },
        { value: 'PREPARING', label: '备餐中' },
        { value: 'READY', label: '已出餐' },
        { value: 'ACCEPTED', label: '已接单' },
        { value: 'PICKED', label: '已取餐' },
        { value: 'DELIVERING', label: '配送中' },
        { value: 'COMPLETED', label: '已完成' }
      ]
    }
  },
  computed: {
    ...mapState('userStore', ['userInfo']),
    ...mapState('sellerStore', ['sellerId']),
    userId() {
      return this.userInfo?.userId || this.sellerId
    },
    // ✅ 修改：根据选择的状态过滤和排序订单
    filteredOrders() {
      let filtered = this.orders
      
      // 根据选择的状态过滤
      if (this.selectedStatus) {
        filtered = this.orders.filter(order => order.status === this.selectedStatus)
      }
      
      // 排序逻辑
      return [...filtered].sort((a, b) => {
        // ✅ 在全部状态下，PREPARING 订单优先级最高
        if (!this.selectedStatus) {
          // 如果 a 是 PREPARING，b 不是，a 排前面
          if (a.status === 'PREPARING' && b.status !== 'PREPARING') {
            return -1
          }
          // 如果 b 是 PREPARING，a 不是，b 排前面
          if (b.status === 'PREPARING' && a.status !== 'PREPARING') {
            return 1
          }
          // 如果都是 PREPARING 或都不是 PREPARING，按时间排序
        }
        
        // 按时间排序（最新的在前）
        if (a.createTime && b.createTime) {
          return new Date(b.createTime) - new Date(a.createTime)
        }
        return b.id - a.id
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
    // ✅ 新增：选择状态
    selectStatus(status) {
      this.selectedStatus = status
      this.page = 1 // 重置到第一页
      this.jumpPage = 1
    },

    // ✅ 新增：获取状态对应的订单数量
    getOrderCountByStatus(status) {
      if (!status) return this.orders.length
      return this.orders.filter(order => order.status === status).length
    },

    // ✅ 新增：获取状态标签
    getStatusLabel(status) {
      const statusConfig = this.statusOptions.find(s => s.value === status)
      return statusConfig ? statusConfig.label : status
    },

    // ✅ 修改：获取按钮文本，废弃 PAID 状态
    getActionButtonText(status) {
      switch (status) {
        case 'PREPARING':
          return '出餐'
        case 'READY':
          return '等待接单'
        case 'ACCEPTED':
          return '等待取餐'
        case 'PICKED':
          return '配送中'
        case 'DELIVERING':
          return '配送中'
        case 'COMPLETED':
          return '已完成'
        default:
          return '查看'
      }
    },

    // ✅ 修改：获取按钮样式类，废弃 PAID 状态
    getStatusButtonClass(status) {
      switch (status) {
        case 'PREPARING':
          return 'action-available'
        case 'READY':
          return 'waiting'
        case 'ACCEPTED':
        case 'PICKED':
        case 'DELIVERING':
          return 'in-progress'
        case 'COMPLETED':
          return 'completed'
        default:
          return 'default'
      }
    },

    // ✅ 新增：判断是否可操作
    isActionable(status) {
      return status === 'PREPARING' // 只有备餐中状态可以点击
    },

    // ✅ 新增：处理订单操作
    handleOrderAction(order) {
      if (order.status === 'PREPARING') {
        this.serveOrder(order.id)
      }
      // 其他状态下只是展示，不做操作
    },

    // ✅ 修复：后端请求获取订单，修复数据解析问题
    async fetchOrders() {
      try {
        console.log('🔍 当前商家ID:', this.sellerId)
        
        if (!this.sellerId) {
          console.warn('⚠️ 商家ID不存在，无法获取订单')
          this.orders = []
          return
        }

        const params = new URLSearchParams({ sellerId: this.sellerId }).toString()
        const response = await fetchWithTimeout(`${BASE_URL}/seller/order?${params}`)
        const result = await response.json()
        
        console.log('📋 获取订单完整响应:', result)
        
        // ✅ 修复：根据实际返回格式解析数据
        if (result.code === 200 && Array.isArray(result.data)) {
          this.orders = result.data
          console.log('✅ 订单加载成功，共', this.orders.length, '个订单')
          console.log('📦 订单详情:', this.orders)
        } else if (result.success && Array.isArray(result.data)) {
          // 兼容 success 字段的响应格式
          this.orders = result.data
          console.log('✅ 订单加载成功（success格式），共', this.orders.length, '个订单')
        } else {
          console.warn('⚠️ 订单数据格式异常:', result)
          this.orders = []
        }
      } catch (error) {
        console.error('❌ 获取订单失败:', error)
        this.orders = []
      }
    },

    // ✅ 恢复：后端请求出餐操作
    async serveOrder(orderId) {
      try {
        console.log('🍽️ 开始出餐操作，订单ID:', orderId)
        
        const response = await fetchWithTimeout(`${BASE_URL}/seller/order/serve`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ orderId })
        })
        
        const result = await response.json()
        console.log('📤 出餐操作响应:', result)
        
        // ✅ 兼容两种响应格式
        if (result.success || result.code === 200) {
          alert('出餐成功！')
          await this.fetchOrders() // 重新获取订单列表
          console.log('✅ 出餐成功，已刷新订单列表')
        } else {
          console.error('❌ 出餐失败:', result.message)
          alert('操作失败：' + (result.message || '未知错误'))
        }
      } catch (error) {
        console.error('❌ 出餐操作网络错误:', error)
        alert('网络错误，操作失败')
      }
    },

    // ✅ 恢复：后端请求提醒消息
    async fetchAlertMessage() {
      try {
        if (!this.userId) {
          console.warn('⚠️ 用户ID不存在，跳过提醒消息检查')
          return
        }
        
        const params = new URLSearchParams({ userId: this.userId }).toString()
        const response = await fetchWithTimeout(`${BASE_URL}/alertMessage?${params}`)
        const result = await response.json()
        
        console.log('📢 提醒消息检查:', result)
        
        if (result.success && result.alertMessage && result.alertMessage.trim()) {
          alert(`📢 系统提醒：\n${result.alertMessage}`)
          console.log('✅ 显示提醒消息:', result.alertMessage)
        } else if (result.code === 200 && result.alertMessage && result.alertMessage.trim()) {
          alert(`📢 系统提醒：\n${result.alertMessage}`)
          console.log('✅ 显示提醒消息:', result.alertMessage)
        }
        
      } catch (error) {
        console.warn('⚠️ 获取提醒消息失败:', error.message)
      }
    },

    startAlertTimer() {
      console.log('🔔 启动提醒消息定时器')
      this.fetchAlertMessage()
      this.alertTimer = setInterval(() => {
        this.fetchAlertMessage()
      }, 5000)
    },

    stopAlertTimer() {
      if (this.alertTimer) {
        console.log('🔕 停止提醒消息定时器')
        clearInterval(this.alertTimer)
        this.alertTimer = null
      }
    },

    changePage(p) {
      if (p < 1) p = 1
      if (p > this.totalPages) p = this.totalPages
      this.page = p
      this.jumpPage = p
    },

    toggleExpand(orderId) {
      this.expandedOrderId = this.expandedOrderId === orderId ? null : orderId
    }
  },

  async mounted() {
    console.log('🚀 SellerOrder 页面挂载')
    console.log('👤 当前用户ID:', this.userId)
    console.log('🏪 当前商家ID:', this.sellerId)
    
    // ✅ 确保有商家ID后再获取订单
    if (this.sellerId) {
      await this.fetchOrders()
    } else {
      console.warn('⚠️ 商家ID未就绪，稍后重试')
      // 等待 Vuex 加载完成
      setTimeout(async () => {
        if (this.sellerId) {
          await this.fetchOrders()
        }
      }, 1000)
    }
    
    this.startAlertTimer()
  },

  beforeUnmount() {
    console.log('🚪 SellerOrder 页面销毁')
    this.stopAlertTimer()
  }
}
</script>

<style scoped>
.seller-order-view {
  max-width: 500px;
  margin: 48px auto 36px auto;
  padding: 1em;
  background: #fff;
  min-height: 100vh;
}

/* ✅ 新增：状态选择栏样式 */
.status-filter {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5em;
  margin-bottom: 1em;
  padding: 0.5em;
  background: #f5f5f5;
  border-radius: 8px;
}

.status-btn {
  position: relative;
  padding: 0.4em 0.8em;
  border: 1px solid #ddd;
  border-radius: 20px;
  background: #fff;
  color: #666;
  font-size: 0.85em;
  cursor: pointer;
  transition: all 0.2s;
}

.status-btn:hover {
  border-color: #1249d5;
  color: #1249d5;
}

.status-btn.active {
  background: #1249d5;
  color: #fff;
  border-color: #1249d5;
}

.count-badge {
  position: absolute;
  top: -8px;
  right: -8px;
  background: #e53935;
  color: #fff;
  border-radius: 10px;
  padding: 0.1em 0.4em;
  font-size: 0.7em;
  min-width: 16px;
  text-align: center;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 0.7em;
}

.order-item {
  background: #f9f9f9;
  border-radius: 6px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.03);
  margin-bottom: 0.5em;
  overflow: hidden;
}

.order-main {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.7em 1em;
  cursor: pointer;
}

.order-info {
  display: flex;
  flex-direction: column;
}

.order-id {
  font-weight: bold;
  font-size: 1.05em;
  margin-bottom: 0.2em;
}

.order-price {
  color: #888;
  font-size: 0.95em;
  margin-bottom: 0.2em;
}

.order-status {
  color: #666;
  font-size: 0.9em;
}

/* ✅ 修改：按钮样式根据状态变化 */
.action-btn {
  border: none;
  border-radius: 6px;
  padding: 0.5em 1.2em;
  font-size: 1em;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn.action-available {
  background: #e53935;
  color: #fff;
}

.action-btn.action-available:hover:not(:disabled) {
  background: #b71c1c;
}

.action-btn.waiting {
  background: #ff9800;
  color: #fff;
  cursor: default;
}

.action-btn.in-progress {
  background: #2196f3;
  color: #fff;
  cursor: default;
}

.action-btn.completed {
  background: #4caf50;
  color: #fff;
  cursor: default;
}

.action-btn.default {
  background: #bdbdbd;
  color: #fff;
  cursor: default;
}

.action-btn:disabled {
  opacity: 0.7;
}

.order-detail {
  background: #fffbe6;
  border-top: 1px solid #ffe082;
  padding: 0.7em 1.5em;
  font-size: 1em;
  color: #333;
}

.order-detail-item {
  margin-bottom: 0.3em;
}

.empty-tip {
  color: #aaa;
  font-size: 0.95em;
  margin: 1em 0;
  text-align: center;
}

.pagination-bar {
  margin-top: 1.5em;
  display: flex;
  align-items: center;
  gap: 1em;
  flex-wrap: wrap;
  font-size: 1em;
}

.expand-enter-active, .expand-leave-active {
  transition: all 0.3s;
}

.expand-enter-from, .expand-leave-to {
  max-height: 0;
  opacity: 0;
  padding: 0 1.5em;
}

.expand-enter-to, .expand-leave-from {
  max-height: 200px;
  opacity: 1;
  padding: 0.7em 1.5em;
}
</style>