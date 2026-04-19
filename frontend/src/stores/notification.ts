import { defineStore } from 'pinia'
import { ref } from 'vue'

let nextId = 0

export interface Notification {
  id: number
  message: string
  type: 'success' | 'error' | 'info'
}

export const useNotificationStore = defineStore('notification', () => {
  const notifications = ref<Notification[]>([])

  function show(message: string, type: 'success' | 'error' | 'info' = 'info') {
    const id = nextId++
    notifications.value.push({ id, message, type })
    setTimeout(() => dismiss(id), 5000)
  }

  function dismiss(id: number) {
    const idx = notifications.value.findIndex(n => n.id === id)
    if (idx !== -1) notifications.value.splice(idx, 1)
  }

  return { notifications, show, dismiss }
})
