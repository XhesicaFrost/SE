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
        <input type="text" id="phone" v-model="address.phone" required pattern="^1\d{10}$" />
      </div>
      <div class="form-group">
        <label for="fullAddress">详细地址</label>
        <textarea id="fullAddress" v-model="address.fullAddress" required></textarea>
      </div>
      <button type="submit" class="submit-btn">保存修改</button>
    </form>
  </div>
</template>

<script>
import TopNav from '@/components/topNav.vue'
import { BASE_URL, fetchWithTimeout } from '@/config.js'

export default {
  name: 'userAddressEdit',
  components: { TopNav },
  data() {
    return {
      navInfo: { 
        title: '编辑地址', 
        pageReturn: () => { this.$router.go(-1) } 
      },
      address: {
        id: null,
        name: '',
        phone: '',
        fullAddress: '',
        current: false
      }
    }
  },
  methods: {
    async fetchAddressInfo() {
      try {
        const params = new URLSearchParams({ userId: this.$store.state.userStore.userId }).toString()
        const response = await fetchWithTimeout(`${BASE_URL}/address?${params}`)
        const result = await response.json()
        if (result && Array.isArray(result)) {
          const address = result.find(addr => addr.id === this.$route.params.addressId)
          if (address) {
            this.address = { ...address }
          }
        }
      } catch (error) {
        console.error('获取地址信息失败:', error)
      }
    },
    async submitAddress() {
      try {
        const formData = new FormData()
        formData.append('id', this.address.id)
        formData.append('name', this.address.name)
        formData.append('phone', this.address.phone)
        formData.append('fullAddress', this.address.fullAddress)
        formData.append('current', this.address.current)

        const response = await fetchWithTimeout(`${BASE_URL}/address/edit`, {
          method: 'POST',
          body: formData
        })

        const result = await response.json()
        if (result.success) {
          alert('地址修改成功！')
          this.$router.go(-1)
        } else {
          alert('地址修改失败，请重试！')
        }
      } catch (error) {
        console.error('地址修改失败:', error)
        alert('地址修改失败，请检查网络连接！')
      }
    }
  },
  mounted() {
    this.fetchAddressInfo()
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