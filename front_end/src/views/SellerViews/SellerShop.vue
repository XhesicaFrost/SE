<template>
  <TopNav :navInfo="navInfo" />
  <div class="seller-shop">
    <!-- 店铺信息栏 -->
    <div class="shop-info-bar">
      <img :src="shopInfo.image" alt="店铺图片" class="shop-img" />
      <div class="shop-info-center">
        <div class="shop-name">{{ shopInfo.name }}</div>
        <div class="shop-address">{{ shopInfo.address }}</div>
      </div>
      <button class="edit-btn" @click="editShop">编辑</button>
    </div>

    <!-- 商品排序和列表 -->
    <div class="goods-sort-bar">
      <label>排序：</label>
      <select v-model="sortType" @change="sortGoods">
        <option value="name">名称</option>
        <option value="sales">销量</option>
        <option value="price">价格</option>
      </select>
    </div>
    <div class="goods-list">
      <div class="goods-item" v-for="item in pagedGoods" :key="item.id">
        <img :src="item.image" alt="商品图片" class="goods-img" />
        <div class="goods-info">
          <div class="goods-name">{{ item.name }}</div>
          <div class="goods-detail">
            <span class="goods-price">￥{{ item.price }}</span>
            <span class="goods-sales">销量：{{ item.sales }}</span>
            <!--显示商品状态 -->
            <span class="goods-status" :class="getStatusClass(item.status)">
              {{ getStatusText(item.status) }}
            </span>
          </div>
        </div>
        <div class="goods-actions">
          <button class="edit-btn" @click="editItem(item.id)">编辑</button>
          <!--根据状态动态显示上架/下架按钮 -->
          <button 
            :class="item.status === '正常' ? 'offline-btn' : 'online-btn'"
            @click="toggleItemStatus(item)"
            :disabled="item.isUpdating"
          >
            {{ item.isUpdating ? '处理中...' : (item.status === '正常' ? '下架' : '上架') }}
          </button>
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
import { mapState } from 'vuex'
import { BASE_URL, fetchWithTimeout } from '@/config.js'
import BottomNav from '@/components/bottomNav.vue'
import TopNav from '@/components/topNav.vue'

export default {
  name: 'sellerShop',
  components: { BottomNav, TopNav },
  data() {
    return {
      shopInfo: {
        image: '',
        name: '',
        address: ''
      },
      goods: [],
      sortType: 'name',
      page: 1,
      pageSize: 10,
      jumpPage: 1,
      navItems: [],
      navInfo: { title: '管理店铺', pageReturn: () => { this.$router.push('/seller') } }

    }
  },
  computed: {
    ...mapState('sellerStore', ['sellerId']),
    sortedGoods() {
      let arr = [...this.goods]
      if (this.sortType === 'name') {
        arr.sort((a, b) => a.name.localeCompare(b.name))
      } else if (this.sortType === 'sales') {
        arr.sort((a, b) => b.sales - a.sales)
      } else if (this.sortType === 'price') {
        arr.sort((a, b) => a.price - b.price)
      }
      return arr
    },
    pagedGoods() {
      const start = (this.page - 1) * this.pageSize
      return this.sortedGoods.slice(start, start + this.pageSize)
    },
    totalPages() {
      return Math.ceil(this.goods.length / this.pageSize) || 1
    }
  },
  methods: {
    /**
     * 编辑店铺信息
     */
    editShop() {
      this.$router.push('/seller/shop/edit')
    },
    /**
     * 编辑商品信息
     * @param {number} id - 商品ID
     */
    editItem(id) {
      this.$router.push(`/seller/item/${id}`)
    },
    /**
     * 商品排序
     */
    sortGoods() {
      this.page = 1
    },
    /**
     * 切换分页
     * @param {number} p - 页码
     */
    changePage(p) {
      if (p < 1) p = 1
      if (p > this.totalPages) p = this.totalPages
      this.page = p
      this.jumpPage = p
    },
    /**
     * 跳转到新增商品页面
     */
    goToAddItem() {
      this.$router.push('/seller/item/register')
    },
    /**
     * 跳转到促销活动管理页面
     */
    goToPromotion() {
      this.$router.push('/seller/promotion')
    },
    /**
     * 跳转到审批信息查看页面
     */
    goToApproval() {
      this.$router.push('/seller/approval')
    },
    /**
     * 获取店铺信息
     */
    async fetchShopInfo() {
      try {
        const params = new URLSearchParams({ sellerId: this.sellerId }).toString()
        const response = await fetchWithTimeout(`${BASE_URL}/seller/shop?${params}`)
        const result = await response.json()
        if (result.code === 200) {
          this.shopInfo = {
            name: result.data.shopName,
            // ✅ 处理base64图片数据
            image: result.data.shopImg ? `data:image/jpeg;base64,${result.data.shopImg}` : '',
            address: result.data.shopAddress
          }
        } else {
          this.shopInfo = {
            name: '获取失败',
            image: '',
            address: ''
          }
        }
      } catch (e) {
        this.shopInfo = {
          name: '获取失败',
          image: '',
          address: ''
        }
      }
    },
    /**
     * 获取商品列表
     */
    async fetchGoods() {
      try {
        const params = new URLSearchParams({ sellerId: this.sellerId }).toString()
        const response = await fetchWithTimeout(`${BASE_URL}/item?${params}`)
        const result = await response.json()
        if (result.code === 200 && Array.isArray(result.data)) {
          // ✅ 确保每个商品都有status字段，并处理base64图片
          this.goods = result.data.map(item => ({
            ...item,
            // ✅ 处理base64图片数据
            image: item.image ? `data:image/jpeg;base64,${item.image}` : '',
            status: item.status || '正常', // 默认为正常状态
            isUpdating: false // 添加更新状态标志
          }))
        } else {
          this.goods = []
        }
      } catch (e) {
        console.error('获取商品列表失败:', e)
        this.goods = []
      }
    },
    /**
     * 获取状态显示文本
     * @param {string} status - 商品状态
     * @returns {string} 显示文本
     */
    getStatusText(status) {
      switch (status) {
        case '正常':
          return '正常'
        case '下架':
          return '已下架'
        case '审批中':
          return '审批中'
        default:
          return '未知'
      }
    },
    /**
     * 获取状态样式类名
     * @param {string} status - 商品状态
     * @returns {string} CSS类名
     */
    getStatusClass(status) {
      switch (status) {
        case '正常':
          return 'status-online'
        case '下架':
          return 'status-offline'
        case '审批中':
          return 'status-pending'
        default:
          return 'status-unknown'
      }
    },
    /**
     * 切换商品上架/下架状态
     * @param {Object} item - 商品对象
     */
    async toggleItemStatus(item) {
      const isOnline = item.status === '正常'
      const action = isOnline ? '下架' : '上架'
      
      // 如果商品状态为"审批中"，不允许上架
      if (!isOnline && item.status === '审批中') {
        alert('该商品正在审批中，暂时不能上架')
        return
      }
      
      if (!confirm(`确定要${action}商品"${item.name}"吗？`)) {
        return
      }
      
      item.isUpdating = true
      
      try {
        if (isOnline) {
          // 下架商品
          await this.offlineItem(item.id)
        } else {
          // 上架商品
          await this.onlineItem(item.id)
        }
        
        await this.fetchGoods()
        
        this.$toast && this.$toast(`${action}成功`)
      } catch (error) {
        console.error(`${action}失败:`, error)
        this.$toast && this.$toast(`${action}失败，请重试`)
      } finally {
        item.isUpdating = false
      }
    },
    /**
     * 下架商品
     * @param {string|number} itemId - 商品ID
     * @returns {Promise} 请求Promise
     */
    async offlineItem(itemId) {
      console.log('执行下架商品:', itemId)
      
      const response = await fetchWithTimeout(`${BASE_URL}/seller/item/offline`, {
        method: 'POST',
        headers: { 
          'Content-Type': 'application/json' 
        },
        body: JSON.stringify({ 
          itemId: itemId,
          sellerId: this.sellerId 
        })
      })
      
      const result = await response.json()
      
      if (result.code !== 200) {
        throw new Error(result.message || '下架失败')
      }
      
      console.log('下架成功:', result)
      return result
    },
    /**
     * 上架商品
     * @param {string|number} itemId - 商品ID  
     * @returns {Promise} 请求Promise
     */
    async onlineItem(itemId) {
      console.log('执行上架商品:', itemId)
      
      const response = await fetchWithTimeout(`${BASE_URL}/seller/item/online`, {
        method: 'POST',
        headers: { 
          'Content-Type': 'application/json' 
        },
        body: JSON.stringify({ 
          itemId: itemId,
          sellerId: this.sellerId 
        })
      })
      
      const result = await response.json()
      
      if (result.code !== 200) {
        throw new Error(result.message || '上架失败')
      }
      
      console.log('上架成功:', result)
      return result
    }
  },
  mounted() {
    this.fetchShopInfo()
    this.fetchGoods()
    this.navItems = [
      { label: '增加商品', action: this.goToAddItem },
      { label: '管理促销活动', action: this.goToPromotion },
      { label: '查看审批', action: this.goToApproval }
    ]
  }
}
</script>

<style scoped>
.seller-shop {
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
}
.edit-btn {
  background: #4caf50;
  color: #fff;
  border: none;
  border-radius: 6px;
  padding: 0.5em 1.2em;
  font-size: 1em;
  cursor: pointer;
  margin-left: 1em;
  transition: background 0.2s;
}
.edit-btn:hover {
  background: #388e3c;
}
.goods-sort-bar {
  margin-bottom: 1em;
  display: flex;
  align-items: center;
  gap: 0.5em;
}
.goods-list {
  display: flex;
  flex-direction: column;
  gap: 0.7em;
}
.goods-item {
  display: flex;
  align-items: center;
  background: #f9f9f9;
  border-radius: 6px;
  padding: 0.7em 1em;
  box-shadow: 0 1px 3px rgba(0,0,0,0.03);
}
.goods-img {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 6px;
  margin-right: 1em;
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
.goods-detail {
  color: #888;
  font-size: 0.95em;
  display: flex;
  flex-wrap: wrap;
  gap: 0.8em;
  align-items: center;
}
.pagination-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 1.2em 0 2.5em 0;
  font-size: 1em;
  gap: 0.5em;
}
.pagination-bar input[type="number"] {
  width: 50px;
  padding: 0.2em;
  border-radius: 4px;
  border: 1px solid #bfbfbf;
  margin: 0 0.3em;
}
.goods-actions {
  display: flex;
  flex-direction: column;
  gap: 0.5em;
  margin-left: 1em;
}
.delete-btn {
  background: #e53935;
  color: #fff;
  border: none;
  border-radius: 6px;
  padding: 0.4em 1.2em;
  font-size: 1em;
  cursor: pointer;
  margin-top: 0.2em;
  transition: background 0.2s;
}
.delete-btn:hover {
  background: #b71c1c;
}
.goods-status {
  font-size: 0.85em;
  padding: 0.2em 0.5em;
  border-radius: 4px;
  font-weight: bold;
}
.status-online {
  color: #4caf50;
}
.status-offline {
  color: #f44336;
}
.status-pending {
  color: #ff9800;
}
.status-unknown {
  color: #9e9e9e;
}
.online-btn {
  background: #4caf50;
  color: #fff;
  border: none;
  border-radius: 6px;
  padding: 0.4em 1.2em;
  font-size: 1em;
  cursor: pointer;
  margin-top: 0.2em;
  transition: background 0.2s;
}
.online-btn:hover {
  background: #388e3c;
}
.online-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}
.offline-btn {
  background: #ff9800;
  color: #fff;
  border: none;
  border-radius: 6px;
  padding: 0.4em 1.2em;
  font-size: 1em;
  cursor: pointer;
  margin-top: 0.2em;
  transition: background 0.2s;
}
.offline-btn:hover {
  background: #f57c00;
}
.offline-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}
</style>