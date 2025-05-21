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
    async handleLogin(userData) {
      console.log('LoginView:Login data:', userData)
      const success = await this.loginUser(userData)
      if (success.success) {
        console.log('LoginView:Login success:', success)
        this.$router.push('/login')
        //事实上，应该按照用户的类型进行跳转
      } else {
        console.log('LoginView:Login failed:', success)
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