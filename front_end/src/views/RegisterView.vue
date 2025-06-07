<!--
  RegisterView.vue 组件说明

  本页面用于展示用户注册界面，包含：
  - 顶部 logo 和标题
  - 注册表单（支持用户名、邮箱、密码、用户类型选择：用户/骑手/商家，默认商家）
  - 错误信息提示
  - 跳转到登录页面的链接
  - 页面底部支持信息
  页面切换时会自动清空错误信息。
-->
<template>
  <div class="register-view">
    <div class="login-header">
      <img src="@/assets/logo.jpg" alt="logo" class="login-logo" />
      <span class="login-title">饿了吗，那就注册吧</span>
    </div>
    <AuthForm form-type="register" @submit="handleRegister" />
    <!-- 在模板中显示错误信息 -->
    <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
    <p>
      Already have an account? 
      <router-link to="/login">Login here</router-link>
    </p>
    <div class="login-support">
      support by 藤田ことねファングールプ
    </div>
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex'
import AuthForm from '@/components/AuthForm.vue'

export default {
  name: 'RegisterView',
  components: {
    AuthForm
  },
  computed: {
    ...mapState('userStore', ['errorMessage'])
  },
  methods: {
    ...mapActions('userStore', ['registerUser']),
      /**
     * 处理注册表单提交
     * @param {Object} userData - 注册表单收集到的数据（包含用户名、邮箱、密码、用户类型等）
     * 调用 registerUser action 进行注册，成功后跳转到登录页面，失败时自动显示错误信息
     */
    async handleRegister(userData) {
      console.log('RegisterView:Register data:', userData)
      if (!userData || 
          typeof userData !== 'object' || 
          userData.isTrusted !== undefined ||  // 事件对象特有属性
          userData.type !== undefined ||       // 事件对象特有属性
          userData.target !== undefined) {     // 事件对象特有属性
          console.error('RegisterView:接收到错误的数据格式:', userData)
        return
      }
      // 验证数据格式
      if (!userData || typeof userData !== 'object' || userData.isTrusted) {
        console.error('接收到错误的数据格式:', userData)
        return
      }
      const success = await this.registerUser(userData)
      if (success.success) {
        console.log('RegisterView:Register success:', success)
        this.$router.push('/login')
      }else{
        console.log('RegisterView:Register failed:', success)
      }
    }
  },
  beforeRouteLeave(to, from, next) {
    this.$store.commit('userStore/SET_ERROR', '')
    next()
  }
}
</script>

<style scoped>
.login-header {
  display: flex;
  align-items: center;
  margin-bottom: 1.5em;
}
.login-logo {
  width: 48px;
  height: 48px;
  object-fit: contain;
  margin-right: 1em;
}
.login-title {
  font-size: 2em;
  font-weight: bold;
  color: #333;
}
.login-support {
  margin-top: 2em;
  text-align: center;
  font-size: 0.85em;
  color: #888;
}
</style>

