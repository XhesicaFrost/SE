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
    async handleRegister(userData) {
      console.log('RegisterView:Register data:', userData)
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