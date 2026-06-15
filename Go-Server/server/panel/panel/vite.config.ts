/// <reference types="vitest/config" />
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  build: {
    // Build into ./dist so Vercel's outputDirectory can find it. The Docker/
    // nginx layout that mounted ../dist is no longer the deploy target.
    outDir: 'dist',
    emptyOutDir: true,
    rollupOptions: {
      output: {
        // Split the rarely-changing framework code into its own chunk so it
        // stays cached across app deploys.
        manualChunks: {
          'react-vendor': ['react', 'react-dom', 'react-router-dom'],
          state: ['zustand'],
        },
      },
    },
  },
  test: {
    environment: 'jsdom',
    setupFiles: ['./src/test/setup.ts'],
    css: false,
    restoreMocks: true,
  },
})
