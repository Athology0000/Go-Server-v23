import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  build: {
    // Docker / nginx mounts `server/panel/dist`, so build directly into it.
    outDir: '../dist',
    emptyOutDir: true,
  },
})
