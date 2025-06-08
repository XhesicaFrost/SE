<template>
  <div class="register-view">
    <div class="login-header">
      <img src="@/assets/logo.jpg" alt="logo" class="login-logo" />
      <span class="login-title">编辑店铺信息</span>
    </div>
    <form @submit.prevent="handleEdit">
      <div class="form-group">
        <label>店铺名称：</label>
        <input v-model="shopName" type="text" required placeholder="请输入店铺名称" :disabled="submitStatus==='success'" />
      </div>
      <div class="form-group">
        <label>店铺地址：</label>
        <input v-model="shopAddress" type="text" required placeholder="请输入店铺地址" :disabled="submitStatus==='success'" />
      </div>
      <div class="form-group">
        <label>店铺图片：</label>
        <input type="file" accept="image/*" @change="onImageChange" :disabled="submitStatus==='success'" />
        <div v-if="shopImageUrl" class="preview-img">
          <img :src="shopImageUrl" alt="店铺图片预览" />
        </div>
      </div>
      <button
        v-if="submitStatus==='normal'"
        type="submit"
        class="submit-btn"
      >修改店铺信息</button>
      <button
        v-else
        type="button"
        class="submit-btn"
        @click="goBack"
      >修改已提交，点击返回主页面</button>
    </form>
    <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
    <div class="login-support">
      support by 藤田ことねファングールプ
    </div>
  </div>
</template>

<script>
import { BASE_URL ,fetchWithTimeout} from '@/config.js'
import { mapState } from 'vuex'

export default {
  name: 'sellerShopEdit',
  computed: {
    ...mapState('sellerStore', {
      sellerId: state => state.sellerId
    })
  },
  data() {
    return {
      shopName: '',
      shopAddress: '',
      shopImage: null,
      shopImageUrl: '',
      errorMessage: '',
      submitStatus: 'normal' // normal | success
    }
  },
  methods: {
    onImageChange(e) {
      const file = e.target.files[0]
      if (file) {
        this.shopImage = file
        this.shopImageUrl = URL.createObjectURL(file)
      }
    },
    async fetchShopInfo() {
      try {
        const params = new URLSearchParams({ sellerId: this.sellerId }).toString()
        const response = await fetchWithTimeout(`${BASE_URL}/shop?${params}`)
        const result = await response.json()
        if ( result.code === 200 && result.data) {
          this.shopName = result.data.shopName
          this.shopAddress = result.data.shopAddress
          // 服务器返回 base64 图片数据
          this.shopImageUrl = result.data.shopImg
            ? `data:image/png;base64,${result.data.shopImg}`
            : ''
        } else {
          this.errorMessage = '店铺信息获取失败'
        }
      } catch (e) {
        this.errorMessage = '网络错误，店铺信息获取失败'
      }
    },
    async handleEdit() {
      if (!this.shopName || !this.shopAddress) {
        this.errorMessage = '请填写完整信息'
        return
      }
      this.errorMessage = ''
      // 构造 FormData
      const formData = new FormData()
      formData.append('shopName', this.shopName)
      formData.append('shopAddress', this.shopAddress)
      if (this.shopImage) {
        formData.append('shopImage', this.shopImage)
      }
      formData.append('sellerId', this.sellerId) // 发送商家id

      try {
        const response = await fetchWithTimeout(`${BASE_URL}/seller/edit`, {
          method: 'POST',
          body: formData
        })
        const result = await response.json()
        if (result.data.status === 'success') {
          this.submitStatus = 'success'
        } else {
          this.errorMessage = '未能成功修改，请重试'
        }
      } catch (e) {
        this.errorMessage = '网络错误，未能成功修改'
      }
    },
    goBack() {
      this.$router.push('/seller/shop')
    }
  },
  mounted() {
    this.fetchShopInfo()
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
input[type="text"], input[type="file"] {
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