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
        console.log('🚀 提交个人资料修改:', this.formData)
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
          formData.append('avatar', this.avatarFile)
        }

        const response = await fetchWithTimeout(`${BASE_URL}/personal/edit`, {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`
          },
          body: formData
        });
        
        const result = await response.json();
        console.log('📋 服务器响应:', result)
        
        if (result.status === 'success' || result.code === 200) {
          alert('个人资料修改成功');
          
          // ✅ 构建完整的更新数据
          const updatedUserInfo = {
            userName: this.formData.name,
            userPhone: this.formData.phone
          }
          
          // ✅ 处理头像更新 - 关键修改
          if (result.data && result.data.avatarUrl) {
            // 情况1：服务器返回新的头像URL
            updatedUserInfo.image = result.data.avatarUrl
            updatedUserInfo.userImage = result.data.avatarUrl  // 保持两个字段同步
            console.log('✅ 使用服务器返回的头像URL:', result.data.avatarUrl)
          } else if (this.avatarFile && this.avatarUrl) {
            // 情况2：用户上传了新头像，使用本地预览URL
            updatedUserInfo.image = this.avatarUrl
            updatedUserInfo.userImage = this.avatarUrl
            console.log('✅ 使用本地预览头像URL:', this.avatarUrl)
          }
          // 情况3：没有新头像，保持原有头像（不需要额外处理）
          
          console.log('🔄 更新用户信息:', updatedUserInfo)
          
          // ✅ 更新 Vuex 中的用户信息
          await this.$store.dispatch('userStore/updateUserInfo', updatedUserInfo);
          
          this.$router.push('/user/personal');
        } else {
          alert('个人资料修改失败: ' + (result.message || '未知错误'));
        }
      } catch (error) {
        console.error('修改个人资料时发生错误:', error);
        alert('网络错误，请稍后重试');
      }
    },

    // ✅ 修正：初始化用户数据
    initUserData() {
      console.log('🔧 初始化用户数据:', this.userInfo)
      
      if (this.userInfo) {
        this.formData.name = this.userInfo.userName || ''
        this.formData.phone = this.userInfo.userPhone || ''
        
        // ✅ 显示现有头像（兼容两种字段名）
        if (this.userInfo.image) {
          this.avatarUrl = this.userInfo.image
        } else if (this.userInfo.userImage) {
          this.avatarUrl = this.userInfo.userImage
        }
        
        console.log('✅ 表单数据初始化完成:', this.formData)
        console.log('🖼️ 头像URL:', this.avatarUrl)
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