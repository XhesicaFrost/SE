<template>
  <TopNav :navInfo="navInfo" />
  <div class="user-shopcart">
    <div class="cart-header">
      <h2>我的历史订单</h2>
      <div class="cart-summary">
        共<span class="total-count">{{ totalOrders }}</span>个订单
      </div>
    </div>
    
    <!-- 空历史记录提示 -->
    <div class="empty-cart" v-if="Object.keys(groupedOrderItems).length === 0">
      <img src="" alt="空历史记录" class="empty-img">
      <p>暂无历史订单</p>
      <button class="shop-btn" @click="$router.push('/user/search')">去逛逛</button>
    </div>
    
    <!-- 按店铺分组的历史订单 -->
    <div class="shop-group" v-for="(group, index) in groupedOrderItems" :key="index">
      <div class="shop-header">
        <img :src="group.shop.image" alt="店铺图片" class="shop-img">
        <div class="shop-info">
          <h3>{{ group.shop.name }}</h3>
          <p class="shop-address">{{ group.shop.address }}</p>
        </div>
        <div class="arrive-info" v-if="group.state === 'arrived'">
          <span class="arrive-text">订单已送达</span>
        </div>
        <div class="arrive-info" v-if="group.state !== 'arrived'">
          <span class="arrive-text">订单处理中</span>
        </div>
      </div>
      <div class="cart-items">
        <div class="cart-item" v-for="item in group.items" :key="item.product.id">
          <img :src="item.product.image" alt="商品图片" class="item-img">
          <div class="item-info">
            <div class="item-name">{{ item.product.name }}</div>
            <div class="item-desc">{{ item.product.description }}</div>
            <div class="item-bottom">
              <div class="item-price">¥{{ item.product.price.toFixed(2) }}</div>
              <div class="item-quantity">
                数量: <span class="quantity">{{ item.quantity }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="shop-footer">
        <div class="shop-total">
          店铺合计: <span class="total-price">¥{{ calculateShopTotal(group.items).toFixed(2) }}</span>
        </div>
      </div>
    </div>
    
  </div>
</template>

<script>
import { BASE_URL, fetchWithTimeout } from '@/config.js'
import TopNav from '@/components/topNav.vue'

export default {
  name: 'userHistory',
  components: { TopNav },
  data() {
    return {
      navInfo: { 
        title: '历史订单', 
        pageReturn: () => { this.$router.go(-1) } 
      },
      groupedOrderItems: [
      ]
    }
  },
  computed: {
    totalOrders() {
      return this.groupedOrderItems.length
    }
  },
  methods: {
    // 获取历史订单数据
    async fetchOrderItems() {
      try {
        const params = new URLSearchParams({ userId: this.$store.state.userStore.userId }).toString()
        const response = await fetchWithTimeout(`${BASE_URL}/history?${params}`)
        const result = await response.json()
        if (result.code === 200 && Array.isArray(result.data)) {
          this.groupedOrderItems = result.data;
        } else {
          this.groupedOrderItems = [];
        }
      } catch (error) {
        this.groupedOrderItems = [];
        console.error('获取历史订单数据失败:', error)
      }
    },
    
    // 计算店铺总价
    calculateShopTotal(items) {
      return items.reduce((total, item) => {
        return total + (item.product.price * item.quantity)
      }, 0)
    }
  },
  mounted() {
    this.fetchOrderItems()
  }
}
</script>

<style scoped>
.user-shopcart {
  max-width: 400px;
  margin: 48px auto 0 auto;
  padding: 1em;
  background: #fff;
  min-height: 100vh;
}

.cart-header {
  padding: 15px 0;
  border-bottom: 1px solid #eee;
  margin-bottom: 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.cart-header h2 {
  margin: 0;
  font-size: 1.3em;
  color: #333;
}

.cart-summary {
  font-size: 0.9em;
  color: #666;
}

.total-count {
  color: #ff6a00;
  font-weight: bold;
  margin: 0 3px;
}

.empty-cart {
  text-align: center;
  padding: 50px 0;
}

.empty-img {
  width: 120px;
  height: 120px;
  opacity: 0.6;
  margin-bottom: 20px;
}

.empty-cart p {
  color: #999;
  margin-bottom: 20px;
}

.shop-btn {
  background: #1249d5;
  color: white;
  border: none;
  padding: 10px 30px;
  border-radius: 20px;
  font-size: 1em;
  cursor: pointer;
}

.shop-group {
  background: #f8f8f8;
  border-radius: 10px;
  margin-bottom: 15px;
  overflow: hidden;
  box-shadow: 0 2px 5px rgba(0,0,0,0.05);
}

.shop-header {
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

.arrive-info {
  margin-left: 90px;
  right: 0;
  top: 0;
  background-color: #fff;
  font-size: 15px;
  color: #66ccff;
}

.cart-items {
  background: #fff;
}

.cart-item {
  display: flex;
  padding: 15px;
  border-bottom: 1px solid #f5f5f5;
  align-items: center;
}

.item-selector {
  margin-right: 10px;
}

.item-checkbox {
  width: 18px;
  height: 18px;
}

.item-img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
  margin-right: 15px;
}

.item-info {
  flex: 1;
}

.item-name {
  font-weight: bold;
  margin-bottom: 5px;
  color: #333;
}

.item-desc {
  font-size: 0.9em;
  color: #888;
  margin-bottom: 10px;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.item-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.item-price {
  color: #ff6a00;
  font-weight: bold;
  font-size: 1.1em;
}

.item-quantity {
  display: flex;
  align-items: center;
  border: 1px solid #ddd;
  border-radius: 15px;
  overflow: hidden;
  font-size: 14px;
  padding-left: 10px;
}

.quantity {
  min-width: 30px;
  text-align: center;
  font-weight: bold;
}

.shop-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 15px;
  background: #fff;
}

.shop-total {
  font-size: 1em;
}

.total-price {
  color: #ff6a00;
  font-weight: bold;
  font-size: 1.1em;
}

.cart-footer {
  position: fixed;
  bottom: 36px;
  left: 0;
  right: 0;
  max-width: 400px;
  margin: 0 auto;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 15px;
  box-shadow: 0 -2px 10px rgba(0,0,0,0.1);
  z-index: 100;
}

.select-all {
  display: flex;
  align-items: center;
  font-size: 0.95em;
}

.select-all-checkbox {
  margin-right: 8px;
  width: 18px;
  height: 18px;
}

.cart-total {
  font-size: 1em;
  margin-left: 10px;
}
</style>