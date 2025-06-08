<template>
  <TopNav :navInfo="navInfo" />
  <div class="user-address">
    
    <!-- 地址列表容器 -->
    <div class="address-container">
      <transition-group name="fade" tag="div" class="address-list">
        <!-- 地址项 -->
        <div v-for="address in addresses" :key="address.id" 
             class="address-item" :class="{selected: address.current}">
          <button class="set-default-btn" 
                  :disabled="address.current"
                  @click="setCurrentAddress(address.id)">
            <div class="selected-badge" v-if="address.current">当前使用</div>
            <div class="address-header">
              <div class="address-name">{{ address.name }}</div>
              <div class="address-phone">{{ address.phone }}</div>
            </div>
            <div class="address-detail">
              {{ address.fullAddress }}
            </div>
            <div class="address-actions">
              <button class="action-btn edit-btn" @click="handleEdit(address)">
                <i class="fas fa-edit"></i>编辑
              </button>
              <button class="action-btn delete-btn" @click="deleteAddress(address.id)">
                <i class="fas fa-trash-alt"></i>删除
              </button>
            </div>
          </button>
        </div>
        
        <!-- 空状态提示 -->
        <div v-if="addresses.length === 0" class="empty-state" key="empty">
          <div class="empty-icon">
            <i class="fas fa-map-marked-alt"></i>
          </div>
          <div class="empty-text">您还没有添加任何地址</div>
        </div>
      </transition-group>
    </div>
  </div>
</template>

<script>
import TopNav from '@/components/topNav.vue'
import { BASE_URL, fetchWithTimeout } from '@/config.js'
import { mapState } from 'vuex'

export default {
  name: 'userAddress',
  components: { TopNav },
  computed: {
    ...mapState('userStore', {
      userId: state => state.userId
    })
  },
  data() {
    return {
      addresses: [
      ],
      navInfo: { 
        title: '收货地址', 
        pageReturn: () => { this.$router.go(-1) },
        function: true,
        functionText: '新增地址',
        functionButton: () => this.$router.push('/user/address/add')
      }
    }
  },
  methods: {
    async fetchAddress() {
      try {
        const params = new URLSearchParams({ userId: this.userId }).toString()
        const response = await fetchWithTimeout(`${BASE_URL}/address?${params}`)
        const result = await response.json()
        if (result.success && result.code === 200 && result.data) {
          this.addresses = result.data
        } else {
          this.errorMessage = '店铺信息获取失败'
        }
      } catch (e) {
        this.errorMessage = '网络错误，店铺信息获取失败'
      }
    },
    async deleteAddress(id) {
      if (!confirm('确定要删除该地址吗？')) return
      try {
        const response = await fetchWithTimeout(`${BASE_URL}/address/delete`, {
          method: 'DELETE',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ id })
        })
        const result = await response.json()
        if (result.success) {
          this.fetchAddress()
        } else {
          alert('删除失败')
        }
      } catch (e) {
        alert('网络错误，删除失败')
      }
    },
    async setCurrentAddress(id) {
      try {
        const response = await fetchWithTimeout(`${BASE_URL}/address/current`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ id })
        })
        const result = await response.json()
        if (result.success) {
          this.fetchAddress()
        } else {
          alert('更改失败')
        }
      } catch (e) {
        alert('网络错误，更改失败')
      }
    },
    handleEdit(id) {
      this.$router.push(`/user/address/edit/${id}`)
    }
  },
  mounted() {
    this.fetchAddress()
  }
}
</script>

<style scoped>
.user-address {
  max-width: 400px;
  margin: 48px auto 0 auto;
  padding: 1em;
  background: #fff;
  min-height: 100vh;
}

/* 地址列表容器 */
.address-container {
  padding: 20px;
}

/* 地址列表 */
.address-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 地址项 */
.address-item {
  background: #fff;
  border-radius: 12px;
  padding: 18px;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.05);
  border: 1px solid #eee;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.set-default-btn {
  background: none;
  border: none;
  cursor: pointer;
}

.address-item.selected {
  border: 1px solid #4a6cf7;
  background: #f0f5ff;
  box-shadow: 0 3px 15px rgba(74, 108, 247, 0.15);
}

.selected-badge {
  position: absolute;
  top: 0;
  right: 0;
  background: #4a6cf7;
  color: white;
  padding: 4px 12px;
  font-size: 0.75rem;
  border-radius: 0 0 0 8px;
}

.address-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.address-name {
  color: #000;
  font-weight: 600;
  font-size: 1.1rem;
}

.address-phone {
  color: #666;
  font-size: 0.95rem;
}

.address-detail {
  color: #555;
  margin-bottom: 10px;
  font-size: 0.95rem;
  line-height: 1.5;
}

.address-actions {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  margin-top: 10px;
  padding-top: 12px;
  border-top: 1px dashed #eee;
}

.action-btn {
  background: none;
  border: none;
  color: #666;
  font-size: 0.9rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  transition: color 0.2s;
}

.edit-btn:hover {
  color: #4a6cf7;
}

.delete-btn:hover {
  color: #f5222d;
}

/* 空状态提示 */
.empty-state {
  text-align: center;
  padding: 50px 20px;
  color: #999;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 20px;
  color: #e8e8e8;
}

.empty-text {
  font-size: 1.1rem;
  margin-bottom: 25px;
}

/* 响应式调整 */
@media (max-width: 480px) {
  .address-item {
    padding: 15px;
  }
}

/* 动画效果 */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.5s, transform 0.5s;
}
.fade-enter, .fade-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style>