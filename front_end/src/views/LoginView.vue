<!--
  LoginView.vue 组件说明

  本页面用于展示用户登录界面，包含：
  - 顶部 logo 和标题
  - 登录表单（支持用户类型选择）
  - 错误信息提示
  - 跳转到注册页面的链接
  - 页面底部支持信息
  页面切换时会自动清空错误信息。
-->

<template>
  <div class="login-view">
    <div class="login-header">
      <img src="@/assets/logo.jpg" alt="logo" class="login-logo" />
      <span class="login-title">饿了吗，那就登录吧</span>
    </div>
    <!-- 在模板中显示错误信息 -->
    <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
    <AuthForm form-type="login" @submit="handleLogin"/>
    <p>
      Don't have an account? 
      <router-link to="/register">Register here</router-link>
    </p>
    <div class="login-support">
      support by 藤田ことねファングールプ
    </div>
  </div>
</template>

<script>
import { mapState, mapActions, mapMutations } from 'vuex'
import AuthForm from '@/components/AuthForm.vue'

export default {
  name: 'LoginView',
  components: {
    AuthForm
  },
  computed: {
    ...mapState('userStore', ['errorMessage'])
  },
  methods: {
    ...mapActions('userStore', ['loginUser']),
    ...mapMutations('userStore', ['SET_ERROR']),
    /**
     * 处理登录表单提交
     * @param {Object} userData - 登录表单收集到的数据（包含邮箱、密码、用户类型等）
     * 调用 loginUser action 进行登录，成功后跳转页面，失败时自动显示错误信息
     */
    async handleLogin(userData) {
      console.log('LoginView:Login data:', userData)
      if (!userData || 
          typeof userData !== 'object' || 
          userData.isTrusted !== undefined ||  // 事件对象特有属性
          userData.type !== undefined ||       // 事件对象特有属性
          userData.target !== undefined) {     // 事件对象特有属性
          console.error('LoginView:接收到错误的数据格式:', userData)
        return
      }
      // 验证数据格式
      if (!userData || typeof userData !== 'object' || userData.isTrusted) {
        console.error('LoginView:接收到错误的数据格式:', userData)
        return
      }
      
      const success = await this.loginUser(userData)
      
      if (success.success) {
        console.log('LoginView:Login success:', success)
        
        // 修复：使用正确的用户类型判断
        const userKind = success.data?.userKind || userData.role || this.$store.state.userStore.userInfo.userKind
        console.log('LoginView:User kind:', userKind)
        
        switch (userKind) {
          case 'user':
            this.$router.push('/user')
            break
          case 'seller':
            this.$router.push('/seller')
            break
          case 'rider':
            this.$router.push('/rider')
            break
          case 'admin':
            this.$router.push('/admin')
            break
          default:
            console.warn('未知的用户类型:', userKind)
            this.$router.push('/user') // 默认跳转到用户页面
        }
      } else {
        console.log('LoginView:Login failed:', success)
        // 错误信息已经通过 commit('SET_ERROR') 设置到 store 中了
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