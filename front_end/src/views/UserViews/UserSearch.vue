<template>
  <div class="user-search">
    <TopNav :navInfo="navInfo" v-model="searchKeyword" @search="performSearch"/>

    <div class="filter-section">
      <div 
        class="filter-item" 
        :class="{ active: activeFilter === 'rating' }"
        @click="setFilter('rating')"
      >
        <i class="fas fa-star filter-icon"></i>
        <span class="filter-text">评分</span>
      </div>
      <div 
        class="filter-item" 
        :class="{ active: activeFilter === 'distance' }"
        @click="setFilter('distance')"
      >
        <i class="fas fa-location-arrow filter-icon"></i>
        <span class="filter-text">距离</span>
      </div>
      <div 
        class="filter-item" 
        :class="{ active: activeFilter === 'price' }"
        @click="setFilter('price')"
      >
        <i class="fas fa-tag filter-icon"></i>
        <span class="filter-text">价格</span>
      </div>
      <div 
        class="filter-item" 
        :class="{ active: activeFilter === 'delivery' }"
        @click="setFilter('delivery')"
      >
        <i class="fas fa-clock filter-icon"></i>
        <span class="filter-text">配送</span>
      </div>
    </div>
    
    <!-- 搜索结果 -->
    <div class="search-results">
      <h3 class="result-title" v-if="searchResults.length">
        找到<strong> {{ searchResults.length }} </strong>家相关店铺
      </h3>
      
      <div class="loading" v-if="isLoading">
        <div class="spinner"></div>
        <p>正在搜索中...</p>
      </div>
      
      <div class="no-results" v-else-if="!searchResults.length && !isLoading">
        <i class="fas fa-search"></i>
        <p>没有找到相关店铺</p>
        <p>请尝试其他关键词</p>
      </div>
      
      <!-- 店铺卡片 -->
      <div 
        class="shop-card" 
        v-for="shop in sortedResults" 
        :key="shop.id"
        @click="viewShop(shop.id)"
      >
        <img :src="shop.image" alt="店铺图片" class="shop-img">
        <div class="shop-info">
          <div>
            <div class="shop-header">
              <div class="shop-name">{{ shop.name }}</div>
              <div class="rating">
                <i class="fas fa-star"></i>
                <span>{{ shop.rating }}</span>
              </div>
            </div>
            <div class="shop-tags">
              <div class="tag" v-for="(tag, index) in shop.tags" :key="index">
                {{ tag }}
              </div>
            </div>
          </div>
          
          <div class="shop-footer">
            <div class="price">¥{{ shop.avgPrice }}/人</div>
            <div class="distance">{{ shop.distance }}km</div>
            <div class="delivery-time">
              <i class="fas fa-bicycle"></i>
              <span>{{ shop.deliveryTime }}分钟</span>
            </div>
          </div>
          
          <div class="products-preview">
            <img 
              v-for="(product, idx) in shop.products" 
              :key="idx" 
              :src="product.image" 
              alt="商品" 
              class="product-img"
            >
          </div>
        </div>
      </div>
    </div>
    
    <!-- 底部导航 -->
    <BottomNav :navItems="navItems" />
  </div>
</template>

<script>
import BottomNav from '@/components/bottomNav.vue'
import TopNav from '@/components/topNav.vue'

export default {
  name: 'UserSearch',
  components: { BottomNav, TopNav },
  data() {
    return {
      searchKeyword: '',
      currentAddress: 'position',
      activeFilter: 'rating',
      isLoading: false,
      searchResults: [],
      navInfo: { search: true, noReturn: true },
      navItems: [
        { label: '首页', action: () => this.$router.push('/user') },
        { label: '搜索', action: () => this.$router.push('/user/search'), isActive: true },
        { label: '购物车', action: () => this.$router.push('/user/shopcart') },
        { label: '我的', action: () => this.$router.push('/user/personal') }
      ],
      // 模拟店铺数据
      allShops: [
        {
          id: 1,
          name: '1',
          image: '',
          rating: 4.8,
          avgPrice: 25,
          distance: 1.2,
          deliveryTime: 25,
          tags: ['网红奶茶', '新品上市', '满30减5'],
          products: [
            { image: '' },
            { image: '' },
            { image: '' }
          ]
        },
        {
          id: 2,
          name: '2',
          image: '',
          rating: 4.6,
          avgPrice: 18,
          distance: 0.8,
          deliveryTime: 20,
          tags: ['经典奶茶', '买一送一'],
          products: [
            { image: '' },
            { image: '' },
            { image: '' }
          ]
        },
        {
          id: 3,
          name: '3',
          image: '',
          rating: 4.9,
          avgPrice: 35,
          distance: 2.1,
          deliveryTime: 35,
          tags: ['欧包奶茶', '高端品质', '新店开业'],
          products: [
            { image: '' },
            { image: '' },
            { image: '' }
          ]
        }
      ]
    }
  },
  computed: {
    sortedResults() {
      // 根据当前激活的筛选条件对结果进行排序
      return [...this.searchResults].sort((a, b) => {
        switch (this.activeFilter) {
          case 'rating':
            return b.rating - a.rating;
          case 'distance':
            return a.distance - b.distance;
          case 'price':
            return a.avgPrice - b.avgPrice;
          case 'delivery':
            return a.deliveryTime - b.deliveryTime;
          default:
            return 0;
        }
      });
    }
  },
  methods: {
    setFilter(filterType) {
      this.activeFilter = filterType;
    },
    selectAddress() {
      // 这里可以添加地址选择逻辑
      alert('地址选择功能待实现');
    },
    performSearch() {
      if (!this.searchKeyword.trim()) return;
      
      this.isLoading = true;
      console.log(this.searchKeyword);
      this.searchResults = [];
      
      // 模拟API请求延迟
      setTimeout(() => {
        // 实际项目中这里应该是API调用
        this.searchResults = this.allShops.filter(shop => 
          shop.name.toLowerCase().includes(this.searchKeyword.toLowerCase()) ||
          shop.tags.some(tag => tag.toLowerCase().includes(this.searchKeyword.toLowerCase()))
        );
        
        this.isLoading = false;
      }, 800);
    },
    viewShop(shopId) {
      this.$router.push(`/user/shopping/${shopId}`);
    }
  },
  mounted() {
    // 页面加载时执行一次搜索
    // this.searchKeyword = '奶茶';
    // this.performSearch();
  }
}
</script>

<style scoped>
.user-search {
  max-width: 400px;
  margin: 48px auto 36px auto;
  padding: 1em;
  background: #fff;
  min-height: 100vh;
}

.back-btn {
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #333;
  font-size: 18px;
  cursor: pointer;
}

.address-selector {
  flex: 1;
  margin: 0 10px;
  background: white;
  border-radius: 15px;
  padding: 5px 12px;
  display: flex;
  align-items: center;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  cursor: pointer;
}

.address-selector i {
  margin-right: 5px;
  color: #666;
}

.address-text {
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.search-container {
  flex: 3;
  position: relative;
}

.search-box {
  width: 100%;
  padding: 8px 15px 8px 35px;
  border-radius: 20px;
  border: none;
  outline: none;
  font-size: 14px;
  background: white;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.search-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #999;
}

/* 筛选区域 */
.filter-section {
  padding: 10px 15px;
  background: white;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
}

.filter-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
}

.filter-icon {
  width: 24px;
  height: 24px;
  margin-bottom: 3px;
  color: #666;
}

.filter-text {
  font-size: 12px;
  color: #666;
}

.filter-item.active .filter-text {
  color: #ff8c00;
  font-weight: bold;
}

.filter-item.active .filter-icon {
  color: #ff8c00;
}

/* 搜索结果区域 */
.search-results {
  padding: 10px 15px;
}

.result-title {
  margin: 10px 0 15px;
  font-size: 16px;
  color: #666;
}

.result-title strong {
  color: #333;
}

.shop-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 15px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  display: flex;
  transition: transform 0.3s ease;
  cursor: pointer;
}

.shop-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.shop-img {
  width: 90px;
  height: 90px;
  object-fit: cover;
}

.shop-info {
  flex: 1;
  padding: 10px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.shop-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.shop-name {
  font-weight: bold;
  font-size: 16px;
  margin-bottom: 5px;
}

.rating {
  display: flex;
  align-items: center;
  color: #ff8c00;
  font-size: 13px;
  margin-bottom: 5px;
}

.rating span {
  margin-left: 3px;
}

.shop-tags {
  display: flex;
  flex-wrap: wrap;
  margin-bottom: 8px;
}

.tag {
  background: #fff8e1;
  color: #ff8c00;
  padding: 2px 6px;
  border-radius: 3px;
  font-size: 11px;
  margin-right: 5px;
  margin-bottom: 3px;
}

.shop-footer {
  display: flex;
  justify-content: space-between;
  color: #666;
  font-size: 12px;
}

.price {
  color: #ff5722;
}

.delivery-time {
  display: flex;
  align-items: center;
}

.delivery-time i {
  margin-right: 3px;
}

.products-preview {
  display: flex;
  margin-top: 8px;
  overflow-x: auto;
  padding-bottom: 5px;
}

.product-img {
  width: 50px;
  height: 50px;
  object-fit: cover;
  border-radius: 4px;
  margin-right: 8px;
  border: 1px solid #f0f0f0;
}

/* 无结果状态 */
.no-results {
  text-align: center;
  padding: 50px 20px;
  color: #999;
}

.no-results i {
  font-size: 60px;
  margin-bottom: 20px;
  color: #e0e0e0;
}

.no-results p {
  font-size: 16px;
  margin-bottom: 10px;
}

/* 加载状态 */
.loading {
  text-align: center;
  padding: 30px 0;
}

.loading .spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #f0f0f0;
  border-top: 3px solid #ffd700;
  border-radius: 50%;
  margin: 0 auto 15px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>