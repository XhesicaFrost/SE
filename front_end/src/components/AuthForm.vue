<!--
  AuthForm.vue 组件功能说明

  本组件用于用户登录和注册表单的展示与数据收集。
  - 根据传入的 formType 属性（"login" 或 "register"）动态切换表单内容。
  - 注册时显示用户名、手机号、密码、用户类型选择（用户/骑手/商家）。
  - 登录时显示手机号、密码、用户类型选择。
  - 用户类型下拉框默认值为“用户”。
  - 表单提交时通过 submit 事件将表单数据传递给父组件。
  - 包含基础样式美化输入框和按钮。
-->
<template>
  <form @submit.prevent="handleSubmit">
    <div v-if="formType === 'register'" class="form-group">
      <label>Username:</label>
      <input v-model="formData.username" type="text" required />
    </div>
    <div class="form-group">
      <label>Phone:</label>
      <input v-model="formData.phone" type="tel" required pattern="^1\d{10}$" placeholder="请输入11位手机号" />
    </div>
    <div class="form-group">
      <label>Password:</label>
      <input v-model="formData.password" type="password" required />
    </div>
    <!-- 用户类型选择 -->
    <div class="form-group">
      <label>用户类型:</label>
      <select v-model="formData.userKind">
        <option value="user">用户</option>
        <option value="rider">骑手</option>
        <option value="seller">商家</option>
      </select>
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
        phone: '',      // 改为手机号
        password: '',
        userKind: 'user'
      }
    }
  },
  methods: {
    handleSubmit() {
      this.$emit('submit', { ...this.formData })
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
input[type="tel"],
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
input[type="tel"]:focus,
input[type="password"]:focus {
  border-color: #3498db;
}

.form-group {
  margin-bottom: 1em;
}
</style>

