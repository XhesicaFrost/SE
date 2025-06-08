<template>
  <TopNav :navInfo="navInfo" />
  <div class="promotion-view">
    <div class="promotion-list">
      <div class="promotion-item" v-for="promo in promotions" :key="promo.promotionId">
        <div class="promo-info">
          <div class="promo-name">{{ promo.promotionName }}</div>
          <div class="promo-desc">满{{ promo.full }}减{{ promo.minus }}</div>
          <div class="promo-time">{{ promo.startTime }} ~ {{ promo.endTime }}</div>
        </div>
        <div class="promo-actions">
          <button class="edit-btn" @click="editPromotion(promo.promotionId)">编辑</button>
          <button class="delete-btn" @click="deletePromotion(promo.promotionId)">删除</button>
        </div>
      </div>
      <div v-if="promotions.length === 0" class="empty-tip">暂无促销活动</div>
    </div>
    <BottomNav :navItems="navItems" />
  </div>
</template>

<script>
import { BASE_URL ,fetchWithTimeout} from '@/config.js'
import { mapState } from 'vuex'
import BottomNav from '@/components/bottomNav.vue'
import TopNav from '@/components/topNav.vue'

export default {
  name: 'sellerPromotion',
  components: { BottomNav, TopNav },
  data() {
    return {
      promotions: [],
      navItems: [
        { label: '新建促销活动', action: () => { this.$router.push('/seller/promotion/register') } }
      ],
      navInfo: { title: '管理促销活动', pageReturn: () => { this.$router.push('/seller') } }
    }
  },
  computed: {
    ...mapState('sellerStore', ['sellerId'])
  },
  methods: {
    async fetchPromotions() {
      try {
        const params = new URLSearchParams({ sellerId: this.sellerId }).toString()
        const response = await fetchWithTimeout(`${BASE_URL}/seller/promotion?${params}`)
        const result = await response.json()
        if (result.code==200 && Array.isArray(result.data)) {
          this.promotions = result.data
        } else {
          this.promotions = []
        }
      } catch (e) {
        this.promotions = []
      }
    },
    async deletePromotion(promotionId) {
      if (!confirm('确定要删除这个促销活动吗？')) {
        return
      }
      
      try {
        const response = await fetchWithTimeout(`${BASE_URL}/seller/promotion`, {
          method: 'DELETE',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ promotionId })
        })
        const result = await response.json()
        if (result.code==200) {
          alert('删除成功')
          this.fetchPromotions()
        } else {
          alert('删除失败：' + (result.message || '未知错误'))
        }
      } catch (e) {
        alert('网络错误，删除失败')
      }
    },
    editPromotion(promotionId) {
      this.$router.push({ 
        name: 'sellerPromotionEdit', 
        params: { id: promotionId } 
      })
    }
  },
  mounted() {
    this.fetchPromotions()
  }
}
</script>

<style scoped>
.promotion-view {
  max-width: 400px;
  margin: 48px auto 36px auto;
  padding: 1em;
  background: #fff;
  min-height: 100vh;
}
.promotion-list {
  display: flex;
  flex-direction: column;
  gap: 0.7em;
}
.promotion-item {
  display: flex;
  align-items: center;
  background: #f9f9f9;
  border-radius: 6px;
  padding: 0.7em 1em;
  box-shadow: 0 1px 3px rgba(0,0,0,0.03);
  position: relative;
}
.promo-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.promo-name {
  font-weight: bold;
  font-size: 1.1em;
  margin-bottom: 0.2em;
}
.promo-desc {
  color: #388e3c;
  font-size: 1em;
  margin-bottom: 0.2em;
}
.promo-time {
  color: #888;
  font-size: 0.95em;
}
.promo-actions {
  display: flex;
  flex-direction: column;
  gap: 0.5em;
  margin-left: 1em;
}
.edit-btn {
  background: #1976d2;
  color: #fff;
  border: none;
  border-radius: 6px;
  padding: 0.4em 1.2em;
  font-size: 1em;
  cursor: pointer;
  margin-bottom: 0.2em;
  transition: background 0.2s;
}
.edit-btn:hover {
  background: #1565c0;
}
.delete-btn {
  background: #e53935;
  color: #fff;
  border: none;
  border-radius: 6px;
  padding: 0.4em 1.2em;
  font-size: 1em;
  cursor: pointer;
  transition: background 0.2s;
}
.delete-btn:hover {
  background: #b71c1c;
}
.empty-tip {
  color: #aaa;
  font-size: 0.95em;
  margin: 1em 0;
  text-align: center;
}
</style>