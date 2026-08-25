FROM node:24.15-alpine
WORKDIR /workspace
COPY package*.json ./
RUN npm ci
COPY . .
CMD ["npm","run","dev"]
