# Node.js 베이스 이미지 (Alpine)
FROM node:20-alpine AS build

# 작업 디렉토리 생성
WORKDIR /app

# 네이티브 모듈 빌드를 위한 도구 설치
RUN apk add --no-cache python3 make g++

# package.json만 복사
COPY package.json ./

# 의존성 설치 (강제 새로 설치)
RUN npm cache clean --force && npm install

# 소스 코드 복사
COPY . .

# Rollup 네이티브 바이너리 수동 설치 (ARM용)
#RUN npm install @rollup/rollup-linux-arm64-gnu --save-dev

# 애플리케이션 빌드
RUN npm run build

# 최종 이미지 설정
FROM nginx:alpine

# 애플리케이션 파일 복사
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80

# 컨테이너 실행 시 실행할 명령 정의
CMD ["nginx", "-g", "daemon off;"]