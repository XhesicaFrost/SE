<template>
  <div class="register-view">
    <h1>Register Page</h1>
    <AuthForm form-type="register" @submit="handleRegister" />
    <p class="error-message">{{ errorMessage }}</p>
    <p>
      Already have an account? 
      <router-link to="/login">Login here</router-link>
    </p>
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
      const success = await this.registerUser(userData)
      if (success) {
        this.$router.push('/login')
      }
    }
  }
}
</script>