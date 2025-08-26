module.exports = {
  testEnvironment: 'jsdom',
  moduleFileExtensions: ['js', 'json', 'vue'],
  transform: {
    '^.+\\.vue$': 'vue-jest',
    '^.+\\.[jt]s$': 'babel-jest'
  },
  moduleNameMapper: {
    '^@/(.*)$': '<rootDir>/src/$1',
    // Mock 静态资源，防止如 jpg/png 在 Jest 中被当作 JS 解析
    '\\.(gif|ttf|eot|svg|png|jpg|jpeg|webp)$': '<rootDir>/tests/__mocks__/fileMock.js'
  },
  testMatch: ['**/tests/unit/**/*.spec.[jt]s'],
  transformIgnorePatterns: ['/node_modules/']
}