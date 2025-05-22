<template>
  <div class="merchant-order-view">
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
          </div>
          <button
            class="serve-btn"
            @click.stop="serveOrder(order.id)"
            :disabled="order.served"
          >
            {{ order.served ? '已出餐' : '出餐' }}
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
              {{ item.name }}：{{ item.count }}：￥{{ (item.price * item.count).toFixed(2) }}
            </div>
          </div>
        </transition>
      </div>
      <div v-if="pagedOrders.length === 0" class="empty-tip">暂无未处理订单</div>
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
import { BASE_URL, fetchWithTimeout } from '@/config.js'
import { mapState } from 'vuex'

export default {
  name: 'MerchantOrder',
  components: { BottomNav },
  data() {
    return {
      orders: [],
      page: 1,
      pageSize: 10,
      jumpPage: 1,
      navItems: [
        { label: '返回商家主页', action: () => { this.$router.push('/merchant') } }
      ],
      expandedOrderId: null
    }
  },
  computed: {
    ...mapState('merchantStore', ['merchantId']),
    sortedOrders() {
      // 未出餐的订单排前面，已出餐的排后面，同组内按订单编号降序
      return [...this.orders].sort((a, b) => {
        if (a.served === b.served) {
          return b.id - a.id
        }
        return a.served ? 1 : -1
      })
    },
    pagedOrders() {
      const start = (this.page - 1) * this.pageSize
      return this.sortedOrders.slice(start, start + this.pageSize)
    },
    totalPages() {
      return Math.ceil(this.orders.length / this.pageSize) || 1
    }
  },
  methods: {
    async fetchOrders() {
      try {
        const params = new URLSearchParams({ merchantId: this.merchantId }).toString()
        const response = await fetchWithTimeout(`${BASE_URL}/merchant/order?${params}`)
        const result = await response.json()
        if (result.success && Array.isArray(result.data)) {
          this.orders = result.data
        } else {
          this.orders = []
        }
      } catch (e) {
        this.orders = []
      }
    },
    async serveOrder(orderId) {
      try {
        const response = await fetchWithTimeout(`${BASE_URL}/merchant/order/serve`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ orderId })
        })
        const result = await response.json()
        if (result.success) {
          this.fetchOrders()
        } else {
          alert('操作失败')
        }
      } catch (e) {
        alert('网络错误，操作失败')
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
  mounted() {
    this.fetchOrders()
  }
}
</script>

<style scoped>
.merchant-order-view {
  max-width: 500px;
  margin: 0 auto 70px auto;
  padding: 1em;
  background: #fff;
  min-height: 100vh;
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
}
.serve-btn {
  background: #e53935;
  color: #fff;
  border: none;
  border-radius: 6px;
  padding: 0.5em 1.2em;
  font-size: 1em;
  cursor: pointer;
  transition: background 0.2s;
}
.serve-btn:disabled {
  background: #bdbdbd;
  cursor: not-allowed;
}
.serve-btn:hover:not(:disabled) {
  background: #b71c1c;
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