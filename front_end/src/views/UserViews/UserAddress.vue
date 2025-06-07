<template>
  <TopNav :navInfo="navInfo" />
  <div class="user-address">
    
    <!-- 地址列表容器 -->
    <div class="address-container">
      <div class="section-title">
        <i class="fas fa-map-marker-alt"></i>
        <span>我的收货地址</span>
      </div>
      
      <transition-group name="fade" tag="div" class="address-list">
        <!-- 地址项 -->
        <div v-for="address in addresses" :key="address.id" 
             class="address-item" :class="{selected: address.isDefault}">
          <div class="selected-badge" v-if="address.isDefault">当前使用</div>
          <div class="address-header">
            <div class="address-name">{{ address.name }}</div>
            <div class="address-phone">{{ address.phone }}</div>
          </div>
          <div class="address-detail">
            <span class="default-tag" v-if="address.isDefault">默认</span>
            {{ address.fullAddress }}
          </div>
          <div class="address-actions">
            <button class="action-btn set-default-btn" 
                    :disabled="address.isDefault"
                    @click="setDefaultAddress(address.id)">
              <i class="fas fa-check-circle"></i>
              {{ address.isDefault ? '默认地址' : '设为默认' }}
            </button>
            <button class="action-btn edit-btn" @click="editAddress(address)">
              <i class="fas fa-edit"></i>编辑
            </button>
            <button class="action-btn delete-btn" @click="deleteAddress(address.id)">
              <i class="fas fa-trash-alt"></i>删除
            </button>
          </div>
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

export default {
  name: 'userAddress',
  components: { TopNav },
  data() {
    return {
      addresses: [
        {
          id: 1,
          name: '张小明',
          phone: '138****5678',
          fullAddress: '北京市海淀区中关村大街27号科技大厦A座1208室',
          isDefault: true
        },
        {
          id: 2,
          name: '李思思',
          phone: '159****1234',
          fullAddress: '上海市浦东新区张江高科技园区亮秀路112号Y1座',
          isDefault: false
        },
        {
          id: 3,
          name: '张小明 (父母家)',
          phone: '138****5678',
          fullAddress: '天津市河西区友谊路32号银丰花园B区5栋302室',
          isDefault: false
        },
        {
          id: 4,
          name: '张小明 (公司)',
          phone: '138****5678',
          fullAddress: '广州市天河区珠江新城华夏路10号富力中心2206室',
          isDefault: false
        }
      ],
      navInfo: { 
        title: '收货地址', 
        pageReturn: () => { this.$router.go(-1) } 
      }
    }
  },
  methods: {
    goBack() {
      // 实际项目中这里会返回上一页
      alert('返回上一页');
    },
    setDefaultAddress(id) {
      this.addresses.forEach(address => {
        address.isDefault = address.id === id;
      });
    },
    editAddress(address) {
      // 实际项目中这里会打开编辑表单
      alert(`编辑地址: ${address.name}\n电话: ${address.phone}\n地址: ${address.fullAddress}`);
    },
    deleteAddress(id) {
      if (confirm('确定要删除这个地址吗？')) {
        const index = this.addresses.findIndex(addr => addr.id === id);
        if (index !== -1) {
          this.addresses.splice(index, 1);
        }
      }
    },
    addNewAddress() {
      // 实际项目中这里会打开添加地址表单
      alert('打开添加新地址表单');
    }
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

.section-title {
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: 15px;
  color: #444;
  display: flex;
  align-items: center;
}

.section-title i {
  margin-right: 8px;
  color: #4a6cf7;
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

.default-tag {
  display: inline-block;
  background: #e6f7ff;
  color: #1890ff;
  font-size: 0.8rem;
  padding: 3px 8px;
  border-radius: 4px;
  margin-right: 8px;
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

.action-btn i {
  margin-right: 5px;
}

.edit-btn:hover {
  color: #4a6cf7;
}

.delete-btn:hover {
  color: #f5222d;
}

.set-default-btn {
  color: #52c41a;
}

.set-default-btn:hover {
  color: #389e0d;
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