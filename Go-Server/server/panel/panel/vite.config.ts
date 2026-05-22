import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  build: {
    // Build into ./dist so Vercel's outputDirectory can find it. The Docker/
    // nginx layout that mounted ../dist is no longer the deploy target.
    outDir: 'dist',
    emptyOutDir: true,
  },
})
