FROM node:24-alpine
WORKDIR /app
COPY package.json package-lock.json angular.json tsconfig.json tsconfig.app.json ./
RUN npm ci
COPY src src
COPY tests tests
CMD ["npm","start"]
