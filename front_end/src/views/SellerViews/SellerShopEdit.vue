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
      <!-- ✅ 新增：店铺标签选择 -->
      <div class="form-group">
        <label>店铺类型：<span class="tag-hint">（必选1-3个标签）</span></label>
        <div class="tags-container">
          <div 
            v-for="tag in availableTags" 
            :key="tag"
            class="tag-item"
            :class="{ 'tag-selected': shopTags.includes(tag), 'tag-disabled': !shopTags.includes(tag) && shopTags.length >= 3 }"
            @click="toggleTag(tag)"
          >
            {{ tag }}
          </div>
        </div>
        <div class="selected-tags-info">
          已选择：{{ shopTags.length }}/3 个标签
        </div>
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
        :disabled="shopTags.length === 0"
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
      // ✅ 新增：店铺标签相关数据
      shopTags: [], // 选中的标签数组
      availableTags: [
        '快餐', '奶茶', '咖啡', '甜品', '火锅', '烧烤', 
        '中式', '西式', '日式', '韩式', '泰式', '川菜',
        '粤菜', '湘菜', '东北菜', '海鲜', '素食', '健康餐',
        '夜宵', '小吃', '面食', '米饭', '汤品', '饮品','早餐'
      ],
      errorMessage: '',
      submitStatus: 'normal' // normal | success
    }
  },
  methods: {
    /**
     * 切换标签选择状态
     * @param {string} tag - 标签名称
     */
    toggleTag(tag) {
      if (this.submitStatus === 'success') return
      
      const index = this.shopTags.indexOf(tag)
      if (index > -1) {
        // 已选中，取消选择
        this.shopTags.splice(index, 1)
      } else {
        // 未选中，添加选择（最多3个）
        if (this.shopTags.length < 3) {
          this.shopTags.push(tag)
        }
      }
    },
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
          // ✅ 新增：加载店铺标签数据
          this.shopTags = result.data.shopTags ? JSON.parse(result.data.shopTags) : []
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
      // ✅ 更新验证逻辑，包含标签验证
      if (!this.shopName || !this.shopAddress) {
        this.errorMessage = '请填写完整信息'
        return
      }
      if (this.shopTags.length === 0) {
        this.errorMessage = '请至少选择一个店铺类型标签'
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
      formData.append('sellerId', this.sellerId)
      // ✅ 新增：添加标签数据到表单
      formData.append('shopTags', JSON.stringify(this.shopTags))

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
.register-view {
  max-width: 400px;
  margin: 48px auto 36px auto;
  padding: 1em;
  background: #fff;
  min-height: 100vh;
}
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

/* ✅ 新增：标签相关样式 */
.tag-hint {
  font-size: 0.85em;
  color: #666;
  font-weight: normal;
}
.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5em;
  margin-top: 0.5em;
  max-height: 120px;
  overflow-y: auto;
  padding: 0.3em;
  border: 1px solid #eee;
  border-radius: 4px;
}
.tag-item {
  padding: 0.3em 0.8em;
  background: #f5f5f5;
  border: 1px solid #ddd;
  border-radius: 20px;
  cursor: pointer;
  font-size: 0.9em;
  transition: all 0.2s;
  user-select: none;
}
.tag-item:hover {
  background: #e8f5e8;
  border-color: #4caf50;
}
.tag-selected {
  background: #4caf50 !important;
  color: white !important;
  border-color: #4caf50 !important;
}
.tag-disabled {
  background: #f0f0f0 !important;
  color: #ccc !important;
  cursor: not-allowed !important;
  border-color: #eee !important;
}
.selected-tags-info {
  margin-top: 0.5em;
  font-size: 0.85em;
  color: #666;
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
  transition: background-color 0.2s;
}
.submit-btn:hover {
  background-color: #43a047;
}
.submit-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
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