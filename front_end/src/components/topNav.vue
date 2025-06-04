<!-- filepath: front_end/src/components/topNav.vue -->
<template>
  <div class="top-bg"></div>
  <div class="top-nav">
    <div class="nav-return" v-if="!navInfo.noReturn">
      <button
        class="return-bottom"
        @click="onClickReturn(navInfo)">
        ←
      </button>
    </div>
    <div class="nav-title" v-if="!navInfo.search">
      <p>{{ navInfo.title }}</p>
    </div>
    <div class="nav-search" v-if="navInfo.search">
      <input
        type="search"
        :value="value" 
        @input="$emit('input', $event.target.value)"
        @keyup.enter="handleEnter"
      />
      <button @click="handleEnter">🔍</button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'TopNav',
  props: {
    navInfo: {
      type: Object,
      required: true,
      default: () => ({})
    },
    value: {
      type: String,
      default: ''
    }
  },
  methods: {
    onClickReturn(info) {
      if (typeof info.pageReturn === 'function') {
        info.pageReturn();
      }
    },
    handleEnter() {
      this.$emit('search',this.value);
    }
  },
  mounted() {
    console.log('TopNav mounted', this.navInfo);
  }   
}
</script>

<style scoped>
.top-bg {
  display: flex;
  background: #eff6f0;
  height: 20px;
  position: fixed;
  left: 50%;
  top: 0;
  transform: translateX(-50%);
  width: 100%;
  max-width: 400px;
  box-sizing: border-box;
}
.top-nav {
  display: flex;
  background: rgba(240,240,240,1);
  height: 48px;
  position: fixed;
  left: 50%;
  top: 20px;
  transform: translateX(-50%);
  width: 100%;
  max-width: 400px;
  box-sizing: border-box;
}
.nav-return {
  position: absolute;
  bottom: 0;
  height: 48px;
  width: 48px;
}
.nav-return button {
  background-color: transparent;
  border-radius: 35%;
  height: 100%;
  width: 100%;
  text-align: center;
  border: none;
  cursor: pointer;
  font-size: 17px;
  transition: background 0.2s;
  color: #3498db;
}
.nav-return button:hover {
  background: rgba(192, 192, 208, 0.5);
}
.nav-title {
  position: absolute;
  left: 100px;
  bottom: 0;
  height: 48px;
  width: 200px;
}
.nav-title.centered-title {
  left: 0;
  width: 100%;
}
.nav-title p {
  position: absolute;
  height: 20px;
  width: 200px;
  top: 9px;
  margin: 0 auto;
  padding: 0;
  font-size: 20px;
  text-align: center;
}
.nav-search {
  position: absolute;
  left: 70px;
  bottom: 0;
  height: 48px;
  width: 260px;
  place-items: center;
}
.nav-search input {
  position: absolute;
  height: 26px;
  top: 14px;
  left: 0;
  width: 232px;
  border: none;
}
.nav-search button {
  background-color: #fff;
  position: absolute;
  height: 26px;
  width: 28px;
  top: 14px;
  right: 0;
  padding: 0;
  border: none;
}
</style>