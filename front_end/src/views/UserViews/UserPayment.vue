<template>
  <TopNav :navInfo="navInfo" />
  <div class="user-payment">
    <!-- 商家信息 -->
    <div class="shop-info">
      <img :src="getImageUrl(shopInfo.image)" class="shop-img" />
      <div class="shop-details">
        <div class="shop-name">{{ shopInfo.name }}</div>
        <div class="shop-address">{{ shopInfo.address }}</div>
      </div>
    </div>

    <!-- 商品信息 -->
    <div class="product-list">
      <div v-for="item in cart" :key="item.product.id" class="product-item">
        <div class="product-name">{{ item.product.name }}</div>
        <div class="product-quantity">数量: {{ item.quantity }}</div>
        <div class="product-price">¥{{ (item.product.price * item.quantity).toFixed(2) }}</div>
      </div>
    </div>

    <!-- 总价格 -->
    <div class="total-price">
      总价格: ¥{{ totalPrice.toFixed(2) }}
    </div>

    <!-- 付款按钮 -->
    <button class="payment-btn" @click="handlePayment">付款</button>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import { BASE_URL, fetchWithTimeout } from '@/config.js'
import TopNav from '@/components/topNav.vue'

export default {
  props: ['shopId'],
  name: 'UserPayment',
  components: { TopNav },
  data() {
    return {
      navInfo: { 
        title: '结算', 
        pageReturn: () => { this.$router.go(-1) } 
      },
      shopInfo: {
        image: '',
        name: '',
        address: '',
        rating: 0,
        monthlySales: 0,
        deliveryTime: ''
      },
      cart: [
      ]
    }
  },
  computed: {
    ...mapState('userStore', ['userId']),
    totalPrice() {
      return Object.values(this.cart).reduce((total, item) => 
        total + (item.product.price * item.quantity), 0)
    }
  },
  methods: {
    getImageUrl(img) {
      // 支持base64或url
      if (!img) return ''
      if (img.startsWith('data:image')) return img
      if (img.length > 100) return `data:image/png;base64,${img}`
      return img
    },
    async fetchShopInfo() {
      try {
        const params = new URLSearchParams({ shopId: this.$route.params.shopId }).toString()
        const response = await fetchWithTimeout(`${BASE_URL}/shop?${params}`)
        const result = await response.json()
        if (result.code === 200) {
          this.shopInfo = {
            name: result.data.shopName,
            image: result.data.shopImg,
            address: result.data.shopAddress,
            rating: result.data.rating,
            monthlySales: result.data.monthlySales,
            deliveryTime: result.data.deliveryTime
          }
        }
      } catch (e) {
        console.error('获取店铺信息失败', e)
      }
    },
    async fetchCartItems() {
      try {
        const userId = this.$store.state.userStore.userInfo.userId;
        if (!userId) {
          console.error('用户ID未获取到');
          this.cart = [];
          return;
        }

        const response = await fetchWithTimeout(`${BASE_URL}/cart_items/user/shopcart?userId=${userId}`);
        const result = await response.json();
        console.log('获取到的购物车数据:', result);
        
        if (result.code === 200 && Array.isArray(result.data)) {
          // 找到当前店铺的购物车数据
          const shopCart = result.data.find(shopCart => 
            String(this.$route.params.shopId) === String(shopCart.shop.id)
          );
          console.log('当前店铺ID:', this.$route.params.shopId);
          console.log('找到的店铺购物车:', shopCart);
          this.cart = shopCart ? shopCart.items : [];
          console.log('处理后的购物车数据:', this.cart);
        } else {
          this.cart = [];
        }
      } catch (error) {
        this.cart = [];
        console.error('获取购物车数据失败:', error);
      }
    },
    async handlePayment() {
      try {
        const response = await fetchWithTimeout(`${BASE_URL}/payment`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            userId: this.$store.state.userStore.userId,
            shopId: this.$router.params.shopId,
            items: this.cart,
          }),
        })
        const result = await response.json()
        if (result.code === 200) {
          alert('支付成功！')
          this.$router.push('/user/history')
        } else {
          alert('支付失败，请重试！')
        }
      } catch (error) {
        console.error('支付失败:', error)
        alert('支付失败，请重试！')
      }
    },
  },
  mounted() {
    this.fetchShopInfo()
    this.fetchCartItems()
  },
}
</script>

<style scoped>
.user-payment {
  max-width: 400px;
  margin: 48px auto;
  padding: 1em;
  background: #fff;
  min-height: 100vh;
}
.shop-info {
  display: flex;
  align-items: center;
  margin-bottom: 1.2em;
  background: #f8f8f8;
  padding: 0.7em 1em;
  border-radius: 8px;
}
.shop-img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
  margin-right: 1em;
}
.shop-details {
  flex: 1;
}
.shop-name {
  font-size: 1.2em;
  font-weight: bold;
}
.shop-address {
  color: #888;
  font-size: 0.95em;
}
.product-list {
  margin-bottom: 1.5em;
}
.product-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.8em;
}
.product-name {
  font-weight: bold;
}
.product-quantity,
.product-price {
  color: #666;
}
.total-price {
  font-size: 1.2em;
  font-weight: bold;
  margin-bottom: 1.5em;
}
.payment-btn {
  background: #1249d5;
  color: white;
  padding: 10px 20px;
  border-radius: 20px;
  font-weight: bold;
  cursor: pointer;
  width: 100%;
  text-align: center;
}
</style>