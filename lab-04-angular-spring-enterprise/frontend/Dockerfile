FROM node:22-alpine
WORKDIR /app
COPY package.json angular.json tsconfig.json ./
RUN npm install
COPY src src
COPY tests tests
CMD ["npm","start"]
