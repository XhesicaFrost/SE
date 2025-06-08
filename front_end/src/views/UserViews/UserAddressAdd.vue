<template>
  <TopNav :navInfo="navInfo" />
  <div class="user-home">
    <form @submit.prevent="submitAddress" class="address-form">
      <div class="form-group">
        <label for="name">姓名</label>
        <input type="text" id="name" v-model="address.name" required />
      </div>
      <div class="form-group">
        <label for="phone">电话</label>
        <input type="text" id="phone" v-model="address.phone" required pattern="^1\d{10}$"/>
      </div>
      <div class="form-group">
        <label for="fullAddress">详细地址</label>
        <textarea id="fullAddress" v-model="address.fullAddress" required></textarea>
      </div>
      <button type="submit" class="submit-btn">提交</button>
    </form>
  </div>
</template>

<script>
import TopNav from '@/components/topNav.vue'
import { BASE_URL, fetchWithTimeout } from '@/config.js'

export default {
  name: 'userAddressAdd',
  components: { TopNav },
  data() {
    return {
      navInfo: { 
        title: '新增地址', 
        pageReturn: () => { this.$router.go(-1) } 
      },
      address: {
        name: '',
        phone: '',
        fullAddress: '',
        current: false
      }
    }
  },
  methods: {
    async submitAddress() {
      try {
        const formData = new FormData()
        formData.append('name', this.address.name)
        formData.append('phone', this.address.phone)
        formData.append('fullAddress', this.address.fullAddress)
        formData.append('current', this.address.current)

        const response = await fetchWithTimeout(`${BASE_URL}/address/add`, {
          method: 'POST',
          body: formData
        })

        const result = await response.json()
        if (result.success) {
          alert('地址添加成功！')
          this.$router.go(-1)
        } else {
          alert('地址添加失败，请重试！')
        }
      } catch (error) {
        console.error('地址添加失败:', error)
        alert('地址添加失败，请检查网络连接！')
      }
    }
  }
}
</script>

<style scoped>
.user-home {
  max-width: 400px;
  margin: 48px auto 0 auto;
  padding: 1em;
  background: #fff;
  min-height: 100vh;
}

.address-form {
  margin-top: 20px;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-size: 0.9em;
  color: #333;
}

.form-group input,
.form-group textarea {
  height: 2em;
  width: 100%;
  margin: 0;
  padding: 0;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 0.9em;
}

.form-group textarea {
  resize: none;
  height: 80px;
}

.submit-btn {
  width: 100%;
  padding: 10px;
  background: #1249d5;
  color: white;
  border: none;
  border-radius: 5px;
  font-size: 1em;
  cursor: pointer;
}

.submit-btn:hover {
  background: #0a3ba1;
}
</style>