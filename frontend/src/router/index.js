import { createRouter, createWebHistory } from 'vue-router'
import Layout from '../views/Layout.vue'

const routes = [
  { path: '/login', name: 'Login', component: () => import('../views/Login.vue') },
  {
    path: '/',
    component: Layout,
    redirect: '/user',
    children: [
      { path: 'user', name: 'User', component: () => import('../views/user/UserList.vue'), meta: { title: '用户管理' } },
      { path: 'community', name: 'Community', component: () => import('../views/community/CommunityList.vue'), meta: { title: '小区管理' } },
      { path: 'person', name: 'Person', component: () => import('../views/person/PersonList.vue'), meta: { title: '居民管理' } },
      { path: 'disabled', name: 'Disabled', component: () => import('../views/disabled/DisabledList.vue'), meta: { title: '残疾人管理' } },
      { path: 'file', name: 'File', component: () => import('../views/file/FileList.vue'), meta: { title: '文件管理' } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：未登录跳转登录页
router.beforeEach((to, from, next) => {
  if (to.path !== '/login' && !localStorage.getItem('token')) {
    next('/login')
  } else {
    next()
  }
})

export default router
