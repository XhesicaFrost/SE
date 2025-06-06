<template>
  <div class="register-view">
    <div class="login-header">
      <img src="@/assets/logo.jpg" alt="logo" class="login-logo" />
      <span class="login-title">新增商品</span>
    </div>
    <form @submit.prevent="handleRegister">
      <div class="form-group">
        <label>商品名称：</label>
        <input v-model="itemName" type="text" required placeholder="请输入商品名称" :disabled="submitStatus==='success'" />
      </div>
      <div class="form-group">
        <label>商品描述：</label>
        <input v-model="itemDescription" type="text" required placeholder="请输入商品描述" :disabled="submitStatus==='success'" />
      </div>
      <div class="form-group">
        <label>商品图片：</label>
        <input type="file" accept="image/*" @change="onImageChange" :disabled="submitStatus==='success'" />
        <div v-if="itemImageUrl" class="preview-img">
          <img :src="itemImageUrl" alt="商品图片预览" />
        </div>
      </div>
      <div class="form-group">
        <label>单价：</label>
        <input v-model.number="itemPrice" type="number" min="0" step="0.01" required placeholder="请输入单价" :disabled="submitStatus==='success'" />
      </div>
      <button
        v-if="submitStatus==='normal'"
        type="submit"
        class="submit-btn"
      >新增商品</button>
      <button
        v-else
        type="button"
        class="submit-btn"
        @click="goBack"
      >已提交，点击返回商品管理</button>
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
  name: 'sellerItemRegister',
  computed: {
    ...mapState('sellerStore', {
      sellerId: state => state.sellerId
    })
  },
  data() {
    return {
      itemName: '',
      itemImage: null,
      itemImageUrl: '',
      itemPrice: '',
      SellerItemEdit:'',
      errorMessage: '',
      submitStatus: 'normal' // normal | success
    }
  },
  methods: {
    onImageChange(e) {
      const file = e.target.files[0]
      if (file) {
        this.itemImage = file
        this.itemImageUrl = URL.createObjectURL(file)
      }
    },
    async handleRegister() {
      if (!this.itemName || !this.itemImage || this.itemPrice === '' || this.itemPrice === null) {
        this.errorMessage = '请填写完整信息并上传图片'
        return
      }
      this.errorMessage = ''
      // 构造 FormData
      const formData = new FormData()
      formData.append('itemName', this.itemName)
      formData.append('itemImage', this.itemImage)
      formData.append('itemPrice', this.itemPrice)
      formData.append('sellerId', this.sellerId)
      formData.append('itemDescription', this.itemDescription || '')
      try {
        const response = await fetchWithTimeout(`${BASE_URL}/seller/item/register`, {
          method: 'POST',
          body: formData
        })
        const result = await response.json()
        if (result.status === 'success') {
          this.submitStatus = 'success'
        } else {
          this.errorMessage = '未能成功发送，请重试'
          // 清空所有已填信息
          this.itemName = ''
          this.itemImage = null
          this.itemImageUrl = ''
          this.itemPrice = ''
        }
      } catch (e) {
        this.errorMessage = '网络错误或超时，未能成功发送'
        // 清空所有已填信息
        this.itemName = ''
        this.itemImage = null
        this.itemImageUrl = ''
        this.itemPrice = ''
      }
    },
    goBack() {
      this.$router.push('/seller/shop')
    }
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
input[type="text"], input[type="file"], input[type="number"] {
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
.preview-img {
  margin-top: 0.5em;
}
.preview-img img {
  max-width: 100%;
  max-height: 120px;
  border-radius: 6px;
  border: 1px solid #eee;
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