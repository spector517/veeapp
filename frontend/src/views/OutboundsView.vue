<template>
  <div>
    <div class="d-flex align-center mb-4">
      <h2 class="text-h6 text-secondary">Исходящие подключения</h2>
      <v-spacer />
      <v-btn color="primary" @click="showAdd = true" :disabled="!xray.configured">
        <v-icon start>mdi-plus</v-icon>
        Добавить
      </v-btn>
    </div>

    <v-alert v-if="xray.configChecked && !xray.configured" type="info" variant="tonal" class="mb-4">
      Сначала необходимо сконфигурировать сервис Xray.
    </v-alert>

    <v-card
      v-if="outbounds.length === 0 && xray.configChecked && xray.configured"
      variant="tonal"
      class="mb-4 pa-4 text-center text-medium-emphasis"
    >
      Исходящие подключения не найдены
    </v-card>

    <v-card v-for="ob in outbounds" :key="ob.name" class="mb-3">
      <v-card-text class="d-flex align-center ga-2">
        <v-icon color="secondary">mdi-server-network</v-icon>
        <div class="flex-grow-1">
          <div class="d-flex align-center ga-1">
            <template v-if="editingOutbound === ob.name">
              <v-text-field
                v-model="editedOutboundName"
                density="compact"
                variant="underlined"
                hide-details
                autofocus
                style="max-width: 300px;"
                @keydown.enter="saveOutboundName(ob.name!)"
                @blur="saveOutboundName(ob.name!)"
              />
            </template>
            <template v-else>
              <div class="text-subtitle-1 font-weight-medium text-primary">{{ ob.name }}</div>
              <v-btn
                v-if="!protectedOutbounds.includes(ob.name ?? '')"
                icon
                size="x-small"
                variant="text"
                color="secondary"
                @click="startEditOutbound(ob)"
              >
                <v-icon size="small">mdi-pencil</v-icon>
                <v-tooltip activator="parent" location="top">Переименовать</v-tooltip>
              </v-btn>
            </template>
          </div>
          <div class="text-caption text-medium-emphasis">{{ ob.address ?? '—' }}</div>
        </div>
        <v-btn
          icon
          size="small"
          variant="tonal"
          color="error"
          :disabled="protectedOutbounds.includes(ob.name ?? '')"
          :loading="xray.loadingKey === `removeOutbound-${ob.name}`"
          @click="confirmDelete(ob.name!)"
        >
          <v-icon>mdi-delete</v-icon>
          <v-tooltip activator="parent" location="top">
            {{ protectedOutbounds.includes(ob.name ?? '') ? 'Системное подключение' : 'Удалить' }}
          </v-tooltip>
        </v-btn>
      </v-card-text>
    </v-card>

    <AddOutboundModal v-model="showAdd" />

    <v-dialog v-model="deleteDialog" max-width="400">
      <v-card>
        <v-card-title class="text-error">Удалить подключение</v-card-title>
        <v-card-text>
          Вы уверены, что хотите удалить подключение <strong>{{ deleteName }}</strong>?
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
import { ref, computed } from 'vue'
import { useXrayStore } from '@/stores/xray'
import type { Outbound } from 'veeapp-sdk'
import AddOutboundModal from '@/components/AddOutboundModal.vue'

const xray = useXrayStore()
const outbounds = computed(() => xray.status?.outbounds ?? [])
const protectedOutbounds = ['direct', 'blackhole', 'dns']
const showAdd = ref(false)
const deleteDialog = ref(false)
const deleteName = ref('')

const editingOutbound = ref<string | null>(null)
const editedOutboundName = ref('')

function startEditOutbound(ob: Outbound) {
  editingOutbound.value = ob.name!
  editedOutboundName.value = ob.name!
}

async function saveOutboundName(originalName: string) {
  const newName = editedOutboundName.value.trim()
  editingOutbound.value = null
  if (newName && newName !== originalName) {
    await xray.changeOutbound(originalName, { newName })
  }
}

function confirmDelete(name: string) {
  deleteName.value = name
  deleteDialog.value = true
}

async function doDelete() {
  deleteDialog.value = false
  await xray.removeOutbound(deleteName.value)
}
</script>
