<template>
  <div>
    <!-- Domain strategy -->
    <v-card class="mb-4">
      <v-card-text>
        <v-select
          v-model="domainStrategy"
          :items="strategies"
          item-title="title"
          item-value="value"
          label="Доменная стратегия"
          variant="outlined"
          density="comfortable"
          hide-details
          :disabled="rules.length === 0"
          @update:model-value="onStrategyChange"
        />
      </v-card-text>
    </v-card>

    <!-- Rules header -->
    <div class="d-flex align-center mb-4">
      <h2 class="text-h6 text-secondary">Правила маршрутизации</h2>
      <v-spacer />
      <v-btn color="primary" @click="openAdd" :disabled="!xray.configured">
        <v-icon start>mdi-plus</v-icon>
        Добавить правило
      </v-btn>
    </div>

    <v-card v-if="rules.length === 0" variant="tonal" class="mb-4 pa-4 text-center text-medium-emphasis">
      Правила маршрутизации не найдены
    </v-card>

    <!-- Rule cards -->
    <v-card v-for="rule in rules" :key="rule.name" class="mb-3">
      <v-card-text>
        <div class="d-flex align-center flex-wrap ga-2 mb-2">
          <template v-if="editingRuleName === rule.name">
            <v-text-field
              v-model="editedRuleName"
              density="compact"
              variant="underlined"
              hide-details
              autofocus
              class="flex-grow-0"
              style="max-width: 300px;"
              @keydown.enter="saveRuleName(rule.name!)"
              @blur="saveRuleName(rule.name!)"
            />
          </template>
          <template v-else>
            <div class="text-subtitle-1 font-weight-medium text-primary">{{ rule.name }}</div>
            <v-btn icon size="x-small" variant="text" color="secondary" @click="startEditName(rule)">
              <v-icon size="small">mdi-pencil</v-icon>
              <v-tooltip activator="parent" location="top">Переименовать</v-tooltip>
            </v-btn>
          </template>
          <template v-if="editingRulePriority === rule.name">
            <v-text-field
              v-model.number="editedPriority"
              type="number"
              density="compact"
              variant="underlined"
              hide-details
              autofocus
              class="flex-grow-0"
              style="max-width: 120px;"
              prefix="Приоритет:"
              @keydown.enter="saveRulePriority(rule.name!)"
              @blur="saveRulePriority(rule.name!)"
            />
          </template>
          <template v-else>
            <v-chip size="small" color="secondary" label>
              Приоритет: {{ rule.priority ?? '—' }}
            </v-chip>
            <v-btn icon size="x-small" variant="text" color="secondary" @click="startEditPriority(rule)">
              <v-icon size="small">mdi-pencil</v-icon>
              <v-tooltip activator="parent" location="top">Изменить приоритет</v-tooltip>
            </v-btn>
          </template>
          <v-spacer />
          <v-btn icon size="small" variant="tonal" color="error"
            :loading="xray.loadingKey === `removeRule-${rule.name}`"
            @click="confirmDelete(rule.name!)">
            <v-icon>mdi-delete</v-icon>
            <v-tooltip activator="parent" location="top">Удалить</v-tooltip>
          </v-btn>
        </div>
        <v-row dense>
          <v-col v-if="rule.domains?.length" cols="12" sm="6">
            <div class="text-caption text-medium-emphasis">Домены</div>
            <v-chip v-for="d in rule.domains" :key="d" size="small" class="mr-1 mt-1">{{ d }}</v-chip>
          </v-col>
          <v-col v-if="rule.ips?.length" cols="12" sm="6">
            <div class="text-caption text-medium-emphasis">IP</div>
            <v-chip v-for="ip in rule.ips" :key="ip" size="small" class="mr-1 mt-1">{{ ip }}</v-chip>
          </v-col>
        </v-row>
        <div class="mt-2">
          <span class="text-caption text-medium-emphasis">Исходящее подключение: </span>
          <v-chip size="small" color="primary" label>{{ rule.outbound_name ?? '—' }}</v-chip>
        </div>
      </v-card-text>
    </v-card>

    <!-- Add modal -->
    <EditRoutingRuleModal
      v-model="ruleDialog"
      :rule="null"
      :outbounds="(xray.status?.outbounds ?? []).map(o => o.name ?? '')"
      @save="onSaveRule"
    />

    <!-- Delete confirmation -->
    <v-dialog v-model="deleteDialog" max-width="400">
      <v-card>
        <v-card-title class="text-error">Удалить правило</v-card-title>
        <v-card-text>
          Вы уверены, что хотите удалить правило <strong>{{ deleteName }}</strong>?
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
import type { RoutingRule } from 'veeapp-sdk'
import {
  RoutingDomainStrategyRequestStrategyEnum,
  AddRoutingRuleRequestProtocolEnum,
} from 'veeapp-sdk'
import EditRoutingRuleModal from '@/components/EditRoutingRuleModal.vue'

const xray = useXrayStore()

const strategies = [
  { title: 'AsIs', value: RoutingDomainStrategyRequestStrategyEnum.AsIs },
  { title: 'IPIfNonMatch', value: RoutingDomainStrategyRequestStrategyEnum.IpIfNotMatch },
  { title: 'IPOnDemand', value: RoutingDomainStrategyRequestStrategyEnum.IpOnDemand },
]
const domainStrategy = computed({
  get: () => xray.status?.routing?.domain_strategy ?? RoutingDomainStrategyRequestStrategyEnum.AsIs,
  set: () => {},
})
const rules = computed(() => xray.status?.routing?.rules ?? [])

const ruleDialog = ref(false)

const editingRuleName = ref<string | null>(null)
const editedRuleName = ref('')
const editingRulePriority = ref<string | null>(null)
const editedPriority = ref<number | undefined>(undefined)

const deleteDialog = ref(false)
const deleteName = ref('')

function onStrategyChange(val: string) {
  xray.setDomainStrategy(val as RoutingDomainStrategyRequestStrategyEnum)
}

function openAdd() {
  ruleDialog.value = true
}

function startEditName(rule: RoutingRule) {
  editingRuleName.value = rule.name!
  editedRuleName.value = rule.name!
}

async function saveRuleName(originalName: string) {
  const newName = editedRuleName.value.trim()
  editingRuleName.value = null
  if (newName && newName !== originalName) {
    await xray.renameRule(originalName, { newName })
  }
}

function startEditPriority(rule: RoutingRule) {
  editingRulePriority.value = rule.name!
  editedPriority.value = rule.priority
}

async function saveRulePriority(ruleName: string) {
  const rule = rules.value.find(r => r.name === ruleName)
  const originalPriority = rule?.priority
  const newPriority = editedPriority.value
  editingRulePriority.value = null
  if (newPriority !== undefined && newPriority !== originalPriority) {
    await xray.setRulePriority(ruleName, { priority: newPriority })
  }
}

function confirmDelete(name: string) {
  deleteName.value = name
  deleteDialog.value = true
}

async function doDelete() {
  deleteDialog.value = false
  await xray.removeRoutingRule(deleteName.value)
}

interface SavePayload {
  isNew: boolean
  originalName?: string
  name: string
  outbound: string
  domain: string
  ip: string
  port: string
  protocol: string
  priority: number | undefined
}

async function onSaveRule(payload: SavePayload) {
  const toArr = (s: string) => s ? s.split(',').map(v => v.trim()).filter(Boolean) : undefined
  await xray.addRoutingRule({
    name: payload.name,
    outbound: payload.outbound,
    domain: toArr(payload.domain),
    ip: toArr(payload.ip),
    port: toArr(payload.port),
    protocol: payload.protocol as AddRoutingRuleRequestProtocolEnum || undefined,
    priority: payload.priority,
  })
  ruleDialog.value = false
}
</script>
