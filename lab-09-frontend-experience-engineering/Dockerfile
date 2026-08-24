FROM node:20-bookworm-slim

RUN apt-get update && apt-get install -y --no-install-recommends \
    ca-certificates curl git \
    && rm -rf /var/lib/apt/lists/*

RUN npm install -g @angular/cli@20

WORKDIR /workspaces/lab

COPY package.json package-lock.json ./
COPY apps/landing-vue/package.json apps/landing-vue/package.json
COPY apps/catalog-angular/package.json apps/catalog-angular/package.json
COPY apps/product-vue/package.json apps/product-vue/package.json
COPY services/cms-api/package.json services/cms-api/package.json
COPY qa/playwright/package.json qa/playwright/package.json

RUN npm ci

COPY . .

RUN npx playwright install --with-deps chromium

CMD ["sleep", "infinity"]
