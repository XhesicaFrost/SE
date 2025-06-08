<template>
  <TopNav :navInfo="navInfo" />
  <div class="admin-comments-review">
    <div class="comments-list">
      <div 
        class="comment-item" 
        v-for="comment in comments" 
        :key="comment.id"
      >
        <div class="comment-content">
          <p><strong>{{ comment.userName }}</strong>: {{ comment.content }}</p>
          <p class="comment-status">状态: {{ comment.status }}</p>
        </div>
        <div class="comment-actions">
          <button class="approve-btn" @click="approveComment(comment.id)">通过</button>
          <button class="reject-btn" @click="rejectComment(comment.id)">拒绝</button>
        </div>
      </div>
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
      comments: [],
      navInfo: { title: '评论审核', noReturn: false }
    }
  },
  methods: {
    async fetchComments() {
      try {
        const params = new URLSearchParams({ shopId: this.$router.params.shopId }).toString()
        const response = await fetchWithTimeout(`${BASE_URL}/admin/comments/${params}`)
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
        const response = await fetchWithTimeout(`${BASE_URL}/admin/comments/approve`, {
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
        const response = await fetchWithTimeout(`${BASE_URL}/admin/comments/reject`, {
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
  margin: 48px auto 0 auto;
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
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1em;
  border: 1px solid #ddd;
  border-radius: 8px;
  background: #f9f9f9;
}

.comment-content {
  flex: 1;
}

.comment-status {
  color: #666;
  font-size: 0.9em;
}

.comment-actions {
  display: flex;
  gap: 0.5em;
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
  background: #d32f2f;
}
</style>