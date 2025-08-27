module.exports = {
  testEnvironment: 'jsdom',
  moduleFileExtensions: ['js', 'json', 'vue'],
  transform: {
    '^.+\\.vue$': 'vue-jest',
    '^.+\\.[jt]s$': 'babel-jest'
  },
  moduleNameMapper: {
    // 先匹配静态资源
    '\\.(gif|ttf|eot|svg|png|jpg|jpeg|webp)$': '<rootDir>/tests/__mocks__/fileMock.js',
    '^@/(.*)$': '<rootDir>/src/$1'
  },
  testMatch: ['**/tests/unit/**/*.spec.[jt]s'],
  transformIgnorePatterns: ['/node_modules/']
}