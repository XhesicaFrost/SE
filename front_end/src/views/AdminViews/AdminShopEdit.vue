<template>
  <TopNav :navInfo="navInfo" />
  <div class="admin-shop-edit">
    <div class="form-group">
      <label>店铺名称</label>
      <input type="text" v-model="shopName" placeholder="请输入店铺名称" />
    </div>
    <div class="form-group">
      <label>店铺地址</label>
      <input type="text" v-model="shopAddress" placeholder="请输入店铺地址" />
    </div>
    <div class="form-group">
      <label>店铺图片</label>
      <input type="file" @change="handleImageChange" accept="image/*" />
      <img v-if="previewImage" :src="previewImage" class="preview-image" />
    </div>
    <div class="error-message" v-if="errorMessage">{{ errorMessage }}</div>
    <div class="button-group">
      <button class="submit-btn" @click="handleEdit">保存修改</button>
      <button class="cancel-btn" @click="goBack">返回</button>
    </div>
  </div>
</template>

<script>
import TopNav from '@/components/topNav.vue'
import { BASE_URL, fetchWithTimeout } from '@/config.js'

export default {
  name: 'AdminShopEdit',
  components: { TopNav },
  props: {
    id: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      shopName: '',
      shopAddress: '',
      shopImage: null,
      previewImage: '',
      errorMessage: '',
      navInfo: { 
        title: '编辑店铺', 
        pageReturn: () => { this.$router.push('/admin/shops') } 
      }
    }
  },
  methods: {
    async fetchShopInfo() {
      try {
        const response = await fetchWithTimeout(`${BASE_URL}/admin/shop/${this.id}`)
        const result = await response.json()
        if (result.code === 200) {
          this.shopName = result.data.name
          this.shopAddress = result.data.address
          this.previewImage = result.data.image
        } else {
          this.errorMessage = '获取店铺信息失败'
        }
      } catch (e) {
        this.errorMessage = '网络错误，获取店铺信息失败'
      }
    },
    handleImageChange(event) {
      const file = event.target.files[0]
      if (file) {
        this.shopImage = file
        this.previewImage = URL.createObjectURL(file)
      }
    },
    async handleEdit() {
      if (!this.shopName || !this.shopAddress) {
        this.errorMessage = '请填写完整信息'
        return
      }
      
      this.errorMessage = ''
      const formData = new FormData()
      formData.append('name', this.shopName)
      formData.append('address', this.shopAddress)
      if (this.shopImage) {
        formData.append('image', this.shopImage)
      }
      formData.append('shopId', this.id)

      try {
        const response = await fetchWithTimeout(`${BASE_URL}/admin/shop/edit`, {
          method: 'POST',
          body: formData
        })
        const result = await response.json()
        if (result.code === 200) {
          alert('修改成功')
          this.$router.push('/admin/shops')
        } else {
          this.errorMessage = '修改失败：' + (result.message || '未知错误')
        }
      } catch (e) {
        this.errorMessage = '网络错误，修改失败'
      }
    },
    goBack() {
      this.$router.push('/admin/shops')
    }
  },
  mounted() {
    this.fetchShopInfo()
  }
}
</script>

<style scoped>
.admin-shop-edit {
  max-width: 500px;
  margin: 48px auto 36px auto;
  padding: 1em;
  background: #fff;
  min-height: 100vh;
}

.form-group {
  margin-bottom: 1.5em;
}

.form-group label {
  display: block;
  margin-bottom: 0.5em;
  font-weight: bold;
  color: #333;
}

.form-group input[type="text"] {
  width: 100%;
  padding: 0.8em;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1em;
}

.form-group input[type="file"] {
  width: 100%;
  padding: 0.5em;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.preview-image {
  max-width: 200px;
  max-height: 200px;
  margin-top: 1em;
  border-radius: 4px;
}

.error-message {
  color: #f44336;
  margin-bottom: 1em;
}

.button-group {
  display: flex;
  gap: 1em;
  margin-top: 2em;
}

.submit-btn, .cancel-btn {
  flex: 1;
  padding: 0.8em;
  border: none;
  border-radius: 4px;
  font-size: 1em;
  cursor: pointer;
  transition: background-color 0.2s;
}

.submit-btn {
  background-color: #4caf50;
  color: white;
}

.submit-btn:hover {
  background-color: #388e3c;
}

.cancel-btn {
  background-color: #f44336;
  color: white;
}

.cancel-btn:hover {
  background-color: #d32f2f;
}
</style> 