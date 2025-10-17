FROM node:20-alpine AS build
WORKDIR /app

# Python 추가 (네이티브 모듈 빌드용)
RUN apk add --no-cache python3 make g++

COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
