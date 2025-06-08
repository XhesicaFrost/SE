<template>
  <TopNav :navInfo="navInfo" />
  <div class="user-shopinfo">
    <!-- 店铺信息栏 -->
    <div class="shop-info-bar">
      <img :src="getImageUrl(shopInfo.image)" class="shop-img" />
      <div class="shop-info-center">
        <div class="shop-name">{{ shopInfo.name }}</div>
        <div class="shop-address">{{ shopInfo.address }}</div>
        <div class="shop-info">
          <div><i class="fas fa-star"></i>评分 {{ shopInfo.rating }}</div>
        </div>
      </div>
    </div>
    
    <!-- 商品列表 -->
    <div class="goods-list">
      <div v-for="product in products" :key="product.id" class="goods-item">
        <div class="product-img" @click="showProductDetail(product)">
          <img :src="getImageUrl(product.image)" alt="商品图片" class="goods-img" />
        </div>
        <div class="goods-info">
          <div class="goods-name">{{ product.name }}</div>
          <div class="goods-desc">{{ product.description }}</div>
          <div class="goods-bottom">
            <div class="goods-price">¥{{ product.price }}</div>
            <div class="quantity-control" v-if="getCartItemQuantity(product.id) > 0">
              <div class="quantity-btn quantity-minus" @click="decreaseQuantity(product)">
                -
              </div>
              <div class="quantity-num">{{ getCartItemQuantity(product.id) }}</div>
              <div class="quantity-btn" @click="addToCart(product)">
                +
              </div>
            </div>
            <div class="quick-add" v-else @click="addToCart(product)">
              +
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- 购物车底部栏 -->
    <div class="cart-bar">
      <div class="cart-icon">
        <div class="cart-badge" v-if="totalQuantity > 0">{{ totalQuantity }}</div>
      </div>
      <div class="cart-info" :style="{ marginLeft: totalQuantity > 0 ? '30px' : '0' }">
        <div class="cart-total">¥{{ totalPrice.toFixed(2) }}</div>
      </div>
      <div class="checkout-btn" :class="{ active: totalQuantity > 0 }" @click="toPayment(totalQuantity)">
        去结算
      </div>
    </div>
    
    <!-- 商品详情弹窗 -->
    <div class="detail-modal" v-if="currentProduct">
      <div class="detail-content">
        <div class="detail-close" @click="currentProduct = null">
          ×
        </div>
        <div class="detail-img">
          <img :src="getImageUrl(currentProduct.image)" alt="商品图片" class="goods-img" />
        </div>
        <div class="goods-info">
          <div class="goods-name">{{ currentProduct.name }}</div>
          <div class="goods-desc">{{ currentProduct.description }}</div>
          <div class="goods-price">¥{{ currentProduct.price }}</div>
          <div class="detail-controls">
            <div class="detail-quantity">
              <div 
                class="quantity-btn quantity-minus" 
                @click="decreaseQuantity(currentProduct)"
                :class="{ disabled: getCartItemQuantity(currentProduct.id) <= 0 }"
              >
                -
              </div>
              <div class="quantity-num">
                {{ getCartItemQuantity(currentProduct.id) }}
              </div>
              <div class="quantity-btn" @click="addToCart(currentProduct)">
                +
              </div>
            </div>
            <div class="add-to-cart-btn" @click="addToCart(currentProduct)">
              {{ getCartItemQuantity(currentProduct.id) > 0 ? '继续添加' : '加入购物车' }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import { BASE_URL, fetchWithTimeout } from '@/config.js'
import TopNav from '@/components/topNav.vue'

export default {
  props: ['shopId'],
  name: 'UserShopInfo',
  components: { TopNav },
  data() {
    return {
      shopInfo: {
        id: 0,
        image: '',
        name: '',
        address: '',
        rating: 0,
        monthlySales: 0,
        deliveryTime: ''
      },
      currentProduct: null,
      cart: [],
      products: [
      ],
      navInfo: { 
        title: '店铺详情', 
        pageReturn: () => { this.$router.go(-1) } 
      }
    }
  },
  computed: {
    ...mapState('userStore', ['userId']),
    totalQuantity() {
      return Object.values(this.cart).reduce((total, item) => total + item.quantity, 0)
    },
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
    async submitCartItems(shopId, productId, change) {
      try {
        const userId = this.$store.state.userStore.userInfo.userId;
        if (!userId) {
          console.error('用户未登录');
          this.$router.push('/login');
          return;
        }

        const formData = new FormData();
        formData.append('shopId', shopId);
        formData.append('productId', productId);
        if(change) formData.append('change', change);

        const response = await fetchWithTimeout(`${BASE_URL}/edit/shopcart?userId=${userId}`, {
          method: 'POST',
          body: formData
        });

        const result = await response.json();
        if (result.success) {
          this.fetchCartItems();
        } else {
          alert('购物车修改失败，请重试！');
        }
      } catch (error) {
        console.error('购物车修改失败:', error);
        alert('购物车修改失败，请检查网络连接！');
      }
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
    async fetchproducts() {
      try {
        const params = new URLSearchParams({ shopId: this.$route.params.shopId }).toString()
        const response = await fetchWithTimeout(`${BASE_URL}/items?${params}`)
        const result = await response.json()
        if (result.code === 200 && Array.isArray(result.data)) {
          this.products = result.data.filter(item => item.status === '正常')
        }
      } catch (e) {
        console.error('获取分类信息失败', e)
      }
    },
    getCartItemQuantity(productId) {
      console.log(this.cart.length)
      const cartItem = this.cart.find(item => item.product.id === productId);
      return cartItem ? cartItem.quantity : 0;
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
    showProductDetail(product) {
      this.currentProduct = product
    },
    addToCart(product) {
      this.submitCartItems(this.shopInfo.id, product.id, 1)
      this.fetchCartItems()
    },
    decreaseQuantity(product) {
      this.submitCartItems(this.shopInfo.id, product.id, -1)
      this.fetchCartItems()
    },
    toPayment(check) {
      if(check <= 0) return
      this.$router.push(`user/payment`)
    }
  },
  mounted() {
    this.fetchShopInfo()
    this.fetchproducts()
    this.fetchCartItems()
  }
}
</script>

<style scoped>
.user-shopinfo {
  max-width: 400px;
  margin: 48px auto 36px auto;
  padding: 1em;
  background: #fff;
  min-height: 100vh;
}
.shop-info-bar {
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
.shop-info-center {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.shop-name {
  font-size: 1.2em;
  font-weight: bold;
  margin-bottom: 0.3em;
}
.shop-address {
  color: #888;
  font-size: 0.95em;
  margin-bottom: 0.3em;
}
.shop-info {
  display: flex;
  gap: 1em;
  font-size: 0.85em;
  color: #666;
}
.goods-list {
  display: flex;
  flex-direction: column;
  gap: 0.7em;
  margin-bottom: 3em;
}
.goods-item {
  display: flex;
  align-items: center;
  background: #f9f9f9;
  border-radius: 6px;
  padding: 0.7em 1em;
  box-shadow: 0 1px 3px rgba(0,0,0,0.03);
}
.product-img {
  width: 65px;
  aspect-ratio: 1;
  overflow: hidden;
}
.goods-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.goods-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.goods-name {
  font-weight: bold;
  font-size: 1.05em;
  margin-bottom: 0.2em;
}
.goods-desc {
  color: #888;
  font-size: 0.9em;
  margin-bottom: 0.5em;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.goods-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.goods-price {
  color: #1249d5;
  font-weight: bold;
}
.quick-add {
  background: #1249d5;
  color: white;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.9em;
  cursor: pointer;
}
.quantity-control {
  display: flex;
  align-items: center;
  gap: 0.3em;
}
.quantity-btn {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: #1249d5;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.9em;
  cursor: pointer;
}
.quantity-minus {
  background: #ccc;
  color: #111;
}
.quantity-minus.disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
.quantity-num {
  min-width: 26px;
  text-align: center;
  font-weight: bold;
}
.cart-bar {
  display: flex;
  align-items: center;
  background: #1249d5;
  height: 56px;
  position: fixed;
  left: 50%;
  bottom: 0;
  transform: translateX(-50%);
  width: 100%;
  max-width: 400px;
  box-sizing: border-box;
  padding: 0 15px;
  z-index: 100;
}
.cart-icon {
  position: absolute;
  width: 30px;
  height: 30px;
  display: flex;
  place-items: center;
}
.cart-badge {
  background: #fff;
  color: #1249d5;
  height: 18px;
  aspect-ratio: 1;
  border-radius: 50%;
  font-size: 0.75em;
  text-align: center;
}
.cart-info {
  flex: 1;
  transition: margin-left 0.3s ease;
}
.cart-total {
  font-size: 1.1em;
  font-weight: bold;
  color: #fff;
}
.checkout-btn {
  background: #bbb;
  color: #333;
  padding: 8px 20px;
  border-radius: 20px;
  font-weight: bold;
  font-size: 0.95em;
  white-space: nowrap;
}
.checkout-btn.active {
  background: #aaddff;
  cursor: pointer;
}
.detail-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 200;
}
.detail-content {
  background: white;
  border-radius: 16px;
  width: 90%;
  max-width: 400px;
  overflow: hidden;
  position: relative;
}
.detail-close {
  position: absolute;
  top: 15px;
  right: 15px;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: rgba(0,0,0,0.5);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10;
  cursor: pointer;
}
.detail-img {
  width: 100%;
  height: 180px;
  background: #f5f5f5;
  overflow: hidden;
}
.detail-controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 1em;
}
.detail-quantity {
  display: flex;
  align-items: center;
  gap: 0.5em;
}
.add-to-cart-btn {
  background: #1249d5;
  color: white;
  padding: 8px 20px;
  border-radius: 20px;
  font-weight: bold;
  font-size: 0.95em;
  cursor: pointer;
}
</style>