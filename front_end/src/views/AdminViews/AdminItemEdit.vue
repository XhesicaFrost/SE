<template>
  <TopNav :navInfo="navInfo" />
  <div class="admin-item-edit">
    <div class="form-group">
      <label>商品名称</label>
      <input type="text" v-model="itemName" placeholder="请输入商品名称" />
    </div>
    <div class="form-group">
      <label>商品价格</label>
      <input type="number" v-model.number="itemPrice" min="0" step="0.01" placeholder="请输入商品价格" />
    </div>
    <div class="form-group">
      <label>商品描述</label>
      <input type="text" v-model="itemDescription" placeholder="请输入商品描述" />
    </div>
    <div class="form-group">
      <label>商品图片</label>
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
  name: 'AdminItemEdit',
  components: { TopNav },
  props: {
    id: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      itemName: '',
      itemPrice: '',
      itemDescription: '',
      itemImage: null,
      previewImage: '',
      errorMessage: '',
      navInfo: { 
        title: '编辑商品', 
        pageReturn: () => { this.$router.push('/admin/items') } 
      }
    }
  },
  methods: {
    async fetchItemInfo() {
      try {
        const response = await fetchWithTimeout(`${BASE_URL}/admin/item/${this.id}`)
        const result = await response.json()
        if (result.code === 200) {
          this.itemName = result.data.name
          this.itemPrice = result.data.price
          this.itemDescription = result.data.description
          this.previewImage = result.data.image
        } else {
          this.errorMessage = '获取商品信息失败'
        }
      } catch (e) {
        this.errorMessage = '网络错误，获取商品信息失败'
      }
    },
    handleImageChange(event) {
      const file = event.target.files[0]
      if (file) {
        this.itemImage = file
        this.previewImage = URL.createObjectURL(file)
      }
    },
    async handleEdit() {
      if (!this.itemName || !this.itemPrice || !this.itemDescription) {
        this.errorMessage = '请填写完整信息'
        return
      }
      
      this.errorMessage = ''
      const formData = new FormData()
      formData.append('name', this.itemName)
      formData.append('price', this.itemPrice)
      formData.append('description', this.itemDescription)
      if (this.itemImage) {
        formData.append('image', this.itemImage)
      }
      formData.append('itemId', this.id)

      try {
        const response = await fetchWithTimeout(`${BASE_URL}/admin/item/edit`, {
          method: 'POST',
          body: formData
        })
        const result = await response.json()
        if (result.code === 200) {
          alert('修改成功')
          this.$router.push('/admin/items')
        } else {
          this.errorMessage = '修改失败：' + (result.message || '未知错误')
        }
      } catch (e) {
        this.errorMessage = '网络错误，修改失败'
      }
    },
    goBack() {
      this.$router.push('/admin/items')
    }
  },
  mounted() {
    this.fetchItemInfo()
  }
}
</script>

<style scoped>
.admin-item-edit {
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

.form-group input[type="text"],
.form-group input[type="number"] {
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