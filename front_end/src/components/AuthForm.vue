<template>
  <form @submit.prevent="handleSubmit">
    <div v-if="formType === 'register'" class="form-group">
      <label>Username:</label>
      <input v-model="formData.username" type="text" required />
    </div>
    <div class="form-group">
      <label>Email:</label>
      <input v-model="formData.email" type="email" required />
    </div>
    <div class="form-group">
      <label>Password:</label>
      <input v-model="formData.password" type="password" required />
    </div>
    <button type="submit" class="submit-btn">
      {{ formType === 'login' ? 'Login' : 'Register' }}
    </button>
  </form>
</template>

<script>
export default {
  name: 'AuthForm',
  props: {
    formType: {
      type: String,
      default: 'login',
      validator: value => ['login', 'register'].includes(value)
    }
  },
  data() {
    return {
      formData: {
        username: '',
        email: '',
        password: ''
      }
    }
  },
  methods: {
    handleSubmit() {
      const payload = this.formType === 'login' 
        ? { email: this.formData.email, password: this.formData.password }
        : this.formData
        
      this.$emit('submit', payload)
    }
  }
}
</script>

<style>
.submit-btn {
  background-color: #4caf50; /* 绿色 */
  color: #fff;
  border: none;
  padding: 0.5em 1.5em;
  border-radius: 4px;
  cursor: pointer;
}
.submit-btn:hover {
  background-color: #43a047; /* 深一点的绿色 */
}
</style>