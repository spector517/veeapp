<template>
  <v-dialog v-model="model" max-width="600">
    <v-card>
      <v-card-title class="text-primary">
        <v-icon start>mdi-server-network</v-icon>
        Добавить исходящее подключение
      </v-card-title>
      <v-card-text>
        <v-btn-toggle v-model="mode" mandatory color="primary" class="mb-4" density="comfortable">
          <v-btn value="params">По параметрам</v-btn>
          <v-btn value="url">По ссылке</v-btn>
        </v-btn-toggle>

        <!-- By params -->
        <v-form v-if="mode === 'params'" ref="paramsForm" @submit.prevent="saveParams">
          <v-text-field
            v-model="params.name"
            label="Имя"
            required
            :rules="[rules.required]"
            variant="outlined"
            density="comfortable"
            class="mb-2"
          />
          <v-text-field
            v-model="params.address"
            label="Адрес"
            required
            :rules="[rules.required]"
            variant="outlined"
            density="comfortable"
            class="mb-2"
          />
          <v-text-field
            v-model="params.uuid"
            label="UUID"
            required
            :rules="[rules.required]"
            variant="outlined"
            density="comfortable"
            class="mb-2"
          />
          <v-text-field
            v-model="params.sni"
            label="SNI"
            required
            :rules="[rules.required]"
            variant="outlined"
            density="comfortable"
            class="mb-2"
          />
          <v-text-field
            v-model="params.shortId"
            label="Short ID"
            required
            :rules="[rules.required]"
            variant="outlined"
            density="comfortable"
            class="mb-2"
          />
          <v-text-field
            v-model.number="params.port"
            label="Порт"
            type="number"
            required
            :rules="[rules.required, rules.port]"
            variant="outlined"
            density="comfortable"
            class="mb-2"
          />
          <v-text-field
            v-model="params.password"
            label="Пароль"
            required
            :rules="[rules.required]"
            variant="outlined"
            density="comfortable"
            class="mb-2"
          />
          <v-text-field
            v-model="params.spiderX"
            label="SpiderX"
            required
            :rules="[rules.required]"
            variant="outlined"
            density="comfortable"
            class="mb-2"
          />
          <v-select
            v-model="params.fingerprint"
            :items="fingerprintOptions"
            label="Fingerprint"
            required
            :rules="[rules.required]"
            variant="outlined"
            density="comfortable"
          />
        </v-form>

        <!-- By URL -->
        <v-form v-else ref="urlForm" @submit.prevent="saveUrl">
          <v-text-field
            v-model="urlData.url"
            label="VLESS URL"
            required
            :rules="[rules.required]"
            variant="outlined"
            density="comfortable"
            class="mb-2"
          />
          <v-text-field
            v-model="urlData.name"
            label="Имя (необязательно)"
            variant="outlined"
            density="comfortable"
          />
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn variant="text" @click="model = false">Отмена</v-btn>
        <v-btn
          color="primary"
          variant="flat"
          @click="mode === 'params' ? saveParams() : saveUrl()"
          :loading="xray.loadingKey === 'addOutbound' || xray.loadingKey === 'addOutboundFromUrl'"
        >
          Добавить
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useXrayStore } from '@/stores/xray'
import { AddOutboundRequestFingerprintEnum } from 'veeapp-sdk'

const fingerprintOptions = Object.values(AddOutboundRequestFingerprintEnum)

const model = defineModel<boolean>({ default: false })
const xray = useXrayStore()

const mode = ref<'params' | 'url'>('params')

const rules = {
  required: (v: string | number | undefined) => (v !== undefined && v !== '' && v !== null) || 'Обязательное поле',
  port: (v: number | undefined) => (!!v && v > 0 && v <= 65535) || 'Укажите порт (1-65535)',
}

const params = ref({
  name: '',
  address: '',
  uuid: '',
  sni: '',
  shortId: '',
  port: undefined as number | undefined,
  password: '',
  spiderX: '',
  fingerprint: '',
})

const urlData = ref({
  url: '',
  name: '',
})

const paramsForm = ref<any>(null)
const urlForm = ref<any>(null)

watch(model, (open) => {
  if (open) {
    mode.value = 'params'
    params.value = { name: '', address: '', uuid: '', sni: '', shortId: '', port: undefined, password: '', spiderX: '', fingerprint: '' as string }
    urlData.value = { url: '', name: '' }
  }
})

async function saveParams() {
  const { valid } = await paramsForm.value?.validate()
  if (!valid) return
  const p = params.value
  await xray.addOutbound({
    name: p.name,
    address: p.address,
    uuid: p.uuid,
    sni: p.sni,
    shortId: p.shortId,
    port: p.port!,
    password: p.password,
    spiderX: p.spiderX,
    fingerprint: p.fingerprint as AddOutboundRequestFingerprintEnum,
  })
  model.value = false
}

async function saveUrl() {
  const { valid } = await urlForm.value?.validate()
  if (!valid) return
  await xray.addOutboundFromUrl({
    url: urlData.value.url,
    name: urlData.value.name || undefined,
  })
  model.value = false
}
</script>
