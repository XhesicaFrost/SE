<template>
  <div class="user-personal">
    <!-- 用户信息区域 -->
    <div class="user-header">
      <div class="avatar">
        <img :src="userInfo.image"/>
      </div>
      <div class="user-info">
        <div class="user-name">{{ userInfo.name }}</div>
        <div class="user-phone">{{ userInfo.phone }}</div>
        <button class="edit-btn" @click="goToEdit">编辑资料</button>
      </div>
    </div>

    <!-- 功能列表区域 -->
    <div class="function-list">
      <div class="function-item" @click="goToAddress">
        <i class="icon-location"></i>
        <span>我的地址</span>
        <i class="icon-arrow"></i>
      </div>
      <div class="function-item" @click="goToOrders">
        <i class="icon-order"></i>
        <span>历史订单</span>
        <i class="icon-arrow"></i>
      </div>
      <div class="function-item" @click="goToService">
        <i class="icon-service"></i>
        <span>服务设置</span>
        <i class="icon-arrow"></i>
      </div>
      <div class="function-item" @click="goToPayment">
        <i class="icon-payment"></i>
        <span>支付设置</span>
        <i class="icon-arrow"></i>
      </div>
    </div>

    <BottomNav :navItems="navItems" />
  </div>
</template>

<script>
import BottomNav from '@/components/bottomNav.vue'

export default {
  name: 'userPersonal',
  components: { BottomNav },
  data() {
    return {
      userInfo: {
        name: '123',
        phone: '123****1234',
        image: 'https://www.baidu.com/img/flexible/logo/pc/result.png'
      },
      navItems: [
        { label: '首页', action: () => { this.$router.push('/user') } },
        { label: '搜索', action: () => { this.$router.push('/user/search') } },
        { label: '购物车', action: () => { this.$router.push('/user/shopcart') } },
        { label: '我的', action: () => { this.$router.push('/user/personal') }, isActive: true }
      ]
    }
  },
  methods: {
    goToEdit() {
      this.$router.push('/user/editProfile');
    },
    goToAddress() {
      this.$router.push('/user/address');
    },
    goToOrders() {
      this.$router.push('/user/orders');
    },
    goToService() {
      this.$router.push('/user/service');
    },
    goToPayment() {
      alert("奶龙已经设置过了！");
      //this.$router.push('/user/payment');
    }
  }
}
</script>

<style scoped>
.user-personal {
  max-width: 400px;
  margin: 0 auto 36px auto;
  padding: 1em;
  background: #fff;
  min-height: 100vh;
}

/* 用户信息头部 */
.user-header {
  display: flex;
  align-items: center;
  padding: 20px 0;
  border-bottom: 1px solid #eee;
  margin-bottom: 20px;
}

.avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  overflow: hidden;
}

.user-info {
  flex: 1;
}

.user-name {
  font-size: 1.2rem;
  font-weight: 600;
  margin-bottom: 5px;
}

.user-phone {
  font-size: 0.9rem;
  color: #666;
  margin-bottom: 10px;
}

.edit-btn {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  padding: 5px 15px;
  border-radius: 15px;
  font-size: 0.85rem;
  cursor: pointer;
  transition: all 0.2s;
}

.edit-btn:hover {
  background: #e9ecef;
}

/* 功能列表 */
.function-list {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}

.function-item {
  display: flex;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #f8f9fa;
  cursor: pointer;
  transition: background 0.2s;
}

.function-item:hover {
  background: #f8f9fa;
}

.function-item i:first-child {
  margin-right: 12px;
  font-size: 18px;
  color: #6a11cb;
}

.function-item span {
  flex: 1;
  font-size: 1rem;
}

.icon-arrow {
  font-size: 14px;
  color: #999;
}
</style>