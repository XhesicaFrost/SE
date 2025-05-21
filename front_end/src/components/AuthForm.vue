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
  padding: 0.75em 2.5em;
  border-radius: 10px;
  font-size: 1.2em;
  cursor: pointer;
}
.submit-btn:hover {
  background-color: #43a047; /* 深一点的绿色 */
}

input[type="text"],
input[type="email"],
input[type="password"] {
  display: inline-block;
  box-sizing: border-box;
  min-width: 0;
  width: 100%;
  line-height: 1.5;
  outline: none;
  border-radius: 3px;
  border: 1px solid #bfbfbf;
  background: #fff;
  font-family: -apple-system, BlinkMacSystemFont, Helvetica Neue, PingFang SC, Noto Sans, Noto Sans SC, Source Sans Pro, Source Han Sans, Segoe UI, Arial, Microsoft YaHei, WenQuanYi Micro Hei, sans-serif;
  font-size: 0.875em;
  padding: 0.3125em 1em;
  margin: 0;
  color: #404040;
  transition: border-color 0.2s;
}

input[type="text"]:focus,
input[type="email"]:focus,
input[type="password"]:focus {
  border-color: #3498db;
}

.form-group {
  margin-bottom: 1em;
}
</style>