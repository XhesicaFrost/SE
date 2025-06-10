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
        <!-- ✅ 修改：改为文件上传，参考 SellerShopEdit -->
        <div class="form-group">
          <label for="avatar">头像</label>
          <input type="file" accept="image/*" @change="onImageChange" />
          <!-- ✅ 新增：图片预览功能 -->
          <div v-if="avatarUrl" class="preview-img">
            <img :src="avatarUrl" alt="头像预览" />
          </div>
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
      },
      // ✅ 新增：图片上传相关数据
      avatarFile: null,    // 保存选择的文件对象
      avatarUrl: '',       // 预览图片URL
    }
  },
  computed: {
    ...mapState('userStore', {
      userInfo: state => state.userInfo
    })
  },
  methods: {
    // ✅ 新增：图片选择处理方法（参考 SellerShopEdit）
    onImageChange(e) {
      const file = e.target.files[0]
      if (file) {
        // 验证文件类型
        if (!file.type.startsWith('image/')) {
          alert('请选择图片文件')
          return
        }
        
        // 验证文件大小（限制为2MB）
        if (file.size > 2 * 1024 * 1024) {
          alert('图片大小不能超过2MB，请选择较小的图片')
          return
        }

        // 保存文件对象和创建预览URL
        this.avatarFile = file
        this.avatarUrl = URL.createObjectURL(file)
      }
    },

    // ✅ 修改：提交方法，参考 SellerShopEdit 的实现
    async submitEdit() {
      try {
        const token = localStorage.getItem('token')
        if (!token) {
          alert('请先登录')
          this.$router.push('/login')
          return
        }

        // ✅ 使用 FormData（与 SellerShopEdit 相同）
        const formData = new FormData()
        formData.append('id', this.userInfo.userId)
        formData.append('name', this.formData.name)
        formData.append('phone', this.formData.phone)
        
        // ✅ 关键：只有选择了新图片才添加到表单
        if (this.avatarFile) {
          formData.append('avatar', this.avatarFile)  // 使用文件对象，不是字符串
        }

        const response = await fetchWithTimeout(`${BASE_URL}/personal/edit`, {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`
            // ✅ 不设置 Content-Type，让浏览器自动处理 FormData
          },
          body: formData  // 直接发送 FormData
        });
        
        const result = await response.json();
        if (result.status === 'success' || result.code === 200) {
          alert('个人资料修改成功');
          // ✅ 更新 Vuex 中的用户信息
          this.$store.dispatch('userStore/updateUserInfo', {
            userName: this.formData.name,
            userPhone: this.formData.phone
          });
          
          this.$router.push('/user/personal');
        } else {
          alert('个人资料修改失败: ' + (result.message || '未知错误'));
        }
      } catch (error) {
        console.error('修改个人资料时发生错误:', error);
        alert('网络错误，请稍后重试');
      }
    },

    // ✅ 新增：初始化用户数据
    initUserData() {
      if (this.userInfo) {
        this.formData.name = this.userInfo.username || ''
        this.formData.phone = this.userInfo.phone || ''
        // 如果有现有头像，显示预览
        if (this.userInfo.avatarUrl) {
          this.avatarUrl = this.userInfo.avatarUrl
        }
      }
    }
  },

  // ✅ 新增：组件挂载时初始化数据
  mounted() {
    this.initUserData()
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

/* ✅ 新增：图片预览样式（参考 SellerShopEdit） */
.preview-img {
  margin-top: 0.5em;
}

.preview-img img {
  max-width: 100%;
  max-height: 120px;
  border-radius: 6px;
  border: 1px solid #eee;
  object-fit: cover;
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