<template>
  <div class="user-shopcart">
    <div class="cart-header">
      <h2>我的购物车</h2>
      <div class="cart-summary">
        共<span class="total-count">{{ totalItems }}</span>件商品
      </div>
    </div>
    
    <!-- 空购物车提示 -->
    <div class="empty-cart" v-if="Object.keys(groupedCartItems).length === 0">
      <img src="" alt="空购物车" class="empty-img">
      <p>购物车空空如也</p>
      <button class="shop-btn" @click="$router.push('/user/search')">去逛逛</button>
    </div>
    
    <!-- 按店铺分组的购物车商品 -->
    <div class="shop-group" v-for="(group, index) in groupedCartItems" :key="index">
      <div class="shop-header">
        <img :src="group.shop.image" alt="店铺图片" class="shop-img">
        <div class="shop-info">
          <h3>{{ group.shop.name }}</h3>
          <p class="shop-address">{{ group.shop.address }}</p>
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
                <button class="quantity-btn" 
                  @click="updateQuantity(index, item, -1)">-</button>
                <span class="quantity">{{ item.quantity }}</span>
                <button class="quantity-btn" @click="updateQuantity(index, item, 1)">+</button>
              </div>
            </div>
          </div>
          <!-- 目前弃用
          <button class="delete-btn" @click="removeItem(index, item.product.id)">
            <i class="fas fa-trash-alt"></i>
          </button>
          -->
        </div>
      </div>
      
      <div class="shop-footer">
        <div class="shop-total">
          店铺合计: <span class="total-price">¥{{ calculateShopTotal(group.items).toFixed(2) }}</span>
        </div>
        <button class="checkout-btn" @click="goToCheckout(group.shop.id)">进店结算</button>
      </div>
    </div>
    
    <BottomNav :navItems="navItems" />
  </div>
</template>

<script>
import BottomNav from '@/components/bottomNav.vue'
import { BASE_URL, fetchWithTimeout } from '@/config.js'

export default {
  name: 'userShopCart',
  components: { BottomNav },
  data() {
    return {
      navItems: [
        { label: '首页', action: () => { this.$router.push('/user') } },
        { label: '搜索', action: () => { this.$router.push('/user/search') } },
        { label: '购物车', action: () => { this.$router.push('/user/shopcart') }, isActive: true },
        { label: '我的', action: () => { this.$router.push('/user/personal') } }
      ],
      groupedCartItems: [
      ]
    }
  },
  computed: {
    totalItems() {
      return Object.values(this.groupedCartItems).reduce((total, group) => {
        return total + group.items.reduce((sum, item) => sum + item.quantity, 0)
      }, 0)
    }
  },
  methods: {
    // 获取购物车数据
    async fetchCartItems() {
      try {
        const params = new URLSearchParams({ userId: this.$store.state.userStore.userId }).toString()
        const response = await fetchWithTimeout(`${BASE_URL}/shopcart?${params}`)
        const result = await response.json()
        if (result.code === 200 && Array.isArray(result.data)) {
          this.groupedCartItems = result.data;
        } else {
          this.groupedCartItems = [];
        }
      } catch (error) {
        this.groupedCartItems = [];
        console.error('获取购物车数据失败:', error)
      }
    },
    async submitCartItems(shopId, productId, change) {
      try {
        const formData = new FormData()
        formData.append('shopId', shopId)
        formData.append('productId', productId)
        if(change) formData.append('change', change)

        const params = new URLSearchParams({ userId: this.$store.state.userStore.userId }).toString()
        const response = await fetchWithTimeout(`${BASE_URL}/edit/shopcart?${params}`, {
          method: 'POST',
          body: formData
        })

        const result = await response.json()
        if (result.success) {
          alert('购物车修改成功！')
          this.$router.go(-1)
        } else {
          alert('购物车修改失败，请重试！')
        }
      } catch (error) {
        console.error('购物车修改失败:', error)
        alert('购物车修改失败，请检查网络连接！')
      }
    },
    
    // 更新商品数量
    updateQuantity(shopId, item, change) {
      this.submitCartItems(shopId, item.product.id, change);
      this.fetchCartItems();
    },
    
    // 计算店铺总价
    calculateShopTotal(items) {
      return items.reduce((total, item) => {
        return total + (item.product.price * item.quantity)
      }, 0)
    },
    
    // 跳转到结算页面
    goToCheckout(shopId) {
      // 实际项目中应传递选中的商品信息
      this.$router.push(`/user/shopping/${shopId}`);
    }
  },
  mounted() {
    this.fetchCartItems()
  }
}
</script>

<style scoped>
.user-shopcart {
  max-width: 400px;
  margin: 0 auto 36px auto;
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
}

.quantity-btn {
  width: 28px;
  height: 28px;
  background: #f5f5f5;
  border: none;
  font-size: 1.1em;
  color: #666;
  cursor: pointer;
}

.quantity-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.quantity {
  min-width: 30px;
  text-align: center;
  font-weight: bold;
}

.delete-btn {
  background: none;
  border: none;
  color: #999;
  font-size: 1.1em;
  margin-left: 10px;
  cursor: pointer;
}

.delete-btn:hover {
  color: #ff4d4f;
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

.checkout-btn {
  background: #1249d5;
  color: white;
  border: none;
  padding: 8px 20px;
  border-radius: 15px;
  font-size: 0.95em;
  cursor: pointer;
  white-space: nowrap;
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

.cart-footer .checkout-btn {
  padding: 10px 25px;
  border-radius: 20px;
  font-weight: bold;
}

.cart-footer .checkout-btn.disabled {
  background: #ccc;
  cursor: not-allowed;
}
</style>