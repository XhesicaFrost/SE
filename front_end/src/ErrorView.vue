<template>
  <div class="error-page">
    <div class="error-content">
      <div class="logo-container">
        <img src="@/assets/logo.jpg" alt="Logo" class="logo" />
      </div>
      
      <div class="error-message">
        <h2>奶龙偷走了你的页面</h2>
      </div>
      
      <div class="countdown">
        {{ countdown }}s 后回到上一页面
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ErrorView',
  data() {
    return {
      countdown: 3,
      timer: null
    }
  },
  mounted() {
    this.startCountdown()
  },
  beforeUnmount() {
    if (this.timer) {
      clearInterval(this.timer)
    }
  },
  methods: {
    startCountdown() {
      this.timer = setInterval(() => {
        this.countdown--
        
        if (this.countdown <= 0) {
          clearInterval(this.timer)
          this.$router.back()
        }
      }, 1000)
    }
  }
}
</script>

<style scoped>
.error-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #a8e6a3 0%, #4caf50 100%);
  font-family: 'Arial', sans-serif;
}

.error-content {
  text-align: center;
  background: rgba(255, 255, 255, 0.95);
  padding: 3em;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(76, 175, 80, 0.3);
  max-width: 400px;
  animation: fadeIn 0.8s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.logo-container {
  margin-bottom: 2em;
}

.logo {
  width: 120px;
  height: 120px;
  object-fit: contain;
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-10px);
  }
  60% {
    transform: translateY(-5px);
  }
}

.error-message h2 {
  color: #333;
  font-size: 1.5em;
  font-weight: bold;
  margin: 0 0 1.5em 0;
  letter-spacing: 1px;
}

.countdown {
  color: #666;
  font-size: 1.1em;
  font-weight: 500;
  animation: pulse 1s infinite;
}

@keyframes pulse {
  0% {
    opacity: 1;
  }
  50% {
    opacity: 0.6;
  }
  100% {
    opacity: 1;
  }
}

/* 响应式设计 */
@media (max-width: 480px) {
  .error-content {
    padding: 2em;
    margin: 1em;
  }
  
  .logo {
    width: 100px;
    height: 100px;
  }
  
  .error-message h2 {
    font-size: 1.3em;
  }
  
  .countdown {
    font-size: 1em;
  }
}
</style>