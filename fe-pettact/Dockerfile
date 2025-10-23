# node 베이스 이미지
FROM node:20 AS build

# 작업 디렉토리 생성
WORKDIR /app

# package.json 의존성 주입 및 설치
COPY package*.json ./
RUN npm install

# 소스코드 복사
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