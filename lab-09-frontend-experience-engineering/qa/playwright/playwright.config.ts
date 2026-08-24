import { defineConfig, devices } from '@playwright/test';

const EASY_BASE_URL = process.env.EASY_BASE_URL ?? 'http://landing-vue:5173';
const INTERMEDIATE_BASE_URL = process.env.INTERMEDIATE_BASE_URL ?? 'http://catalog-angular:4200';
const ADVANCED_BASE_URL = process.env.ADVANCED_BASE_URL ?? 'http://product-vue:5174';

export default defineConfig({
  testDir: './tests',
  fullyParallel: false,
  forbidOnly: !!process.env.CI,
  retries: 0,
  reporter: [['list']],
  timeout: 30_000,
  use: {
    trace: 'retain-on-failure',
    screenshot: 'only-on-failure',
  },
  projects: [
    {
      name: 'easy',
      testMatch: 'easy.spec.ts',
      use: { ...devices['Desktop Chrome'], baseURL: EASY_BASE_URL },
    },
    {
      name: 'intermediate',
      testMatch: 'intermediate.spec.ts',
      use: { ...devices['Desktop Chrome'], baseURL: INTERMEDIATE_BASE_URL },
    },
    {
      name: 'advanced',
      testMatch: 'advanced.spec.ts',
      use: { ...devices['Desktop Chrome'], baseURL: ADVANCED_BASE_URL },
    },
  ],
});
