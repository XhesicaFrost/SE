<!-- filepath: front_end/src/components/topNav.vue -->
<template>
  <div class="top-bg"></div>
  <div class="top-nav">
    <div class="nav-return" v-if="!navInfo.noReturn">
      <button
        @click="runFunction(navInfo.pageReturn)"
      >
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
        @input="handleInput($event)"
        @keyup.enter="handleEnter"
        ref="searchInput"
      />
      <button @click="handleEnter">🔍</button>
    </div>
    <div class="nav-function" v-if="navInfo.function">
      <button
        @click="runFunction(navInfo.functionButton)"
      >
        <img :src="navInfo.functionImage" v-if="navInfo.functionImage != NULL"/>
        <p v-if="navInfo.functionText != NULL">{{ navInfo.functionText }}</p>
      </button>
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
    runFunction(func) {
      if (typeof func === 'function') {
        func();
      }
    },
    handleInput(event) {
      this.$emit('input', event.target.value);
    },
    handleEnter() {
      this.$emit('search', this.$refs.searchInput.value);
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
  outline: none;
  padding: 7px;
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
.nav-function {
  position: absolute;
  bottom: 0;
  right: 0px;
  width: 48px;
  height: 48px;
}
.nav-function button {
  background-color: transparent;
  border-radius: 35%;
  height: 100%;
  width: 100%;
  border: none;
  cursor: pointer;
  transition: background 0.2s;
  overflow: hidden;
}
.nav-function button:hover {
  background: rgba(192, 192, 208, 0.5);
}
.nav-function button img {
  position: absolute;
  margin: 0;
  padding: 0;
  top: 13%;
  left: 13%;
  width: 74%;
  height: 74%;
}
.nav-function button p {
  position: absolute;
  margin: 0;
  padding: 0;
  top: 11px;
  left: 10px;
  width: 28px;
  height: 28px;
  font-size: 14px;
  line-height: 14px;
  font-weight: bold;
  color: #1246a0;
}
</style>