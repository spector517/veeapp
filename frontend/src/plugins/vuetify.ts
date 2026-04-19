import 'vuetify/styles'
import '@mdi/font/css/materialdesignicons.css'
import { createVuetify } from 'vuetify'

export default createVuetify({
  theme: {
    defaultTheme: 'dark',
    themes: {
      dark: {
        colors: {
          primary: '#FF9800',
          secondary: '#42A5F5',
          background: '#1E1E1E',
          surface: '#2A2A2A',
        },
      },
    },
  },
  defaults: {
    VBtn: { variant: 'flat' },
    VCard: { variant: 'outlined' },
  },
})
