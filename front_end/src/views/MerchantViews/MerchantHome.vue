<template>
  <div class="merchant-home">
    <!-- 顶部统计 -->
    <div class="top-stats">
      <div class="stat-item">
        <div class="stat-title">今日营销额</div>
        <div class="stat-value">{{ todayRevenue }} 元</div>
      </div>
      <div class="stat-item">
        <div class="stat-title">今日订单总数</div>
        <div class="stat-value">{{ todayOrderCount }} 单</div>
      </div>
    </div>

    <!-- 最新评论 -->
    <div class="comments-section">
      <div class="comments-title">最新评论</div>
      <div class="comments-list">
        <div class="comment-row" v-for="(comment, idx) in pagedComments" :key="idx">
          <div class="comment-username">{{ comment.username }}</div>
          <div class="comment-content">{{ comment.content }}</div>
        </div>
        <div v-if="pagedComments.length === 0" class="empty-tip">暂无评论</div>
      </div>
      <div class="pagination-bar" v-if="totalCommentPages > 1">
        <button :disabled="commentPage === 1" @click="commentPage--">上一页</button>
        <span>第 {{ commentPage }} 页 / 共 {{ totalCommentPages }} 页</span>
        <button :disabled="commentPage === totalCommentPages" @click="commentPage++">下一页</button>
      </div>
    </div>

    <!-- 底部导航栏 -->
    <BottomNav :navItems="navItems" />
  </div>
</template>

<script>
import { mapState, mapMutations } from 'vuex'
import { BASE_URL, debug_merchant_created, fetchWithTimeout } from '@/config.js'
import BottomNav from '@/components/bottomNav.vue'

export default {
  name: 'MerchantHome',
  components: { BottomNav },
  data() {
    return {
      todayRevenue: 0,
      todayOrderCount: 0,
      latestComments: [],
      navItems: [],
      commentPage: 1,
      commentPageSize: 10
    }
  },
  computed: {
    ...mapState('userStore', ['userInfo']),
    pagedComments() {
      const start = (this.commentPage - 1) * this.commentPageSize
      return this.latestComments.slice(start, start + this.commentPageSize)
    },
    totalCommentPages() {
      return Math.ceil(this.latestComments.length / this.commentPageSize) || 1
    }
  },
  methods: {
    ...mapMutations('merchantStore', ['SET_MERCHANT_ID', 'SET_MERCHANT_NAME']),
    goTo(type) {
        console.log('goTo', type)
      if (type === 'shop') {
        this.$router.push('/merchant/shop')
      } else if (type === 'order') {
        this.$router.push('/merchant/order')
      } else if (type === 'data') {
        this.$router.push('/merchant/data')
      } else if (type === 'register') {
        this.$router.push('/merchant/register')
      }
    },
    /**
     * fetchMerchantHomeData
     * 根据商家ID（merchantId）获取商家首页数据，包括今日营销额、今日订单总数和最新评论。
     * 调用 /merchantHome 接口，成功后更新页面数据。
     * @param {string} merchantId - 商家ID
     */
    async fetchMerchantHomeData(merchantId) {
      try {
        console.log('fetchMerchantHomeData')
        const params = new URLSearchParams({ merchantId: merchantId }).toString()
        const response = await fetchWithTimeout(`${BASE_URL}/merchantHome?${params}`)
        const result = await response.json()
        if (result.success && result.code === 200) {
          this.todayRevenue = result.data.todayRevenue
          this.todayOrderCount = result.data.todayOrderCount
          this.latestComments = result.data.latestComments
        } else {
          this.$toast && this.$toast(result.message || '数据获取失败')
        }
      } catch (error) {
        this.$toast && this.$toast('网络异常，数据获取失败')
      }
    },
    /**
     * fetchMerchantInfo
     * 进入页面时调用，通过当前用户ID请求 /userToMerchant 接口，获取商家ID、商家名称和商家状态，
     * 并保存到 merchantStore，随后根据 merchantStatus 决定导航栏内容和是否获取首页数据。
     * merchantStatus 可为“未注册/审批中/封禁中/正常”
     */
    async fetchMerchantInfo() {
      try {
        console.log('fetchMerchantInfo')
        const params = new URLSearchParams({ userId: this.userInfo.userId }).toString()
        const response = await fetchWithTimeout(`${BASE_URL}/userToMerchant?${params}`)
        const result = await response.json()
        if (result.success && result.code === 200) {
          this.SET_MERCHANT_ID(result.merchantId || '')
          this.SET_MERCHANT_NAME(result.merchantName || '')
          const status = result.merchantStatus
          if (status === '未注册') {
            // 未注册店铺，仅显示“创建店铺”
            this.navItems = [
              { label: '创建店铺', action: () => this.goTo('register') }
            ]
            this.todayRevenue = 0
            this.todayOrderCount = 0
            this.latestComments = []
          } else if (status === '审批中') {
            // 审批中，仅显示提示
            this.navItems = [
              { label: '正在审批', action: () => {} }
            ]
            this.todayRevenue = 0
            this.todayOrderCount = 0
            this.latestComments = []
          } else if (status === '封禁中') {
            // 封禁中，仅显示提示
            this.navItems = [
              { label: '封禁，请联系管理员', action: () => {} }
            ]
            this.todayRevenue = 0
            this.todayOrderCount = 0
            this.latestComments = []
          } else if (status === '正常') {
            // 正常，显示全部功能
            this.navItems = [
              { label: '管理店铺', action: () => this.goTo('shop') },
              { label: '管理订单', action: () => this.goTo('order') },
              { label: '查看数据', action: () => this.goTo('data') },
              { label: '个人中心', action: () => this.goTo('withdraw') }
            ]
            this.fetchMerchantHomeData(result.merchantId)
          }
        } else {
          this.$toast && this.$toast(result.message || '商家信息获取失败')
        }
      } catch (error) {
        this.$toast && this.$toast('网络异常，商家信息获取失败')
      }
      if (debug_merchant_created) {
        this.navItems = [
          { label: '管理店铺', action: () => this.goTo('shop') },
          { label: '管理订单', action: () => this.goTo('order') },
          { label: '查看数据', action: () => this.goTo('data') },
          { label: '个人中心', action: () => this.goTo('withdraw') }
        ]
      }
    }
  },
  mounted() {
    console.log('MerchantHome mounted')
    // 检查是否已创建商家
    this.navItems = [
      { label: '创建店铺', action: () => this.goTo('register') }
    ]
    this.fetchMerchantInfo()
  }
}
</script>

<style scoped>
.merchant-home {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: #f8f8f8;
}
.top-stats {
  display: flex;
  justify-content: space-between;
  background: #fff;
  padding: 1.2em 1.5em;
  border-bottom: 1px solid #eee;
}
.stat-item {
  flex: 1;
  text-align: center;
}
.stat-title {
  font-size: 1em;
  color: #888;
}
.stat-value {
  font-size: 1.5em;
  font-weight: bold;
  color: #333;
  margin-top: 0.3em;
}
.comments-section {
  flex: 1;
  padding: 1.5em;
}
.comments-title {
  font-size: 1.1em;
  font-weight: bold;
  margin-bottom: 1em;
}
.comments-list {
  display: flex;
  flex-direction: column;
  gap: 0.7em;
}
.comment-row {
  display: flex;
  justify-content: space-between;
  background: #fff;
  padding: 0.7em 1em;
  border-radius: 6px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.03);
}
.comment-username {
  font-weight: bold;
  color: #3498db;
  width: 30%;
  text-align: left;
}
.comment-content {
  width: 65%;
  color: #333;
  text-align: left;
}
.empty-tip {
  color: #aaa;
  font-size: 0.95em;
  margin: 1em 0;
  text-align: center;
}
.pagination-bar {
  margin-top: 1em;
  display: flex;
  align-items: center;
  gap: 1em;
  font-size: 1em;
}
.pagination-btn {
  background: #3498db;
  color: #fff;
  border: none;
  border-radius: 4px;
  padding: 0.5em 1em;
  cursor: pointer;
  transition: background 0.2s;
}
.pagination-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}
.pagination-info {
  margin: 0 1em;
  color: #333;
}
.bottom-nav {
  display: flex;
  justify-content: space-around;
  align-items: center;
  background: #fff;
  border-top: 1px solid #eee;
  height: 56px;
  position: fixed;
  left: 50%;
  bottom: 0;
  transform: translateX(-50%);
  width: 100%;
  max-width: 400px; /* 你可以根据主内容区宽度调整 */
  box-sizing: border-box;
}
.nav-item {
  flex: 1;
  text-align: center;
  color: #3498db;
  font-size: 1.1em;
  cursor: pointer;
  padding: 0.7em 0;
  transition: background 0.2s;
}
.nav-item:hover {
  background: #f0f8ff;
}
</style>