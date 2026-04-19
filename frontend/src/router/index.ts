import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/service',
    },
    {
      path: '/service',
      name: 'service',
      component: () => import('@/views/ServiceInfoView.vue'),
    },
    {
      path: '/clients',
      name: 'clients',
      component: () => import('@/views/ClientsView.vue'),
    },
    {
      path: '/routing',
      name: 'routing',
      component: () => import('@/views/RoutingView.vue'),
    },
    {
      path: '/outbounds',
      name: 'outbounds',
      component: () => import('@/views/OutboundsView.vue'),
    },
  ],
})

export default router
