<template>
  <v-dialog v-model="model" max-width="600">
    <v-card>
      <v-card-title class="text-primary">
        <v-icon start>mdi-cog</v-icon>
        Конфигурация Xray
      </v-card-title>
      <v-card-text>
        <v-form ref="form" @submit.prevent="save">
          <v-text-field
            v-model="formData.host"
            label="Хост"
            required
            :rules="[rules.required]"
            variant="outlined"
            density="comfortable"
            class="mb-2"
          />
          <v-text-field
            v-model.number="formData.port"
            label="Порт"
            type="number"
            variant="outlined"
            density="comfortable"
            class="mb-2"
          />
          <v-text-field
            v-model="formData.realityHost"
            label="Reality хост"
            variant="outlined"
            density="comfortable"
            class="mb-2"
          />
          <v-text-field
            v-model.number="formData.realityPort"
            label="Reality порт"
            type="number"
            variant="outlined"
            density="comfortable"
            class="mb-2"
          />
          <v-text-field
            v-model="formData.realityNames"
            label="Reality names (через запятую)"
            variant="outlined"
            density="comfortable"
            class="mb-2"
          />
          <v-text-field
            v-model="formData.name"
            label="Имя сервера"
            variant="outlined"
            density="comfortable"
            class="mb-2"
          />
          <v-checkbox
            v-model="formData.clean"
            label="Очистить существующую конфигурацию"
            color="primary"
            density="comfortable"
          />
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn variant="text" @click="model = false">Отмена</v-btn>
        <v-btn color="primary" variant="flat" @click="save" :loading="xray.loadingKey === 'config'">
          Сохранить
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useXrayStore } from '@/stores/xray'

const model = defineModel<boolean>({ default: false })
const xray = useXrayStore()

const rules = {
  required: (v: string) => !!v || 'Обязательное поле',
}

const formData = ref({
  host: '',
  port: undefined as number | undefined,
  realityHost: '',
  realityPort: undefined as number | undefined,
  realityNames: '',
  name: '',
  clean: false,
})

const form = ref<any>(null)

watch(model, (open) => {
  if (open) {
    // Prefill from current status
    const s = xray.status
    // reality_address may be "host:port" — split on last ":"
    let realityHost = ''
    let realityPort: number | undefined = undefined
    if (s?.reality_address) {
      const lastColon = s.reality_address.lastIndexOf(':')
      if (lastColon !== -1) {
        realityHost = s.reality_address.slice(0, lastColon)
        const portStr = s.reality_address.slice(lastColon + 1)
        const parsed = parseInt(portStr, 10)
        realityPort = isNaN(parsed) ? undefined : parsed
      } else {
        realityHost = s.reality_address
      }
    }
    formData.value = {
      host: s?.server_host ?? '',
      port: s?.server_port ?? undefined,
      realityHost,
      realityPort,
      realityNames: s?.reality_names?.join(',') ?? '',
      name: s?.server_name ?? '',
      clean: false,
    }
  }
})

async function save() {
  const { valid } = await form.value?.validate()
  if (!valid) return

  const data = formData.value
  await xray.applyConfig({
    host: data.host,
    port: data.port,
    realityHost: data.realityHost || undefined,
    realityPort: data.realityPort,
    realityNames: data.realityNames
      ? data.realityNames.split(',').map(s => s.trim()).filter(Boolean)
      : undefined,
    name: data.name || undefined,
    clean: data.clean || undefined,
  })
  model.value = false
}
</script>
