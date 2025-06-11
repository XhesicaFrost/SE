<template>
  <TopNav :navInfo="navInfo" />
  <div class="user-home">
    <form @submit.prevent="submitComment" class="comment-form">
      <div class="form-group">
        <label>评价类型</label>
        <select v-model="comment.type" required>
          <option value="">请选择</option>
          <option value="good">好评</option>
          <option value="bad">差评</option>
        </select>
      </div>
      <div class="form-group">
        <label for="detail">评论详情</label>
        <textarea id="detail" v-model="comment.detail" required></textarea>
      </div>
      <div class="form-group">
        <label for="image">订单图片（可选）</label>
        <input type="file" id="image" @change="handleImage" accept="image/*" />
        <!-- 新增：图片预览 -->
        <div v-if="imageUrl" class="preview-img">
          <img :src="imageUrl" alt="图片预览" />
        </div>
      </div>
      <button type="submit" class="submit-btn">提交评论</button>
    </form>
  </div>
</template>

<script>
import TopNav from '@/components/topNav.vue'
import { BASE_URL } from '@/config.js'

export default {
  name: 'UserCommentsAdd',
  components: { TopNav },
  data() {
    return {
      navInfo: {
        title: '添加订单评论',
        pageReturn: () => { this.$router.go(-1) }
      },
      comment: {
        type: '',
        detail: '',
        image: null
      },
      imageUrl: '' // 新增：图片预览URL
    }
  },
  methods: {
    handleImage(event) {
      const file = event.target.files[0]
      if (file) {
        // 类型校验
        if (!file.type.startsWith('image/')) {
          alert('请选择图片文件')
          return
        }
        // 大小校验（2MB）
        if (file.size > 2 * 1024 * 1024) {
          alert('图片大小不能超过2MB，请选择较小的图片')
          return
        }
        this.comment.image = file
        this.imageUrl = URL.createObjectURL(file)
      } else {
        this.comment.image = null
        this.imageUrl = ''
      }
    },
    async submitComment() {
      if (!this.comment.type || !this.comment.detail) {
        alert('请填写完整信息')
        return
      }
      try {
        const token = localStorage.getItem('token')
        if (!token) {
          alert('请先登录')
          this.$router.push('/login')
          return
        }
        const formData = new FormData()
        formData.append('userId', this.$store.state.userStore.userInfo.userId)
        formData.append('orderId', this.$route.params.orderId)
        formData.append('type', this.comment.type)
        formData.append('detail', this.comment.detail)
        if (this.comment.image) {
          formData.append('image', this.comment.image)
        }
        const response = await fetch(`${BASE_URL}/order/comment/add`, {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`
          },
          body: formData
        })
        const result = await response.json()
        if (result.success) {
          alert('评论提交成功！')
          this.$router.go(-1)
        } else {
          alert('评论提交失败，请重试！')
        }
      } catch (error) {
        console.error('评论提交失败:', error)
        alert('评论提交失败，请检查网络连接！')
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

.comment-form {
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
.form-group textarea,
.form-group select {
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
</style>