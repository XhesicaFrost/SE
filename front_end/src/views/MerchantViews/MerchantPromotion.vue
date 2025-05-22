<template>
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
import { BASE_URL } from '@/config.js'
import { mapState } from 'vuex'
import BottomNav from '@/components/bottomNav.vue'

export default {
  name: 'MerchantPromotion',
  components: { BottomNav },
  data() {
    return {
      promotions: [],
      navItems: [
        { label: '新建促销活动', action: () => { this.$router.push('/merchant/promotion/register') } }
      ]
    }
  },
  computed: {
    ...mapState('merchantStore', ['merchantId'])
  },
  methods: {
    async fetchPromotions() {
      try {
        const params = new URLSearchParams({ merchantId: this.merchantId }).toString()
        const response = await fetch(`${BASE_URL}/merchant/promotion?${params}`)
        const result = await response.json()
        if (result.success && Array.isArray(result.data)) {
          this.promotions = result.data
        } else {
          this.promotions = []
        }
      } catch (e) {
        this.promotions = []
      }
    },
    async deletePromotion(promotionId) {
      try {
        const response = await fetch(`${BASE_URL}/merchant/promotion`, {
          method: 'DELETE',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ promotionId })
        })
        const result = await response.json()
        if (result.success) {
          this.fetchPromotions()
        } else {
          alert('删除失败')
        }
      } catch (e) {
        alert('网络错误，删除失败')
      }
    },
    editPromotion(promotionId) {
      this.$router.push(`/merchant/edit/${promotionId}`)
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
  margin: 0 auto 70px auto;
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