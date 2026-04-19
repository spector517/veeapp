import { Configuration, XrayServiceApi, XrayClientsApi, XrayConfigApi, XrayRoutingApi, XrayOutboundsApi } from 'veeapp-sdk'

const config = new Configuration({
  basePath: '',
})

export const xrayServiceApi = new XrayServiceApi(config)
export const xrayClientsApi = new XrayClientsApi(config)
export const xrayConfigApi = new XrayConfigApi(config)
export const xrayRoutingApi = new XrayRoutingApi(config)
export const xrayOutboundsApi = new XrayOutboundsApi(config)
