<template>
  <v-dialog v-model="model" max-width="400">
    <v-card>
      <v-card-title class="text-primary">
        <v-icon start>mdi-account-plus</v-icon>
        Добавить клиента
      </v-card-title>
      <v-card-text>
        <v-form ref="form" @submit.prevent="save">
          <v-text-field
            v-model="clientName"
            label="Имя клиента"
            required
            :rules="[rules.required]"
            variant="outlined"
            density="comfortable"
            autofocus
          />
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn variant="text" @click="model = false">Отмена</v-btn>
        <v-btn color="primary" variant="flat" @click="save" :loading="xray.loadingKey === 'addClients'">
          Добавить
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

const clientName = ref('')
const form = ref<any>(null)

const rules = {
  required: (v: string) => !!v?.trim() || 'Обязательное поле',
}

watch(model, (open) => {
  if (open) clientName.value = ''
})

async function save() {
  const { valid } = await form.value?.validate()
  if (!valid) return
  await xray.addClients([clientName.value.trim()])
  model.value = false
}
</script>
