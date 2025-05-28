<template>
  <div>
    <!-- 选项卡按钮 -->
    <div class="tabs">
      <button
        v-for="(tab, index) in tabs"
        :key="index"
        @click="setActiveTab(index)"
        :class="{ 'active': activeTab === index }"
      >
        {{ tab.name }}
      </button>
    </div>

    <!-- 选项卡内容 -->
    <div class="content">
      <div v-for="(tab, index) in tabs" :key="index" v-show="activeTab === index">
        {{ tab.content }}
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      activeTab: 1, // 默认激活第一个选项卡
      tabs: [
        { name: "首页", content: "这是首页内容" },
        { name: "产品", content: "这是产品介绍" },
        { name: "关于", content: "关于我们" }
      ]
    };
  },
  methods: {
    setActiveTab(index) {
      this.activeTab = index; // 点击时更新激活状态
    }
  }
};
</script>

<style>
button.active {
  background-color: #42b983;
  color: white;
  border-bottom: 2px solid #35495e;
}
button.active:hover {
  background-color: #42b983 !important;     /* 强制保持 active 状态的背景色 */
  transform: none !important;         /* 禁用动画 */
  box-shadow: none !important;        /* 移除阴影 */
  cursor: default !important;         /* 保持默认光标 */
}
button {
  padding: 10px 20px;
  border: none;
  cursor: pointer;
  margin-right: 5px;
  transition: background 0.3s;
}
button:hover {
  background-color: #66ccff;
}
</style>