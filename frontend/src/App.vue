<template>
  <v-app>
    <v-app-bar color="surface" density="comfortable" elevation="2">
      <v-app-bar-title class="font-weight-bold">
        <span style="color: #42A5F5">V</span><span style="color: #a2a5a6">ee</span><span style="color: #FF9800">A</span><span style="color: #a2a5a6">pp</span><span v-if="xray.status?.server_name" style="color: #a2a5a6"> - {{ xray.status.server_name }}</span>
      </v-app-bar-title>

      <template #append>
        <v-btn
          color="primary"
          variant="flat"
          :disabled="!xray.restartRequired || xray.status?.server_status !== 'running'"
          :loading="xray.loadingKey === 'apply'"
          @click="xray.applyChanges()"
        >
          <v-icon start>mdi-check-circle</v-icon>
          Применить
        </v-btn>
      </template>
    </v-app-bar>

    <v-main>
      <v-tabs v-model="currentTab" color="primary" bg-color="surface" grow>
        <v-tab value="service" to="/service">
          <v-icon start>mdi-information-outline</v-icon>
          <span class="d-none d-sm-inline">Информация</span>
        </v-tab>
        <v-tab value="clients" to="/clients">
          <v-icon start>mdi-account-group</v-icon>
          <span class="d-none d-sm-inline">Клиенты</span>
        </v-tab>
        <v-tab value="routing" to="/routing">
          <v-icon start>mdi-directions-fork</v-icon>
          <span class="d-none d-sm-inline">Маршрутизация</span>
        </v-tab>
        <v-tab value="outbounds" to="/outbounds">
          <v-icon start>mdi-server-network</v-icon>
          <span class="d-none d-sm-inline">Исходящие подключения</span>
        </v-tab>
      </v-tabs>

      <v-container fluid class="pa-4">
        <router-view />
      </v-container>
    </v-main>

    <!-- Loading overlay: scrim only -->
    <v-overlay
      :model-value="xray.loading"
      persistent
      scrim="black"
      :opacity="0.35"
    />

    <!-- Notifications -->
    <div class="notification-stack">
      <v-snackbar
        v-for="n in notifications.notifications"
        :key="n.id"
        :model-value="true"
        :color="n.type === 'error' ? 'error' : n.type === 'success' ? 'success' : 'info'"
        :timeout="-1"
        location="bottom right"
        multi-line
      >
        {{ n.message }}
        <template #actions>
          <v-btn variant="text" @click="notifications.dismiss(n.id)">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </template>
      </v-snackbar>
    </div>

    <!-- Footer -->
    <v-footer app color="surface" class="text-caption text-medium-emphasis justify-center py-2">
      VeeApp (v{{ appVersion }})
    </v-footer>
  </v-app>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, watch, watchEffect } from 'vue'
import { useRoute } from 'vue-router'
import { useXrayStore } from '@/stores/xray'
import { useNotificationStore } from '@/stores/notification'

const xray = useXrayStore()
const notifications = useNotificationStore()
const route = useRoute()
const appVersion = __APP_VERSION__

const currentTab = ref(route.name as string)

watch(() => route.name, (name) => {
  if (name) currentTab.value = name as string
})

watchEffect(() => {
  document.title = xray.status?.server_name
    ? `VeeApp - ${xray.status.server_name}`
    : 'VeeApp'
})

function onBeforeUnload(e: BeforeUnloadEvent) {
  if (xray.loading) {
    e.preventDefault()
  }
}

onMounted(() => {
  window.addEventListener('beforeunload', onBeforeUnload)
  xray.refreshAll()
})

onBeforeUnmount(() => {
  window.removeEventListener('beforeunload', onBeforeUnload)
})
</script>

<style>
.notification-stack {
  position: fixed;
  bottom: 16px;
  right: 16px;
  z-index: 9999;
  display: flex;
  flex-direction: column-reverse;
  gap: 8px;
}

.v-card--variant-outlined {
  border-color: rgb(var(--v-theme-secondary)) !important;
}

.v-dialog .v-card {
  background-color: rgb(var(--v-theme-background)) !important;
  border: none !important;
}

/* Red asterisk on required fields — Vuetify 3 renders it as <span aria-hidden="true"> */
.v-field-label span[aria-hidden="true"] {
  color: rgb(var(--v-theme-error)) !important;
}
</style>
