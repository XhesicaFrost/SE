<template>
  <TopNav :navInfo="navInfo" />
  <div class="admin-shop">
    <!-- 店铺列表 -->
    <div class="shop-list">
      <div 
        class="shop-item" 
        v-for="shop in pagedShops" 
        :key="shop.id"
        @click="viewShop(shop.id)"
      >
        <img :src="getImageUrl(shop.image)" alt="店铺图片" class="shop-img" />
        <div class="shop-info">
          <div class="shop-name">{{ shop.name }}</div>
          <div class="shop-address">{{ shop.address }}</div>
          <div class="shop-status">状态: {{ shop.status }}</div>
        </div>
        <div class="shop-actions">
          <button class="edit-btn" @click.stop="editShop(shop.id)">编辑</button>
          <button 
            class="toggle-btn" 
            :class="{ 'disable-btn': shop.status === '封禁中' || shop.status === '审批中' }"
            @click.stop="toggleShopStatus(shop.id, shop.status)"
          >
            {{ shop.status === '审批中' || shop.status === '封禁中' ? '启用' : '禁用' }}
          </button>
          <button class="review-btn" @click.stop="reviewComments(shop.id)">审核评论</button>
        </div>
      </div>
    </div>

    <!-- 分页栏 -->
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

    <!-- 底部导航栏 -->
    <BottomNav :navItems="navItems" />
  </div>
</template>

<script>
import BottomNav from '@/components/bottomNav.vue'
import TopNav from '@/components/topNav.vue'
import { BASE_URL, fetchWithTimeout } from '@/config.js'

export default {
  name: 'AdminShop',
  components: { BottomNav, TopNav },
  data() {
    return {
      shops: [],
      page: 1,
      pageSize: 8,
      jumpPage: 1,
      navItems: [
        { label: '店铺管理', action: () => { this.$router.push('/admin/shops') }, isActive: true },
        { label: '商品管理', action: () => { this.$router.push('/admin/items') } },
        { label: '订单管理', action: () => { this.$router.push('/admin/orders') } }
      ],
      navInfo: { title: '店铺管理', noReturn: true }
    }
  },
  computed: {
    pagedShops() {
      const start = (this.page - 1) * this.pageSize
      return this.shops.slice(start, start + this.pageSize)
    },
    totalPages() {
      return Math.ceil(this.shops.length / this.pageSize) || 1
    }
  },
  methods: {
    getImageUrl(img) {
      if (!img) return ''
      if (img.startsWith('data:image')) return img
      if (img.length > 100) return `data:image/png;base64,${img}`
      return img
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
    viewShop(shopId) {
      this.$router.push(`/admin/shop/${shopId}`)
    },
    editShop(shopId) {
      this.$router.push(`/admin/shop/edit/${shopId}`)
    },
    async toggleShopStatus(shopId, currentStatus) {
      const newStatus = currentStatus === '正常' ? 'disable' : 'enable'
      
      try {
        const response = await fetchWithTimeout(`${BASE_URL}/admin/shop/status`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ shopId, status: newStatus })
        })
        
        const result = await response.json()
        if (result.code === 200) {
          this.fetchShops()
        } else {
          alert('操作失败: ' + (result.message || '未知错误'))
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
    reviewComments(shopId) {
      this.$router.push(`/admin/shop/comments/${shopId}`)
    }
  },
  mounted() {
    this.fetchShops()
  }
}
</script>

<style scoped>
.admin-shop {
  max-width: 500px;
  margin: 48px auto 36px auto;
  padding: 1em;
  background: #fff;
  min-height: 100vh;
}

.shop-list {
  display: flex;
  flex-direction: column;
  gap: 1em;
}

.shop-item {
  display: flex;
  align-items: center;
  background: #f9f9f9;
  border-radius: 8px;
  padding: 1em;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  cursor: pointer;
  transition: all 0.2s;
}

.shop-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 3px 8px rgba(0,0,0,0.08);
}

.shop-img {
  width: 70px;
  height: 70px;
  object-fit: cover;
  border-radius: 8px;
  margin-right: 1em;
}

.shop-info {
  flex: 1;
}

.shop-name {
  font-weight: bold;
  font-size: 1.1em;
  margin-bottom: 0.3em;
}

.shop-address, .shop-status {
  color: #666;
  font-size: 0.9em;
  margin-bottom: 0.2em;
}

.shop-actions {
  display: flex;
  flex-direction: column;
  gap: 0.5em;
}

.edit-btn, .toggle-btn {
  padding: 0.4em 0.8em;
  border: none;
  border-radius: 4px;
  font-size: 0.9em;
  cursor: pointer;
  transition: background 0.2s;
}

.edit-btn {
  background: #4caf50;
  color: white;
}

.edit-btn:hover {
  background: #388e3c;
}

.toggle-btn {
  background: #f44336;  /* 默认红色（禁用） */
  color: white;
}

.toggle-btn:hover {
  background: #d32f2f;
}

.disable-btn {
  background: #2196f3;  /* 蓝色（启用） */
}

.disable-btn:hover {
  background: #1976d2;
}

.pagination-bar {
  margin-top: 1.5em;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.8em;
  flex-wrap: wrap;
  font-size: 0.95em;
}

.pagination-bar button {
  padding: 0.4em 0.8em;
  background: #f0f0f0;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
}

.pagination-bar button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination-bar input {
  width: 50px;
  padding: 0.3em;
  border: 1px solid #ddd;
  border-radius: 4px;
  text-align: center;
}

.review-btn {
  background: #ff9800;
  color: white;
}

.review-btn:hover {
  background: #f57c00;
}
</style>