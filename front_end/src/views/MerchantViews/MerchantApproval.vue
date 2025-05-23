<template>
  <div class="merchant-approval">
    <!-- 店铺信息修改审批列表 -->
    <div class="approval-section">
      <div class="section-title">店铺信息修改</div>
      <div v-if="shopApprovals.length === 0" class="empty-tip">暂无审批事项</div>
      <div class="approval-list">
        <div class="approval-item" v-for="item in shopApprovals" :key="item.id">
          <img :src="getImageUrl(item.image)" alt="店铺图片" class="approval-img" />
          <div class="approval-info">
            <div class="approval-title">{{ item.name }}</div>
            <div class="approval-desc">{{ item.address }}</div>
          </div>
          <div
            class="approval-status"
            :class="statusClass(item.status)"
          >{{ item.status }}</div>
        </div>
      </div>
    </div>

    <!-- 商品信息修改审批列表 -->
    <div class="approval-section">
      <div class="section-title">商品信息修改</div>
      <div v-if="itemApprovals.length === 0" class="empty-tip">暂无审批事项</div>
      <div class="approval-list">
        <div class="approval-item" v-for="item in itemApprovals" :key="item.id">
          <img :src="getImageUrl(item.image)" alt="商品图片" class="approval-img" />
          <div class="approval-info">
            <div class="approval-title">{{ item.name }}</div>
            <div class="approval-desc">单价：￥{{ item.price }}</div>
          </div>
          <div
            class="approval-status"
            :class="statusClass(item.status)"
          >{{ item.status }}</div>
        </div>
      </div>
    </div>

    <!-- 底部导航栏 -->
    <BottomNav :navItems="navItems" />
  </div>
</template>

<script>
import BottomNav from '@/components/bottomNav.vue'
import { BASE_URL } from '@/config.js'
import { mapState } from 'vuex'

export default {
  name: 'MerchantApproval',
  components: { BottomNav },
  data() {
    return {
      shopApprovals: [],
      itemApprovals: [],
      navItems: [
        { label: '返回店铺管理', action: () => { this.$router.push('/merchant/shop') } }
      ]
    }
  },
  computed: {
    ...mapState('merchantStore', ['merchantId'])
  },
  methods: {
    statusClass(status) {
      if (status === '审批中') return 'status-pending'
      if (status === '否决') return 'status-reject'
      if (status === '通过') return 'status-pass'
      return ''
    },
    getImageUrl(img) {
      // 支持base64或url
      if (!img) return ''
      if (img.startsWith('data:image')) return img
      if (img.length > 100) return `data:image/png;base64,${img}`
      return img
    },
    // 获取店铺审批数据
    async fetchShopApprovals() {
      try {
        const params = new URLSearchParams({ merchantId: this.merchantId }).toString()
        const response = await fetch(`${BASE_URL}/approval/shop?${params}`)
        const result = await response.json()
        if (result.success && Array.isArray(result.data)) {
          this.shopApprovals = result.data
        } else {
          this.shopApprovals = []
        }
      } catch (e) {
        this.shopApprovals = []
      }
    },
    // 获取商品审批数据
    async fetchItemApprovals() {
      try {
        const params = new URLSearchParams({ merchantId: this.merchantId }).toString()
        const response = await fetch(`${BASE_URL}/approval/item?${params}`)
        const result = await response.json()
        if (result.success && Array.isArray(result.data)) {
          this.itemApprovals = result.data
        } else {
          this.itemApprovals = []
        }
      } catch (e) {
        this.itemApprovals = []
      }
    }
  },
  mounted() {
    this.fetchShopApprovals()
    this.fetchItemApprovals()
  }
}
</script>

<style scoped>
.merchant-approval {
  max-width: 400px;
  margin: 0 auto 70px auto;
  padding: 1em;
  background: #fff;
  min-height: 100vh;
}
.section-title {
  font-size: 1.15em;
  font-weight: bold;
  margin: 1.2em 0 0.5em 0;
  color: #333;
}
.approval-list {
  display: flex;
  flex-direction: column;
  gap: 0.7em;
}
.approval-item {
  display: flex;
  align-items: center;
  background: #f9f9f9;
  border-radius: 6px;
  padding: 0.7em 1em;
  box-shadow: 0 1px 3px rgba(0,0,0,0.03);
}
.approval-img {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 6px;
  margin-right: 1em;
  background: #eee;
}
.approval-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.approval-title {
  font-weight: bold;
  font-size: 1.05em;
  margin-bottom: 0.2em;
}
.approval-desc {
  color: #888;
  font-size: 0.95em;
}
.approval-status {
  padding: 0.4em 1.2em;
  border-radius: 12px;
  font-size: 1em;
  font-weight: bold;
  text-align: center;
  min-width: 60px;
}
.status-pending {
  background: #fffbe6;
  color: #ff9800;
  border: 1px solid #ffe082;
}
.status-reject {
  background: #ffebee;
  color: #e53935;
  border: 1px solid #ffcdd2;
}
.status-pass {
  background: #e8f5e9;
  color: #43a047;
  border: 1px solid #a5d6a7;
}
.empty-tip {
  color: #aaa;
  font-size: 0.95em;
  margin-bottom: 1em;
  margin-left: 0.5em;
}
</style>