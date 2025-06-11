<template>
  <TopNav :navInfo="navInfo" />
  <div class="admin-comments-review">
    <!-- 评论列表 -->
    <div class="comments-list">
      <div 
        class="comment-item" 
        v-for="comment in pagedComments" 
        :key="comment.id"
      >
        <div class="comment-header">
          <div class="user-info">
            <span class="username">{{ comment.username }}</span>
            <span class="time">{{ formatTime(comment.createTime) }}</span>
          </div>
          <div class="rating" :class="comment.type">
            {{ comment.type==='good' ? '好评' : '差评' }}
          </div>
        </div>
        <div class="comment-content">
          {{ comment.content }}
        </div>
        <div class="comment-images" v-if="comment.image">
          <img :src="getImageUrl(comment.image)" alt="订单图片" />
        </div>
        <div class="comment-actions">
          <button 
            class="approve-btn" 
            @click="approveComment(comment.id)"
            v-if="comment.status === '待审核'"
          >
            通过
          </button>
          <button 
            class="reject-btn" 
            @click="rejectComment(comment.id)"
            v-if="comment.status === '待审核'"
          >
            拒绝
          </button>
          <span class="status" :class="comment.status">
            {{ comment.status }}
          </span>
        </div>
      </div>
    </div>

    <!-- 分页栏 -->
    <div class="pagination-bar">
      <button :disabled="page === 1" @click="changePage(page - 1)">上一页</button>
      <span>第 {{ page }} 页 / 共 {{ totalPages }} 页</span>
      <button :disabled="page === totalPages" @click="changePage(page + 1)">下一页</button>
      <span>
        跳转到
        <input type="number" v-model.number="jumpPage" min="1" :max="totalPages" style="width: 50px;" />
        <button @click="changePage(jumpPage)">GO</button>
      </span>
    </div>
  </div>
</template>

<script>
import { BASE_URL, fetchWithTimeout } from '@/config.js'
import TopNav from '@/components/topNav.vue'

export default {
  name: 'AdminCommentsReview',
  components: { TopNav },
  data() {
    return {
      comments: [
      ],
      page: 1,
      pageSize: 10,
      jumpPage: 1,
      navInfo: { 
        title: '评论审核', 
        pageReturn: () => { this.$router.go(-1) } 
      }
    }
  },
  computed: {
    pagedComments() {
      const start = (this.page - 1) * this.pageSize
      return this.comments.slice(start, start + this.pageSize)
    },
    totalPages() {
      return Math.ceil(this.comments.length / this.pageSize) || 1
    }
  },
  methods: {
    getImageUrl(img) {
      console.log('原始图片数据:', img)
      if (!img) {
        console.log('图片数据为空')
        return ''
      }
      const base64Url = `data:image/jpeg;base64,${img}`
      console.log('生成的base64 URL:', base64Url)
      return base64Url
    },
    formatTime(time) {
      if (!time) return ''
      const date = new Date(time)
      return date.toLocaleString()
    },
    async fetchComments() {
      try {
        const shopId = this.$route.params.shopId
        const response = await fetchWithTimeout(`${BASE_URL}/admin/shop/comments/${shopId}`)
        const result = await response.json()
        if (result.code === 200 && Array.isArray(result.data)) {
          this.comments = result.data
        } else {
          this.comments = []
        }
      } catch (e) {
        console.error('获取评论列表失败:', e)
        this.comments = []
      }
    },
    async approveComment(commentId) {
      try {
        const response = await fetchWithTimeout(`${BASE_URL}/admin/comment/approve`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ commentId })
        })
        const result = await response.json()
        if (result.code === 200) {
          this.fetchComments()
        } else {
          alert('操作失败: ' + (result.message || '未知错误'))
        }
      } catch (e) {
        alert('网络错误，操作失败')
      }
    },
    async rejectComment(commentId) {
      try {
        const response = await fetchWithTimeout(`${BASE_URL}/admin/comment/reject`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ commentId })
        })
        const result = await response.json()
        if (result.code === 200) {
          this.fetchComments()
        } else {
          alert('操作失败: ' + (result.message || '未知错误'))
        }
      } catch (e) {
        alert('网络错误，操作失败')
      }
    },
    changePage(p) {
      if (p < 1) p = 1
      if (p > this.totalPages) p = this.totalPages
      this.page = p
      this.jumpPage = p
    }
  },
  mounted() {
    this.fetchComments()
  }
}
</script>

<style scoped>
.admin-comments-review {
  max-width: 500px;
  margin: 48px auto 36px auto;
  padding: 1em;
  background: #fff;
  min-height: 100vh;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 1em;
}

.comment-item {
  background: #f9f9f9;
  border-radius: 8px;
  padding: 1em;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5em;
}

.user-info {
  display: flex;
  flex-direction: column;
}

.username {
  font-weight: bold;
  font-size: 1.1em;
}

.time {
  color: #666;
  font-size: 0.9em;
}

.rating {
  font-weight: bold;
}

.rating.good {
  color: #12d012;
}

.rating.bad {
  color: #cc1e1e;
}

.comment-content {
  margin: 0.5em 0;
  line-height: 1.5;
}

.comment-images {
  position: relative;
  left: 0;
  top: 0;
  margin: 0;
  padding: 0;
  height: 100px;
  width: 100px;
  overflow: hidden;
}

.comment-images img {
  border-radius: 4px;
  object-fit: cover;
}

.comment-actions {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 0.5em;
  margin-top: 0.5em;
}

.approve-btn, .reject-btn {
  padding: 0.4em 0.8em;
  border: none;
  border-radius: 4px;
  font-size: 0.9em;
  cursor: pointer;
  transition: background 0.2s;
}

.approve-btn {
  background: #4caf50;
  color: white;
}

.approve-btn:hover {
  background: #388e3c;
}

.reject-btn {
  background: #f44336;
  color: white;
}

.reject-btn:hover {
  background: #9b0101;
}

.status {
  padding: 0.2em 0.5em;
  border-radius: 4px;
  font-size: 0.9em;
}

.status.待审核 {
  background: #ffd700;
  color: #000;
}

.status.已通过 {
  background: #4caf50;
  color: white;
}

.status.已拒绝 {
  background: #f44336;
  color: white;
}

.pagination-bar {
  margin-top: 1.5em;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.8em;
  flex-wrap: wrap;
  font-size: 0.95em;
}

.pagination-bar button {
  padding: 0.4em 0.8em;
  background: #f0f0f0;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
}

.pagination-bar button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination-bar input {
  width: 50px;
  padding: 0.3em;
  border: 1px solid #ddd;
  border-radius: 4px;
  text-align: center;
}
</style>