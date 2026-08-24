import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// Configuración básica de Vite: solo necesitamos el plugin de React
// para que entienda archivos .tsx (JSX con TypeScript).
export default defineConfig({
  plugins: [react()],
})
