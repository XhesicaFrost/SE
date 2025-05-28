<!--
  sellerRegister.vue 组件说明

  本页面用于商家注册店铺，包含：
  - 顶部 logo 和标题
  - 店铺名称输入框
  - 店铺地址输入框
  - 店铺图片上传
  - 注册按钮（绿色，样式参考RegisterView.vue）
  - 错误信息提示
  - 页面底部支持信息
-->

<template>
  <TopNav :navInfo="navInfo" />
  <div class="register-view">
    <div class="login-header">
      <img src="@/assets/logo.jpg" alt="logo" class="login-logo" />
      <span class="login-title">注册您的店铺</span>
    </div>
    <form @submit.prevent="handleRegister">
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
      >注册店铺</button>
      <button
        v-else
        type="button"
        class="submit-btn"
        @click="goBack"
      >已发送申请，等待审批，点击返回主页面</button>
    </form>
    <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
    <div class="login-support">
      support by 藤田ことねファングールプ
    </div>
  </div>
  <BottomNav :navItems="navItems" />
</template>

<script>
import BottomNav from '@/components/bottomNav.vue'
import TopNav from '@/components/topNav.vue'
import { BASE_URL } from '@/config.js'
import { mapState } from 'vuex'

export default {
  name: 'sellerRegister',
  components: { TopNav, BottomNav },
  computed: {
    ...mapState('userStore', {
      userId: state => state.userInfo.userId
    })
  },
  data() {
    return {
      shopName: '',
      shopAddress: '',
      shopImage: null,
      shopImageUrl: '',
      errorMessage: '',
      submitStatus: 'normal', // normal | success
      navItems: [
        { label: '创建店铺', action: () => { this.$router.push('/seller/register') }, isActive: true }
      ],
      navInfo: { title: '创建店铺', pageReturn: () => { this.$router.push('/seller') } }
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
    async handleRegister() {
      if (!this.shopName || !this.shopAddress || !this.shopImage) {
        this.errorMessage = '请填写完整信息并上传图片'
        return
      }
      this.errorMessage = ''
      // 构造 FormData
      const formData = new FormData()
      formData.append('shopName', this.shopName)
      formData.append('shopAddress', this.shopAddress)
      formData.append('shopImage', this.shopImage)
      formData.append('userId', this.userId) // 发送商家id

      try {
        const response = await fetch(`${BASE_URL}/seller/register`, {
          method: 'POST',
          body: formData
        })
        const result = await response.json()
        if (result.status === 'success') {
          this.submitStatus = 'success'
        } else {
          this.errorMessage = '未能成功发送，请重试'
          // 清空所有已填信息
          this.shopName = ''
          this.shopAddress = ''
          this.shopImage = null
          this.shopImageUrl = ''
        }
      } catch (e) {
        this.errorMessage = '网络错误，未能成功发送'
        // 清空所有已填信息
        this.shopName = ''
        this.shopAddress = ''
        this.shopImage = null
        this.shopImageUrl = ''
      }
    },
    goBack() {
      this.$router.push('/seller')
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