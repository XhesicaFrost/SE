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
          </div>
        </div>
        <div class="goods-actions">
          <button class="edit-btn" @click="editItem(item.id)">编辑</button>
          <button class="delete-btn" @click="deleteItem(item.id)">删除</button>
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
    editShop() {
      this.$router.push('/seller/shop/edit')
    },
    editItem(id) {
      this.$router.push(`/seller/item/${id}`)
    },
    async deleteItem(id) {
      if (!confirm('确定要删除该商品吗？')) return
      try {
        const response = await fetchWithTimeout(`${BASE_URL}/seller/item`, {
          method: 'DELETE',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ id })
        })
        const result = await response.json()
        if (result.code === 200) {
          this.fetchGoods()
        } else {
          alert('删除失败')
        }
      } catch (e) {
        alert('网络错误，删除失败')
      }
    },
    sortGoods() {
      this.page = 1
    },
    changePage(p) {
      if (p < 1) p = 1
      if (p > this.totalPages) p = this.totalPages
      this.page = p
      this.jumpPage = p
    },
    goToAddItem() {
      this.$router.push('/seller/item/register')
    },
    goToPromotion() {
      this.$router.push('/seller/promotion')
    },
    goToApproval() {
      this.$router.push('/seller/approval')
    },
    async fetchShopInfo() {
      try {
        const params = new URLSearchParams({ sellerId: this.sellerId }).toString()
        const response = await fetchWithTimeout(`${BASE_URL}/shop?${params}`)
        const result = await response.json()
        if (result.code === 200) {
          this.shopInfo = {
            name: result.data.shopName,
            image: result.data.shopImg,
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
    async fetchGoods() {
      try {
        const params = new URLSearchParams({ sellerId: this.sellerId }).toString()
        const response = await fetchWithTimeout(`${BASE_URL}/item?${params}`)
        const result = await response.json()
        if ( result.code === 200 && Array.isArray(result.data)) {
          this.goods = result.data
        } else {
          this.goods = []
        }
      } catch (e) {
        this.goods = []
      }
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
  gap: 1.2em;
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
</style>