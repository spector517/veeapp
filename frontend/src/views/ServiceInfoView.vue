<template>
  <div>
    <!-- Unconfigured banner -->
    <v-alert
      v-if="xray.configChecked && !xray.configured"
      type="warning"
      variant="tonal"
      prominent
      class="mb-4"
    >
      <v-alert-title>Сервис Xray не сконфигурирован</v-alert-title>
      Требуется начальная конфигурация для работы сервиса.
      <template #append>
        <v-btn color="primary" variant="flat" @click="showConfig = true">
          Настроить
        </v-btn>
      </template>
    </v-alert>

    <!-- Status card -->
    <v-card v-if="xray.configured" class="mb-4">
      <v-card-title class="text-secondary">
        <v-icon start>mdi-information-outline</v-icon>
        Статус сервиса
      </v-card-title>
      <v-card-text>
        <v-table v-if="xray.status" density="comfortable">
          <colgroup>
            <col style="width: 250px;" />
            <col />
          </colgroup>
          <tbody>
            <tr v-if="xray.status.server_name">
              <td class="text-medium-emphasis">Имя сервера</td>
              <td>{{ xray.status.server_name }}</td>
            </tr>
            <tr>
              <td class="text-medium-emphasis">Версия VeePeeNET</td>
              <td>{{ xray.status.veepeenet_version ?? '—' }} (build {{ xray.status.veepeenet_build ?? '—' }})</td>
            </tr>
            <tr>
              <td class="text-medium-emphasis">Версия Xray</td>
              <td>{{ xray.status.xray_version ?? '—' }}</td>
            </tr>
            <tr>
              <td class="text-medium-emphasis">Статус сервиса</td>
              <td>
                <v-chip
                  :color="statusColor"
                  size="small"
                  label
                >
                  {{ statusLabel }}
                </v-chip>
              </td>
            </tr>
            <tr>
              <td class="text-medium-emphasis">Публичный адрес сервера</td>
              <td>{{ xray.status.server_host ?? '—' }}</td>
            </tr>
            <tr>
              <td class="text-medium-emphasis">Порт сервиса</td>
              <td>{{ xray.status.server_port ?? '—' }}</td>
            </tr>
            <tr>
              <td class="text-medium-emphasis">Reality address</td>
              <td>{{ xray.status.reality_address ?? '—' }}</td>
            </tr>
            <tr>
              <td class="text-medium-emphasis">Reality names</td>
              <td>{{ xray.status.reality_names?.join(', ') ?? '—' }}</td>
            </tr>
          </tbody>
        </v-table>
        <v-skeleton-loader v-else type="table-tbody" />
      </v-card-text>
      <v-card-actions class="flex-wrap ga-2 pa-4">
        <v-btn
          color="success"
          variant="tonal"
          class="flex-grow-1 flex-sm-grow-0"
          :disabled="isRunning || !xray.configured || (xray.status?.clients?.clients?.length ?? 0) === 0"
          :loading="xray.loadingKey === 'start'"
          @click="xray.startService()"
        >
          <v-icon start>mdi-play</v-icon>
          Запустить
        </v-btn>
        <v-btn
          color="error"
          variant="tonal"
          class="flex-grow-1 flex-sm-grow-0"
          :disabled="!isRunning"
          :loading="xray.loadingKey === 'stop'"
          @click="xray.stopService()"
        >
          <v-icon start>mdi-stop</v-icon>
          Остановить
        </v-btn>
        <v-btn
          color="warning"
          variant="tonal"
          class="flex-grow-1 flex-sm-grow-0"
          :disabled="!isRunning || (xray.status?.clients?.clients?.length ?? 0) === 0"
          :loading="xray.loadingKey === 'restart'"
          @click="xray.restartService()"
        >
          <v-icon start>mdi-restart</v-icon>
          Перезапустить
        </v-btn>
        <v-btn
          color="primary"
          variant="tonal"
          class="w-100 w-sm-auto ml-sm-auto mt-1 mt-sm-0"
          @click="showConfig = true"
        >
          <v-icon start>mdi-cog</v-icon>
          Настроить
        </v-btn>
      </v-card-actions>
    </v-card>

    <!-- Updates card -->
    <v-card v-if="xray.configured">
      <v-card-title class="text-secondary">
        <v-icon start>mdi-update</v-icon>
        Обновления
      </v-card-title>
      <v-card-text>
        <v-row align="center">
          <v-col cols="12" sm="auto">
            <v-btn
              color="secondary"
              variant="tonal"
              @click="xray.updateGeodata()"
              :loading="xray.loadingKey === 'geodata'"
            >
              <v-icon start>mdi-earth</v-icon>
              Обновить Geodata
            </v-btn>
          </v-col>
          <v-col cols="12" sm="auto" class="flex-grow-1">
            <v-btn
              v-if="xray.versions.length === 0"
              color="secondary"
              variant="tonal"
              :loading="xray.loadingKey === 'versions'"
              @click="xray.fetchVersions()"
            >
              <v-icon start>mdi-format-list-bulleted</v-icon>
              Получить список версий
            </v-btn>
            <v-row v-else align="center" no-gutters>
              <v-col>
                <v-select
                  v-model="selectedVersion"
                  :items="xray.versions"
                  label="Версия Xray"
                  density="compact"
                  hide-details
                  variant="outlined"
                />
              </v-col>
              <v-col cols="auto" class="ml-2">
                <v-btn
                  color="secondary"
                  variant="tonal"
                  :disabled="!selectedVersion"
                  :loading="xray.loadingKey === 'updateXray'"
                  @click="confirmUpdateXray"
                >
                  <v-icon start>mdi-download</v-icon>
                  Обновить Xray
                </v-btn>
              </v-col>
            </v-row>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>

    <!-- Config modal -->
    <ConfigModal v-model="showConfig" />

    <!-- UpdateXray confirmation dialog -->
    <v-dialog v-model="updateXrayDialog" max-width="480">
      <v-card>
        <v-card-title class="text-warning">
          <v-icon start>mdi-alert</v-icon>
          Внимание!
        </v-card-title>
        <v-card-text>
          Самостоятельное обновление Xray может привести к неработоспособности сервера. Продолжить?
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="updateXrayDialog = false">Отмена</v-btn>
          <v-btn color="warning" variant="flat" @click="doUpdateXray">Обновить</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useXrayStore } from '@/stores/xray'
import ConfigModal from '@/components/ConfigModal.vue'

const xray = useXrayStore()

const showConfig = ref(false)
const selectedVersion = ref<string | null>(null)
const updateXrayDialog = ref(false)

function confirmUpdateXray() {
  updateXrayDialog.value = true
}

async function doUpdateXray() {
  updateXrayDialog.value = false
  await xray.updateXray(selectedVersion!.value!)
}

const isRunning = computed(() => xray.status?.server_status === 'running')

const statusLabel = computed(() => {
  switch (xray.status?.server_status) {
    case 'running':
      return xray.restartRequired
        ? 'Запущен (требуется применение новых настроек)'
        : 'Запущен'
    case 'stopped': return 'Остановлен'
    default: return xray.status?.server_status ?? 'Неизвестно'
  }
})

const statusColor = computed(() => {
  if (xray.status?.server_status === 'running') {
    return xray.restartRequired ? 'warning' : 'success'
  }
  return 'error'
})
</script>
