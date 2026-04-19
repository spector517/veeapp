<template>
  <v-dialog v-model="model" max-width="600">
    <v-card>
      <v-card-title class="text-primary">
        <v-icon start>mdi-directions-fork</v-icon>
        {{ isNew ? 'Добавить правило' : 'Редактировать правило' }}
      </v-card-title>
      <v-card-text>
        <v-form ref="form" @submit.prevent="save">
          <v-text-field
            v-model="formData.name"
            label="Имя правила"
            required
            :rules="[rules.required]"
            variant="outlined"
            density="comfortable"
            class="mb-2"
          />
          <v-select
            v-model="formData.outbound"
            :items="outbounds"
            label="Исходящее подключение"
            required
            :rules="[rules.required]"
            variant="outlined"
            density="comfortable"
            class="mb-2"
          />
          <v-text-field
            v-model="formData.domain"
            label="Домены (через запятую)"
            variant="outlined"
            density="comfortable"
            class="mb-2"
          />
          <v-text-field
            v-model="formData.ip"
            label="IP (через запятую)"
            variant="outlined"
            density="comfortable"
            class="mb-2"
          />
          <v-text-field
            v-model="formData.port"
            label="Порты (через запятую)"
            variant="outlined"
            density="comfortable"
            class="mb-2"
          />
          <v-select
            v-model="formData.protocol"
            :items="protocolOptions"
            label="Протокол"
            variant="outlined"
            density="comfortable"
            clearable
            class="mb-2"
          />
          <v-text-field
            v-model.number="formData.priority"
            label="Приоритет"
            type="number"
            variant="outlined"
            density="comfortable"
          />
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn variant="text" @click="model = false">Отмена</v-btn>
        <v-btn color="primary" variant="flat" @click="save">
          {{ isNew ? 'Добавить' : 'Сохранить' }}
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import type { RoutingRule } from 'veeapp-sdk'
import { AddRoutingRuleRequestProtocolEnum } from 'veeapp-sdk'

const protocolOptions = Object.values(AddRoutingRuleRequestProtocolEnum)

const props = defineProps<{
  rule: RoutingRule | null
  outbounds: string[]
}>()

const emit = defineEmits<{
  save: [payload: {
    isNew: boolean
    originalName?: string
    name: string
    outbound: string
    domain: string
    ip: string
    port: string
    protocol: string
    priority: number | undefined
  }]
}>()

const model = defineModel<boolean>({ default: false })

const isNew = computed(() => !props.rule)

const formData = ref({
  name: '',
  outbound: '',
  domain: '',
  ip: '',
  port: '',
  protocol: '',
  priority: undefined as number | undefined,
})

const form = ref<any>(null)

const rules = {
  required: (v: string) => !!v?.trim() || 'Обязательное поле',
}

watch(model, (open) => {
  if (open) {
    if (props.rule) {
      formData.value = {
        name: props.rule.name ?? '',
        outbound: props.rule.outbound_name ?? '',
        domain: props.rule.domains?.join(',') ?? '',
        ip: props.rule.ips?.join(',') ?? '',
        port: '',
        protocol: '',
        priority: props.rule.priority,
      }
    } else {
      formData.value = {
        name: '',
        outbound: '',
        domain: '',
        ip: '',
        port: '',
        protocol: '',
        priority: undefined,
      }
    }
  }
})

async function save() {
  const { valid } = await form.value?.validate()
  if (!valid) return
  emit('save', {
    isNew: isNew.value,
    originalName: props.rule?.name ?? undefined,
    ...formData.value,
  })
}
</script>
