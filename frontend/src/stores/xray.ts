import { defineStore } from 'pinia'
import { ref } from 'vue'
import type {
  StatusResponse,
  XrayConfigRequest,
  AddOutboundRequest,
  AddOutboundFromUrlRequest,
  ChangeOutboundRequest,
  AddRoutingRuleRequest,
  ChangeRuleConditionsRequest,
  RenameRuleRequest,
  SetRulePriorityRequest,
} from 'veeapp-sdk'
import { RoutingDomainStrategyRequestStrategyEnum } from 'veeapp-sdk'
import {
  xrayServiceApi,
  xrayClientsApi,
  xrayConfigApi,
  xrayRoutingApi,
  xrayOutboundsApi,
} from '@/api'
import { useNotificationStore } from '@/stores/notification'
import { AxiosError } from 'axios'

export const useXrayStore = defineStore('xray', () => {
  const status = ref<StatusResponse | null>(null)
  const configured = ref(false)
  const versions = ref<string[]>([])
  const restartRequired = ref(false)
  const loading = ref(false)
  const configChecked = ref(false)
  const loadingKey = ref<string | null>(null)

  function errorMessage(e: unknown): string {
    if (e instanceof AxiosError) {
      return e.response?.data?.message || e.message
    }
    return String(e)
  }

  async function fetchStatus() {
    try {
      const res = await xrayServiceApi.status()
      status.value = res.data
      if (res.data.restart_required !== undefined) {
        restartRequired.value = res.data.restart_required
      }
    } catch (e) {
      useNotificationStore().show('Ошибка загрузки статуса: ' + errorMessage(e), 'error')
    }
  }

  async function checkConfig() {
    try {
      const res = await xrayConfigApi.checkConfig()
      configured.value = res.data.configured ?? false
      configChecked.value = true
    } catch (e) {
      useNotificationStore().show('Ошибка проверки конфигурации: ' + errorMessage(e), 'error')
    }
  }

  async function refreshAll() {
    await Promise.all([fetchStatus(), checkConfig()])
  }

  async function fetchVersions(limit = 10) {
    loadingKey.value = 'versions'
    loading.value = true
    try {
      const res = await xrayConfigApi.listVersions(limit)
      versions.value = res.data.releases ?? []
    } catch (e) {
      useNotificationStore().show('Ошибка загрузки версий: ' + errorMessage(e), 'error')
    } finally {
      loading.value = false
      loadingKey.value = null
    }
  }

  async function exec(fn: () => Promise<any>, successMsg?: string, key?: string, showOverlay = true) {
    const notify = useNotificationStore()
    if (showOverlay) loading.value = true
    if (key) loadingKey.value = key
    try {
      const res = await fn()
      const msg = res?.data?.message ?? successMsg ?? 'Операция выполнена'
      notify.show(msg, 'success')
      restartRequired.value = true
      await refreshAll()
    } catch (e) {
      notify.show('Ошибка: ' + errorMessage(e), 'error')
    } finally {
      if (showOverlay) loading.value = false
      loadingKey.value = null
    }
  }

  // Service controls
  async function startService() {
    await exec(() => xrayServiceApi.start(), 'Сервис запущен', 'start')
  }

  async function stopService() {
    await exec(() => xrayServiceApi.stop(), 'Сервис остановлен', 'stop')
  }

  async function restartService() {
    await exec(() => xrayServiceApi.restart(), 'Сервис перезапущен', 'restart')
  }

  // Config
  async function applyConfig(req: XrayConfigRequest) {
    await exec(() => xrayConfigApi.config(req), 'Конфигурация применена', 'config')
  }

  async function updateGeodata() {
    await exec(() => xrayConfigApi.updateGeodata(), 'Geodata обновлены', 'geodata')
  }

  async function updateXray(version: string) {
    await exec(() => xrayConfigApi.updateXray(version), 'Xray обновлён', 'updateXray')
  }

  // Clients
  async function addClients(names: string[]) {
    await exec(() => xrayClientsApi.add1({ names }), 'Клиенты добавлены', 'addClients')
  }

  async function removeClients(names: string[]) {
    await exec(() => xrayClientsApi.remove({ names }), 'Клиенты удалены', `removeClient-${names[0]}`)
  }

  // Outbounds
  async function addOutbound(req: AddOutboundRequest) {
    await exec(() => xrayOutboundsApi.add(req), 'Подключение добавлено', 'addOutbound')
  }

  async function addOutboundFromUrl(req: AddOutboundFromUrlRequest) {
    await exec(() => xrayOutboundsApi.addFromUrl(req), 'Подключение добавлено', 'addOutboundFromUrl')
  }

  async function changeOutbound(name: string, req: ChangeOutboundRequest) {
    await exec(() => xrayOutboundsApi.change(name, req), 'Подключение изменено', `changeOutbound-${name}`)
  }

  async function removeOutbound(name: string) {
    await exec(() => xrayOutboundsApi.remove1(name), 'Подключение удалено', `removeOutbound-${name}`)
  }

  // Routing
  async function addRoutingRule(req: AddRoutingRuleRequest) {
    await exec(() => xrayRoutingApi.addRule(req), 'Правило добавлено', 'addRule')
  }

  async function removeRoutingRule(name: string) {
    await exec(() => xrayRoutingApi.removeRule(name), 'Правило удалено', `removeRule-${name}`)
  }

  async function renameRule(name: string, req: RenameRuleRequest) {
    await exec(() => xrayRoutingApi.renameRule(name, req), 'Правило переименовано', 'renameRule')
  }

  async function setRulePriority(name: string, req: SetRulePriorityRequest) {
    await exec(() => xrayRoutingApi.setPriority(name, req), 'Приоритет обновлён', 'setPriority')
  }

  async function changeRuleConditions(name: string, req: ChangeRuleConditionsRequest) {
    await exec(() => xrayRoutingApi.changeConditions(name, req), 'Условия обновлены', 'changeConditions')
  }

  async function setDomainStrategy(strategy: RoutingDomainStrategyRequestStrategyEnum) {
    await exec(
      () => xrayRoutingApi.setDomainStrategy({ strategy }),
      'Доменная стратегия обновлена',
      'setStrategy',
    )
  }

  // Apply = restart if running
  async function applyChanges() {
    if (status.value?.server_status === 'running') {
      await exec(() => xrayServiceApi.restart(), 'Изменения применены', 'apply')
      restartRequired.value = false
    }
  }

  return {
    status,
    configured,
    configChecked,
    versions,
    restartRequired,
    loading,
    loadingKey,
    fetchStatus,
    checkConfig,
    fetchVersions,
    refreshAll,
    startService,
    stopService,
    restartService,
    applyConfig,
    updateGeodata,
    updateXray,
    addClients,
    removeClients,
    addOutbound,
    addOutboundFromUrl,
    changeOutbound,
    removeOutbound,
    addRoutingRule,
    removeRoutingRule,
    renameRule,
    setRulePriority,
    changeRuleConditions,
    setDomainStrategy,
    applyChanges,
  }
})
