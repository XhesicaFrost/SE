<template>
  <div class="login-view">
    <h1>Login Page</h1>
    <AuthForm form-type="login" />
    <p>
      Don't have an account? 
      <router-link to="/register">Register here</router-link>
    </p>
  </div>
</template>

<script>
import AuthForm from '@/components/AuthForm.vue'

export default {
  name: 'LoginView',
  components: {
    AuthForm
  }
}
</script>