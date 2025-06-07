const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,
//   devServer: {
//     proxy: {
//       '/api': {
//         target: 'http://localhost:12345',
//         changeOrigin: true,
//         pathRewrite: {
//           '^/api': ''  // 将 /api 前缀移除
//         },
//         logLevel: 'debug'  // 显示代理日志
//       }
//     }
//   }
})