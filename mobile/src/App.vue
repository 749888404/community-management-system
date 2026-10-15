<script setup>
import { onLaunch, onShow, onHide } from '@dcloudio/uni-app'

onLaunch(() => {
  console.log('App Launch')
})
onShow(() => {
  console.log('App Show')
})
onHide(() => {
  console.log('App Hide')
})
</script>

<style lang="scss">
/* 每个页面公共 css，含 uView 基础样式 */
@import 'uview-plus/index.scss';

page {
  background-color: #f5f6f8;
  font-size: 28rpx;
  color: #303133;
}
</style>
