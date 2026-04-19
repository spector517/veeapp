<template>
  <div>
    <div class="d-flex align-center mb-4">
      <h2 class="text-h6 text-secondary">Клиенты</h2>
      <v-spacer />
      <v-btn color="primary" @click="showAdd = true" :disabled="!xray.configured">
        <v-icon start>mdi-account-plus</v-icon>
        Добавить
      </v-btn>
    </div>

    <v-alert v-if="xray.configChecked && !xray.configured" type="info" variant="tonal" class="mb-4">
      Сначала необходимо сконфигурировать сервис Xray.
    </v-alert>

    <v-card v-if="(xray.status?.clients?.clients ?? []).length === 0 && xray.configChecked && xray.configured" variant="tonal" class="mb-4 pa-4 text-center text-medium-emphasis">
      Клиенты не найдены
    </v-card>

    <v-card v-for="client in xray.status?.clients?.clients ?? []" :key="client.name" class="mb-3">
      <v-card-text class="d-flex align-center flex-wrap ga-2">
        <div class="flex-grow-1" style="min-width: 0;">
          <div class="text-subtitle-1 font-weight-medium text-primary">{{ client.name }}</div>
          <div class="text-caption text-medium-emphasis text-truncate d-none d-md-block">
            {{ client.url }}
          </div>
        </div>
        <div class="d-flex ga-1 flex-shrink-0">
          <v-btn
            icon
            size="small"
            variant="tonal"
            color="secondary"
            @click="copyUrl(client.url!)"
          >
            <v-icon>mdi-content-copy</v-icon>
            <v-tooltip activator="parent" location="top">Копировать URL</v-tooltip>
          </v-btn>
          <v-btn
            icon
            size="small"
            variant="tonal"
            color="secondary"
            @click="showQr(client)"
          >
            <v-icon>mdi-qrcode</v-icon>
            <v-tooltip activator="parent" location="top">QR-код</v-tooltip>
          </v-btn>
          <v-btn
            icon
            size="small"
            variant="tonal"
            color="error"
            :loading="xray.loadingKey === `removeClient-${client.name}`"
            @click="confirmDelete(client.name!)"
          >
            <v-icon>mdi-delete</v-icon>
            <v-tooltip activator="parent" location="top">Удалить</v-tooltip>
          </v-btn>
        </div>
      </v-card-text>
    </v-card>

    <!-- Add client modal -->
    <AddClientModal v-model="showAdd" />

    <!-- QR code dialog -->
    <v-dialog v-model="qrDialog" max-width="360">
      <v-card class="text-center pa-4">
        <v-card-title class="text-primary font-weight-bold">{{ qrClient?.name }}</v-card-title>
        <v-card-text>
          <QrcodeVue :value="qrClient?.url ?? ''" :size="280" :margin="2" level="M" />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="qrDialog = false">Закрыть</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Delete confirmation -->
    <v-dialog v-model="deleteDialog" max-width="400">
      <v-card>
        <v-card-title class="text-error">Удалить клиента</v-card-title>
        <v-card-text>
          Вы уверены, что хотите удалить клиента <strong>{{ deleteName }}</strong>?
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="deleteDialog = false">Отмена</v-btn>
          <v-btn color="error" variant="flat" @click="doDelete">Удалить</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import QrcodeVue from 'qrcode.vue'
import { useXrayStore } from '@/stores/xray'
import { useNotificationStore } from '@/stores/notification'
import type { Client } from 'veeapp-sdk'
import AddClientModal from '@/components/AddClientModal.vue'

const xray = useXrayStore()
const notify = useNotificationStore()

const showAdd = ref(false)
const qrDialog = ref(false)
const qrClient = ref<Client | null>(null)
const deleteDialog = ref(false)
const deleteName = ref('')

function copyUrl(url: string) {
  navigator.clipboard.writeText(url).then(
    () => notify.show('URL скопирован', 'success'),
    () => notify.show('Не удалось скопировать', 'error'),
  )
}

function showQr(client: Client) {
  qrClient.value = client
  qrDialog.value = true
}

function confirmDelete(name: string) {
  deleteName.value = name
  deleteDialog.value = true
}

async function doDelete() {
  deleteDialog.value = false
  await xray.removeClients([deleteName.value])
}
</script>
