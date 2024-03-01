import {defineConfig} from 'vite'
import react from '@vitejs/plugin-react'
import * as process from "process";
import * as fs from "fs";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  define: {
    APP_VERSION: JSON.stringify(process.env.npm_package_version),
  }
})
