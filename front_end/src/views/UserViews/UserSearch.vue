
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
  <title>美味餐厅 - 店铺详情</title>
  <script src="https://cdn.jsdelivr.net/npm/vue@3.2.31/dist/vue.global.js"></script>
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
  <style>
    * {
      margin: 0;
      padding: 0;
      box-sizing: border-box;
      -webkit-tap-highlight-color: transparent;
    }
    
    body {
      font-family: -apple-system, BlinkMacSystemFont, "PingFang SC", "Helvetica Neue", STHeiti, "Microsoft Yahei", Tahoma, Simsun, sans-serif;
      background-color: #f5f5f5;
      color: #333;
      font-size: 14px;
      line-height: 1.5;
      max-width: 500px;
      margin: 0 auto;
      position: relative;
      min-height: 100vh;
      padding-bottom: 60px;
    }
    
    .shop-header {
      background: linear-gradient(to bottom, #ffd444, #ffb000);
      padding: 15px;
      position: relative;
      border-radius: 0 0 16px 16px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.1);
      color: #333;
    }
    
    .shop-name {
      font-size: 22px;
      font-weight: bold;
      margin-bottom: 8px;
      display: flex;
      align-items: center;
    }
    
    .shop-name i {
      margin-left: 8px;
      font-size: 18px;
      color: #ff6b00;
    }
    
    .shop-info {
      display: flex;
      font-size: 13px;
      margin-bottom: 8px;
      color: #666;
    }
    
    .shop-info div {
      margin-right: 15px;
      display: flex;
      align-items: center;
    }
    
    .shop-info i {
      margin-right: 4px;
      color: #ff6b00;
    }
    
    .shop-tags {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
      margin-top: 10px;
    }
    
    .tag {
      background-color: rgba(255,255,255,0.8);
      border-radius: 12px;
      padding: 3px 10px;
      font-size: 12px;
      color: #666;
    }
    
    .promo-banner {
      background-color: #fff9e6;
      padding: 10px 15px;
      border-radius: 8px;
      margin-top: 15px;
      display: flex;
      align-items: center;
      font-size: 13px;
      box-shadow: 0 1px 3px rgba(0,0,0,0.05);
    }
    
    .promo-banner i {
      color: #ff6b00;
      margin-right: 8px;
      font-size: 16px;
    }
    
    .category-tabs {
      display: flex;
      background: white;
      padding: 0 15px;
      overflow-x: auto;
      position: sticky;
      top: 0;
      z-index: 10;
      box-shadow: 0 2px 4px rgba(0,0,0,0.05);
    }
    
    .category-tab {
      padding: 12px 10px;
      font-size: 14px;
      white-space: nowrap;
      position: relative;
    }
    
    .category-tab.active {
      color: #ff6b00;
      font-weight: bold;
    }
    
    .category-tab.active::after {
      content: '';
      position: absolute;
      bottom: 0;
      left: 50%;
      transform: translateX(-50%);
      width: 20px;
      height: 3px;
      background-color: #ff6b00;
      border-radius: 2px;
    }
    
    .product-list {
      padding: 15px;
    }
    
    .category-title {
      font-size: 16px;
      font-weight: bold;
      margin: 20px 0 10px 0;
      padding-left: 5px;
      border-left: 3px solid #ff6b00;
    }
    
    .product-item {
      display: flex;
      background: white;
      border-radius: 12px;
      padding: 12px;
      margin-bottom: 15px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.03);
      position: relative;
    }
    
    .product-img {
      width: 80px;
      height: 80px;
      border-radius: 8px;
      object-fit: cover;
      margin-right: 12px;
      background: #f5f5f5;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #ccc;
    }
    
    .product-info {
      flex: 1;
      padding-right: 10px;
    }
    
    .product-name {
      font-size: 16px;
      font-weight: bold;
      margin-bottom: 5px;
    }
    
    .product-desc {
      font-size: 12px;
      color: #999;
      margin-bottom: 8px;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }
    
    .product-bottom {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    
    .product-price {
      color: #ff6b00;
      font-size: 16px;
      font-weight: bold;
    }
    
    .product-price::before {
      content: '¥';
      font-size: 12px;
    }
    
    .quick-add {
      background: #ff6b00;
      color: white;
      width: 26px;
      height: 26px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 18px;
      box-shadow: 0 2px 4px rgba(255,107,0,0.3);
    }
    
    .quantity-control {
      display: flex;
      align-items: center;
    }
    
    .quantity-btn {
      width: 24px;
      height: 24px;
      border-radius: 50%;
      background: #ff6b00;
      color: white;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 16px;
    }
    
    .quantity-minus {
      background: #f5f5f5;
      color: #999;
    }
    
    .quantity-num {
      min-width: 26px;
      text-align: center;
      font-weight: bold;
    }
    
    .cart-bar {
      position: fixed;
      bottom: 0;
      left: 0;
      right: 0;
      background: white;
      display: flex;
      align-items: center;
      padding: 10px 15px;
      box-shadow: 0 -2px 10px rgba(0,0,0,0.1);
      max-width: 500px;
      margin: 0 auto;
    }
    
    .cart-icon {
      position: relative;
      margin-right: 12px;
    }
    
    .cart-badge {
      position: absolute;
      top: -5px;
      right: -5px;
      background: #ff6b00;
      color: white;
      width: 18px;
      height: 18px;
      border-radius: 50%;
      font-size: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
    }
    
    .cart-info {
      flex: 1;
    }
    
    .cart-total {
      font-size: 16px;
      font-weight: bold;
      color: #ff6b00;
    }
    
    .cart-total::before {
      content: '¥';
      font-size: 12px;
    }
    
    .cart-desc {
      font-size: 12px;
      color: #999;
    }
    
    .checkout-btn {
      background: #ffd444;
      color: #333;
      padding: 10px 25px;
      border-radius: 20px;
      font-weight: bold;
      font-size: 15px;
    }
    
    .checkout-btn.active {
      background: #ffb000;
    }
    
    /* 详情弹窗样式 */
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
      z-index: 100;
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
    }
    
    .detail-img {
      width: 100%;
      height: 180px;
      object-fit: cover;
      background: #f5f5f5;
    }
    
    .detail-info {
      padding: 20px;
    }
    
    .detail-name {
      font-size: 20px;
      font-weight: bold;
      margin-bottom: 8px;
    }
    
    .detail-desc {
      color: #666;
      margin-bottom: 15px;
      line-height: 1.6;
    }
    
    .detail-price {
      color: #ff6b00;
      font-size: 20px;
      font-weight: bold;
      margin-bottom: 20px;
    }
    
    .detail-controls {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    
    .detail-quantity {
      display: flex;
      align-items: center;
    }
    
    .detail-quantity .quantity-num {
      font-size: 18px;
      min-width: 36px;
    }
    
    .detail-quantity .quantity-btn {
      width: 32px;
      height: 32px;
      font-size: 18px;
    }
    
    .add-to-cart-btn {
      background: #ff6b00;
      color: white;
      padding: 10px 25px;
      border-radius: 20px;
      font-weight: bold;
      font-size: 16px;
    }
    
    /* 滚动条样式 */
    ::-webkit-scrollbar {
      width: 0;
      height: 0;
      background: transparent;
    }
  </style>
</head>
<body>
  <div id="app">
    <!-- 店铺头部信息 -->
    <div class="shop-header">
      <div class="shop-name">
        美味餐厅 <i class="fas fa-crown"></i>
      </div>
      <div class="shop-info">
        <div><i class="fas fa-star"></i> 4.8</div>
        <div><i class="fas fa-shopping-bag"></i> 月售1256</div>
        <div><i class="fas fa-clock"></i> 30分钟</div>
        <div><i class="fas fa-truck"></i> 配送费¥5</div>
      </div>
      <div class="shop-tags">
        <div class="tag">优惠多多</div>
        <div class="tag">满30减5</div>
        <div class="tag">新店优惠</div>
        <div class="tag">折扣商品</div>
      </div>
      <div class="promo-banner">
        <i class="fas fa-tag"></i>
        <div>新用户下单立减15元，满50元免配送费</div>
      </div>
    </div>
    
    <!-- 分类标签 -->
    <div class="category-tabs">
      <div 
        v-for="(category, index) in categories" 
        :key="index"
        class="category-tab"
        :class="{ active: activeCategory === index }"
        @click="activeCategory = index"
      >
        {{ category.name }}
      </div>
    </div>
    
    <!-- 商品列表 -->
    <div class="product-list">
      <div v-for="(category, index) in categories" :key="index">
        <h3 class="category-title">{{ category.name }}</h3>
        <div v-for="product in category.products" :key="product.id" class="product-item">
          <div class="product-img" @click="showProductDetail(product)">
            <i class="fas fa-image"></i>
          </div>
          <div class="product-info">
            <div class="product-name">{{ product.name }}</div>
            <div class="product-desc">{{ product.description }}</div>
            <div class="product-bottom">
              <div class="product-price">{{ product.price }}</div>
              <div class="quantity-control" v-if="cart[product.id] && cart[product.id].quantity > 0">
                <div class="quantity-btn quantity-minus" @click="decreaseQuantity(product)">
                  <i class="fas fa-minus"></i>
                </div>
                <div class="quantity-num">{{ cart[product.id].quantity }}</div>
                <div class="quantity-btn" @click="addToCart(product)">
                  <i class="fas fa-plus"></i>
                </div>
              </div>
              <div class="quick-add" v-else @click="addToCart(product)">
                <i class="fas fa-plus"></i>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 购物车底部栏 -->
    <div class="cart-bar">
      <div class="cart-icon">
        <i class="fas fa-shopping-cart" style="font-size: 24px;"></i>
        <div class="cart-badge" v-if="totalQuantity > 0">{{ totalQuantity }}</div>
      </div>
      <div class="cart-info">
        <div class="cart-total">{{ totalPrice.toFixed(2) }}</div>
        <div class="cart-desc">另需配送费¥5</div>
      </div>
      <div class="checkout-btn" :class="{ active: totalQuantity > 0 }">
        {{ totalQuantity > 0 ? '去结算' : '¥20起送' }}
      </div>
    </div>
    
    <!-- 商品详情弹窗 -->
    <div class="detail-modal" v-if="currentProduct">
      <div class="detail-content">
        <div class="detail-close" @click="currentProduct = null">
          <i class="fas fa-times"></i>
        </div>
        <div class="detail-img">
          <i class="fas fa-image" style="font-size: 50px; color: #ddd;"></i>
        </div>
        <div class="detail-info">
          <div class="detail-name">{{ currentProduct.name }}</div>
          <div class="detail-desc">{{ currentProduct.description }}</div>
          <div class="detail-price">¥{{ currentProduct.price }}</div>
          <div class="detail-controls">
            <div class="detail-quantity">
              <div 
                class="quantity-btn quantity-minus" 
                @click="decreaseQuantity(currentProduct)"
                :class="{ disabled: !cart[currentProduct.id] || cart[currentProduct.id].quantity <= 0 }"
              >
                <i class="fas fa-minus"></i>
              </div>
              <div class="quantity-num">
                {{ cart[currentProduct.id] ? cart[currentProduct.id].quantity : 0 }}
              </div>
              <div class="quantity-btn" @click="addToCart(currentProduct)">
                <i class="fas fa-plus"></i>
              </div>
            </div>
            <div class="add-to-cart-btn" @click="addToCart(currentProduct)">
              {{ cart[currentProduct.id] && cart[currentProduct.id].quantity > 0 ? '已添加' : '加入购物车' }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <script>
    const { createApp, ref, reactive, computed } = Vue
    
    createApp({
      setup() {
        // 当前选中的分类
        const activeCategory = ref(0)
        
        // 当前查看的商品详情
        const currentProduct = ref(null)
        
        // 购物车数据
        const cart = reactive({})
        
        // 分类和商品数据
        const categories = ref([
          {
            name: '热销推荐',
            products: [
              {
                id: 1,
                name: '香辣鸡腿堡',
                price: 18.9,
                description: '香辣可口，外酥里嫩，经典美味'
              },
              {
                id: 2,
                name: '奥尔良烤鸡腿堡',
                price: 19.9,
                description: '奥尔良风味，鲜嫩多汁'
              },
              {
                id: 3,
                name: '双层牛肉芝士堡',
                price: 24.9,
                description: '双层牛肉饼，搭配香浓芝士'
              }
            ]
          },
          {
            name: '超值套餐',
            products: [
              {
                id: 4,
                name: '香辣鸡腿堡套餐',
                price: 32.9,
                description: '香辣鸡腿堡+中薯条+中可乐'
              },
              {
                id: 5,
                name: '奥尔良烤鸡腿堡套餐',
                price: 34.9,
                description: '奥尔良烤鸡腿堡+中薯条+中可乐'
              },
              {
                id: 6,
                name: '双人分享套餐',
                price: 59.9,
                description: '2个汉堡+2份小吃+2杯中可乐'
              }
            ]
          },
          {
            name: '小吃配餐',
            products: [
              {
                id: 7,
                name: '香辣鸡翅(2对)',
                price: 12.9,
                description: '香辣酥脆，外焦里嫩'
              },
              {
                id: 8,
                name: '黄金鸡块(5块)',
                price: 10.9,
                description: '外酥里嫩，金黄诱人'
              },
              {
                id: 9,
                name: '薯条(大)',
                price: 11.9,
                description: '金黄酥脆，外酥里糯'
              }
            ]
          },
          {
            name: '饮料甜品',
            products: [
              {
                id: 10,
                name: '可乐(大)',
                price: 9.9,
                description: '冰爽可口，畅快淋漓'
              },
              {
                id: 11,
                name: '草莓圣代',
                price: 8.9,
                description: '香滑冰淇淋搭配草莓酱'
              },
              {
                id: 12,
                name: '巧克力新地',
                price: 8.9,
                description: '香滑冰淇淋搭配巧克力酱'
              }
            ]
          }
        ])
        
        // 显示商品详情
        const showProductDetail = (product) => {
          currentProduct.value = product
        }
        
        // 添加到购物车
        const addToCart = (product) => {
          if (!cart[product.id]) {
            cart[product.id] = {
              product,
              quantity: 0
            }
          }
          cart[product.id].quantity += 1
        }
        
        // 减少商品数量
        const decreaseQuantity = (product) => {
          if (cart[product.id] && cart[product.id].quantity > 0) {
            cart[product.id].quantity -= 1
          }
        }
        
        // 计算购物车总数量
        const totalQuantity = computed(() => {
          return Object.values(cart).reduce((total, item) => {
            return total + item.quantity
          }, 0)
        })
        
        // 计算购物车总价
        const totalPrice = computed(() => {
          return Object.values(cart).reduce((total, item) => {
            return total + (item.product.price * item.quantity)
          }, 0)
        })
        
        return {
          activeCategory,
          currentProduct,
          cart,
          categories,
          totalQuantity,
          totalPrice,
          showProductDetail,
          addToCart,
          decreaseQuantity
        }
      }
    }).mount('#app')
  </script>
</body>
</html>