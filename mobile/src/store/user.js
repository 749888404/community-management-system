import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: uni.getStorageSync('token') || '',
    userInfo: uni.getStorageSync('userInfo') || null
  }),
  getters: {
    isLogin: (state) => !!state.token,
    role: (state) => (state.userInfo ? state.userInfo.role : ''),
    realName: (state) =>
      state.userInfo ? state.userInfo.realName || state.userInfo.username : ''
  },
  actions: {
    setUser(token, userInfo) {
      this.token = token
      this.userInfo = userInfo
      uni.setStorageSync('token', token)
      uni.setStorageSync('userInfo', userInfo)
    },
    logout() {
      this.token = ''
      this.userInfo = null
      uni.removeStorageSync('token')
      uni.removeStorageSync('userInfo')
    }
  }
})
