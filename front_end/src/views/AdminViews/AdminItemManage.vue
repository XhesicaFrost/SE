<template>
  <TopNav :navInfo="navInfo" />
  <div class="admin-item">
    <!-- 搜索和筛选 -->
    <div class="filter-bar">
      <input 
        type="text" 
        v-model="searchQuery" 
        placeholder="搜索商品名称..." 
        class="search-input"
      />
      <select v-model="shopFilter" class="shop-select">
        <option value="">所有店铺</option>
        <option v-for="shop in shops" :key="shop.id" :value="shop.id">
          {{ shop.name }}
        </option>
      </select>
    </div>
    
    <!-- 商品列表 -->
    <div class="item-list">
      <div 
        class="item-card" 
        v-for="item in filteredItems" 
        :key="item.id"
      >
        <img :src="item.image" alt="商品图片" class="item-img" />
        <div class="item-info">
          <div class="item-name">{{ item.name }}</div>
          <div class="item-shop">店铺: {{ getShopName(item.shopId) }}</div>
          <div class="item-price">¥{{ item.price.toFixed(2) }}</div>
          <div class="item-status">
            状态: 
            <span :class="item.isActive ? 'active-status' : 'inactive-status'">
              {{ item.isActive ? '上架' : '下架' }}
            </span>
          </div>
        </div>
        <div class="item-actions">
          <button class="edit-btn" @click="editItem(item.id)">编辑</button>
          <button 
            class="toggle-btn" 
            :class="{ 'disable-btn': !item.isActive }"
            @click="toggleItemStatus(item.id, item.isActive)"
          >
            {{ item.isActive ? '下架' : '上架' }}
          </button>
        </div>
      </div>
      
      <div v-if="filteredItems.length === 0" class="empty-tip">
        没有找到符合条件的商品
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
  name: 'AdminItem',
  components: { BottomNav, TopNav },
  data() {
    return {
      items: [],
      shops: [],
      searchQuery: '',
      shopFilter: '',
      navItems: [
        { label: '店铺管理', action: () => { this.$router.push('/admin/shops') } },
        { label: '商品管理', action: () => { this.$router.push('/admin/items') }, isActive: true },
        { label: '订单管理', action: () => { this.$router.push('/admin/orders') } }
      ],
      navInfo: { title: '商品管理', noReturn: true }
    }
  },
  computed: {
    filteredItems() {
      return this.items.filter(item => {
        const matchesSearch = item.name.toLowerCase().includes(this.searchQuery.toLowerCase())
        const matchesShop = this.shopFilter ? item.shopId == this.shopFilter : true
        return matchesSearch && matchesShop
      })
    }
  },
  methods: {
    async fetchItems() {
      try {
        const response = await fetchWithTimeout(`${BASE_URL}/admin/items`)
        const result = await response.json()
        if (result.code === 200 && Array.isArray(result.data)) {
          this.items = result.data
        } else {
          this.items = []
        }
      } catch (e) {
        console.error('获取商品列表失败:', e)
        this.items = []
      }
    },
    async fetchShops() {
      try {
        const response = await fetchWithTimeout(`${BASE_URL}/admin/shops`)
        const result = await response.json()
        if (result.code === 200 && Array.isArray(result.data)) {
          this.shops = result.data
        } else {
          this.shops = []
        }
      } catch (e) {
        console.error('获取店铺列表失败:', e)
        this.shops = []
      }
    },
    getShopName(shopId) {
      const shop = this.shops.find(s => s.id == shopId)
      return shop ? shop.name : '未知店铺'
    },
    editItem(itemId) {
      this.$router.push(`/admin/item/edit/${itemId}`)
    },
    async toggleItemStatus(itemId, currentStatus) {
      try {
        const formData = new FormData()
        formData.append('itemId', itemId)
        formData.append('status', currentStatus ? 'disable' : 'enable')
        
        const response = await fetchWithTimeout(`${BASE_URL}/admin/item/status`, {
          method: 'POST',
          body: formData
        })
        
        const result = await response.json()
        if (result.code === 200) {
          this.fetchItems()
        } else {
          alert('操作失败: ' + (result.message || '未知错误'))
        }
      } catch (e) {
        alert('网络错误，操作失败')
      }
    }
  },
  mounted() {
    this.fetchItems()
    this.fetchShops()
  }
}
</script>

<style scoped>
.admin-item {
  max-width: 500px;
  margin: 48px auto 36px auto;
  padding: 1em;
  background: #fff;
  min-height: 100vh;
}

.filter-bar {
  display: flex;
  gap: 1em;
  margin-bottom: 1.2em;
}

.search-input, .shop-select {
  flex: 1;
  padding: 0.6em;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 0.95em;
}

.item-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 1.2em;
}

.item-card {
  background: #f9f9f9;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 5px rgba(0,0,0,0.05);
  transition: all 0.2s;
}

.item-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.08);
}

.item-img {
  width: 100%;
  height: 140px;
  object-fit: cover;
}

.item-info {
  padding: 0.8em;
}

.item-name {
  font-weight: bold;
  margin-bottom: 0.3em;
  font-size: 1.05em;
}

.item-shop, .item-price, .item-status {
  font-size: 0.9em;
  color: #555;
  margin-bottom: 0.2em;
}

.item-price {
  color: #e53935;
  font-weight: bold;
}

.active-status {
  color: #4caf50;
}

.inactive-status {
  color: #f44336;
}

.item-actions {
  display: flex;
  padding: 0.5em 0.8em 0.8em;
  gap: 0.5em;
}

.edit-btn, .toggle-btn {
  flex: 1;
  padding: 0.5em;
  border: none;
  border-radius: 4px;
  font-size: 0.9em;
  cursor: pointer;
}

.edit-btn {
  background: #4caf50;
  color: white;
}

.edit-btn:hover {
  background: #388e3c;
}

.toggle-btn {
  background: #2196f3;
  color: white;
}

.toggle-btn:hover {
  background: #1976d2;
}

.disable-btn {
  background: #ff9800;
}

.disable-btn:hover {
  background: #f57c00;
}

.empty-tip {
  grid-column: 1 / -1;
  text-align: center;
  color: #888;
  padding: 2em 0;
  font-size: 1.1em;
}
</style>