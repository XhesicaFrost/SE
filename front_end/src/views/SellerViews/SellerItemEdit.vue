<template>
  <div class="register-view">
    <div class="login-header">
      <img src="@/assets/logo.jpg" alt="logo" class="login-logo" />
      <span class="login-title">编辑商品信息</span>
    </div>
    <form @submit.prevent="handleEdit">
      <div class="form-group">
        <label>商品名称：</label>
        <input v-model="itemName" type="text" required placeholder="请输入商品名称" :disabled="submitStatus==='success'" />
      </div>
      <div class="form-group">
        <label>商品图片：</label>
        <input type="file" accept="image/*" @change="onImageChange" :disabled="submitStatus==='success'" />
        <div v-if="itemImageUrl" class="preview-img">
          <img :src="itemImageUrl" alt="商品图片预览" />
        </div>
      </div>
      <div class="form-group">
        <label>商品描述：</label>
        <input v-model="itemDescription" type="text" required placeholder="请输入商品描述" :disabled="submitStatus==='success'" />
      </div>
      <div class="form-group">
        <label>单价：</label>
        <input v-model.number="itemPrice" type="number" min="0" step="0.01" required placeholder="请输入单价" :disabled="submitStatus==='success'" />
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
      >修改已提交，点击返回商品管理</button>
    </form>
    <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
    <div class="login-support">
      support by 藤田ことねファングールプ
    </div>
  </div>
</template>

<script>
import { BASE_URL ,fetchWithTimeout} from '@/config.js'

export default {
  name: 'sellerItemEdit',
  data() {
    return {
      itemId: '', // 商品ID
      itemName: '',
      itemImage: null,
      itemImageUrl: '',
      itemPrice: '',
      itemDescription: '',
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
    async fetchItemInfo() {
      // 获取商品信息用于预填充
      try {
        const params = new URLSearchParams({ id: this.itemId }).toString()
        const response = await fetchWithTimeout(`${BASE_URL}/seller/item?${params}`)
        const result = await response.json()
        if (result.code === 200 && result.data) {
          this.itemName = result.data.itemName
          this.itemPrice = result.data.itemPrice
          // 服务器返回 base64 图片数据
          this.itemImageUrl = result.data.itemImage
            ? `data:image/png;base64,${result.data.itemImage}`
            : ''
          this.itemDescription = result.data.itemDescription || ''
        } else {
          this.errorMessage = '商品信息获取失败'
        }
      } catch (e) {
        this.errorMessage = '网络错误，商品信息获取失败'
      }
    },
    async handleEdit() {
      if (!this.itemName || this.itemPrice === '' || this.itemPrice === null) {
        this.errorMessage = '请填写完整信息'
        return
      }
      this.errorMessage = ''
      // 构造 FormData
      const formData = new FormData()
      formData.append('itemId', this.itemId)
      formData.append('itemName', this.itemName)
      formData.append('itemPrice', this.itemPrice)
      formData.append('itemDescription', this.itemDescription)
      if (this.itemImage) {
        formData.append('itemImage', this.itemImage)
      }
      try {
        const response = await fetchWithTimeout(`${BASE_URL}/seller/item/edit`, {
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
    // 从路由获取商品ID
    console.log("sellerItemEdit mounted")
    this.itemId = this.$route.params.id
    this.fetchItemInfo()
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