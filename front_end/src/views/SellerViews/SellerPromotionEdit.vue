<template>
  <div class="register-view">
    <div class="login-header">
      <img src="@/assets/logo.jpg" alt="logo" class="login-logo" />
      <span class="login-title">编辑促销活动</span>
    </div>
    <form @submit.prevent="handleEdit">
      <div class="form-group">
        <label>活动名称：</label>
        <input v-model="promotionName" type="text" required placeholder="请输入活动名称" :disabled="submitStatus==='success'" />
      </div>
      <div class="form-group">
        <label>满多少元：</label>
        <input v-model.number="full" type="number" min="0" step="0.01" required placeholder="请输入满减门槛" :disabled="submitStatus==='success'" />
      </div>
      <div class="form-group">
        <label>减多少元：</label>
        <input v-model.number="minus" type="number" min="0" step="0.01" required placeholder="请输入减免金额" :disabled="submitStatus==='success'" />
      </div>
      <div class="form-group">
        <label>开始时间：</label>
        <input v-model="startTime" type="datetime-local" required :disabled="submitStatus==='success'" />
      </div>
      <div class="form-group">
        <label>结束时间：</label>
        <input v-model="endTime" type="datetime-local" required :disabled="submitStatus==='success'" />
      </div>
      <button
        v-if="submitStatus==='normal'"
        type="submit"
        class="submit-btn"
      >保存修改</button>
      <button
        v-else
        type="button"
        class="submit-btn"
        @click="goBack"
      >修改已提交，点击返回促销管理</button>
    </form>
    <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
    <div class="login-support">
      support by 藤田ことねファングールプ
    </div>
  </div>
</template>

<script>
import { BASE_URL, fetchWithTimeout } from '@/config.js'
import { mapState } from 'vuex'

export default {
  name: 'sellerPromotionEdit',
  computed: {
    ...mapState('sellerStore', {
      sellerId: state => state.sellerId
    })
  },
  data() {
    return {
      promotionId: '',
      promotionName: '',
      full: '',
      minus: '',
      startTime: '',
      endTime: '',
      errorMessage: '',
      submitStatus: 'normal' // normal | success
    }
  },
  methods: {
    async fetchPromotionInfo() {
      // 获取当前促销活动信息用于预填充
      try {
        const params = new URLSearchParams({ promotionId: this.promotionId }).toString()
        const response = await fetchWithTimeout(`${BASE_URL}/seller/promotion/detail?${params}`)
        const result = await response.json()
        if (result.success && result.data) {
          this.promotionName = result.data.promotionName
          this.full = result.data.full
          this.minus = result.data.minus
          this.startTime = result.data.startTime
          this.endTime = result.data.endTime
        } else {
          this.errorMessage = '促销活动信息获取失败'
        }
      } catch (e) {
        this.errorMessage = '网络错误或超时，促销活动信息获取失败'
      }
    },
    async handleEdit() {
      if (!this.promotionName || this.full === '' || this.minus === '' || !this.startTime || !this.endTime) {
        this.errorMessage = '请填写完整信息'
        return
      }
      this.errorMessage = ''
      const formData = new FormData()
      formData.append('promotionId', this.promotionId)
      formData.append('promotionName', this.promotionName)
      formData.append('full', this.full)
      formData.append('minus', this.minus)
      formData.append('startTime', this.startTime)
      formData.append('endTime', this.endTime)
      formData.append('sellerId', this.sellerId)

      try {
        const response = await fetchWithTimeout(`${BASE_URL}/seller/promotion/edit`, {
          method: 'POST',
          body: formData
        })
        const result = await response.json()
        if (result.status === 'success') {
          this.submitStatus = 'success'
        } else {
          this.errorMessage = '未能成功修改，请重试'
        }
      } catch (e) {
        this.errorMessage = '网络错误或超时，未能成功修改'
      }
    },
    goBack() {
      this.$router.push('/seller/promotion')
    }
  },
  mounted() {
    this.promotionId = this.$route.params.id
    this.fetchPromotionInfo()
  }
}
</script>

<style scoped>
.login-header {
  display: flex;
  align-items: center;
  margin-bottom: 1.5em;
}
.login-logo {
  width: 48px;
  height: 48px;
  object-fit: contain;
  margin-right: 1em;
}
.login-title {
  font-size: 2em;
  font-weight: bold;
  color: #333;
}
.form-group {
  margin-bottom: 1.2em;
}
input[type="text"], input[type="number"], input[type="datetime-local"] {
  width: 100%;
  padding: 0.5em;
  border-radius: 4px;
  border: 1px solid #bfbfbf;
  font-size: 1em;
  margin-top: 0.3em;
}
.submit-btn {
  background-color: #4caf50;
  color: #fff;
  border: none;
  padding: 0.75em 2.5em;
  border-radius: 10px;
  font-size: 1.2em;
  cursor: pointer;
  margin-top: 1em;
}
.submit-btn:hover {
  background-color: #43a047;
}
.login-support {
  margin-top: 2em;
  text-align: center;
  font-size: 0.85em;
  color: #888;
}
.error-message {
  color: red;
  margin: 1em 0;
}
</style>