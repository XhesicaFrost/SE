<template>
  <TopNav :navInfo="navInfo" />
  <div class="user-home">
    <div class="user-personal-edit">
      <form @submit.prevent="submitEdit">
        <div class="form-group">
          <label for="name">姓名</label>
          <input type="text" id="name" v-model="formData.name" placeholder="请输入姓名" />
        </div>
        <div class="form-group">
          <label for="phone">手机号</label>
          <input type="text" id="phone" v-model="formData.phone" placeholder="请输入手机号" />
        </div>
        <div class="form-group">
          <label for="image">头像</label>
          <input type="text" id="image" v-model="formData.image" placeholder="请输入头像" />
        </div>
        <button type="submit" class="submit-btn">保存</button>
      </form>
    </div>
  </div>
</template>

<script>
import TopNav from '@/components/topNav.vue'
import { mapState } from 'vuex';
import { BASE_URL, fetchWithTimeout } from '@/config.js'

export default {
  name: 'userHome',
  components: { TopNav },
  data() {
    return {
      navInfo: { 
        title: '编辑资料', 
        pageReturn: () => { this.$router.push('/user/personal') } 
      },
      formData: {
        id: 0,
        name: '',
        phone: '',
        image: ''
      }
    }
  },
  computed: {
    ...mapState('userStore', {
      userInfo: state => state.userInfo
    })
  },
  methods: {
    async submitEdit() {
      try {
        const token = localStorage.getItem('token')
        if (!token) {
          alert('请先登录')
          this.$router.push('/login')
          return
        }
        const formData = new FormData()
        formData.append('id', this.userInfo.userId)
        formData.append('name', this.formData.name)
        formData.append('phone', this.formData.phone)
        formData.append('image', this.formData.image)

        const response = await fetchWithTimeout(`${BASE_URL}/personal/edit`, {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`
          },
          body: formData
        });
        const result = await response.json();
        if (result.status === 'success') {
          alert('个人资料修改成功');
          this.$router.push('/user/personal');
        } else {
          alert('个人资料修改失败');
        }
      } catch (error) {
        console.error('修改个人资料时发生错误:', error);
        alert('网络错误，请稍后重试');
      }
    },
    createFormData(data) {
      const formData = new FormData();
      for (const key in data) {
        if (data[key]) {
          formData.append(key, data[key]);
        }
      }
      return formData;
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

.user-personal-edit {
  max-width: 400px;
  margin: 0 auto;
  padding: 1em;
  background: #fff;
  min-height: 100vh;
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

input {
  width: 100%;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.submit-btn {
  width: 100%;
  padding: 10px;
  background-color: #1249d5;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
}

.submit-btn:hover {
  background-color: #1010d0;
}
</style>