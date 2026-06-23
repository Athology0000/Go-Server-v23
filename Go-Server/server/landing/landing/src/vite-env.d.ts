/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_PANEL_URL?: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
