# 🐾 PetTact
> **Pet + Tact**: 반려동물과의 연결, 그리고 소통을 위한 통합 플랫폼

<br>
## 📋 프로젝트 소개
 
반려동물 1500만 시대, 매년 10만 마리 이상 발생하는 유기동물 문제 해결을 위해 분산된 보호소 정보를 한 곳에서 조회할 수 있는 통합 플랫폼입니다. <br>
반려인을 위한 반려동물 등록, 커뮤니티, 쇼핑 등 다양한 기능을 제공합니다.

- **유기동물 & 보호소 조회**: 공공데이터 API 기반 전국 유기동물 및 보호소 정보 통합 조회
- **반려동물 관리**: 내 반려동물 등록 및 케어, AI 일기 & 챗봇, 공공데이터 기반 동반시설 조회(병원, 약국, 미용, 카페 등)
- **커뮤니티**: 동적 게시판 생성 및 소통 기능
- **쇼핑몰**: 반려동물 용품 구매 및 장바구니
- **실시간 채팅**: SSE 기반 실시간 채팅 시스템

<br>

## 📅 프로젝트 정보
- **개발 기간**: 2025.06.11 ~ 2025.07.23 (6주)
- **팀 구성**: 4인 (Full Stack 개발)
- **배포 상태**: 배포 종료 (~~www.pettact.site~~)


<br>

## ✨ 주요 기능

### 1. 유기동물 & 보호소 조회
공공데이터 API를 활용하여 전국 보호소의 유기동물 정보를 실시간으로 통합 조회합니다. <br>
공고 마감 임박 동물 우선 노출, 축종/견종/지역별 상세 검색 기능을 제공합니다.

### 2. 반려동물 관리
내 반려동물을 등록하고 관리할 수 있으며, AI 일기 작성 및 Pet Assistance 기능을 제공합니다. <br>
Pet Assistance는 반려동물 케어 관련 질문에 실질적인 답변을 제공하는 AI 챗봇입니다.
공공데이터 기반 동반시설(병원, 약국, 카페 등) 조회 기능을 지원합니다.

### 3. 커뮤니티
관리자가 게시판을 커스터마이징하여 생성할 수 있는 동적 게시판 시스템으로 유지보수를 최소화했습니다. <br>
권한, 댓글, 이미지, 추천 여부 등을 설정하여 다양한 형태의 게시판 운영이 가능합니다.

### 4. 쇼핑몰
사용자 누구나 판매자로 신청하여 반려동물 용품을 판매할 수 있는 오픈 마켓 형태의 쇼핑몰입니다. <br>
상품 등록, 구매, 장바구니 기능을 제공합니다.

### 5. 실시간 채팅
SSE 기반으로 관리자 및 판매자와의 1:1 실시간 문의가 가능합니다.

<br>

## 🛠️ 기술 스택

### Backend
![Java](https://img.shields.io/badge/Java-17-007396?style=flat-square&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?style=flat-square&logo=springboot)
![JPA](https://img.shields.io/badge/JPA-Hibernate-59666C?style=flat-square&logo=hibernate)
![JWT](https://img.shields.io/badge/JWT-Auth-000000?style=flat-square&logo=jsonwebtokens)
![WebSocket](https://img.shields.io/badge/WebSocket-STOMP-010101?style=flat-square)

### Database
![MariaDB](https://img.shields.io/badge/MariaDB-003545?style=flat-square&logo=mariadb&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-47A248?style=flat-square&logo=mongodb&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=flat-square&logo=redis&logoColor=white)

### AI/ML
![Python](https://img.shields.io/badge/Python-3776AB?style=flat-square&logo=python&logoColor=white)
![FastAPI](https://img.shields.io/badge/FastAPI-009688?style=flat-square&logo=fastapi&logoColor=white)
![Flask](https://img.shields.io/badge/Flask-000000?style=flat-square&logo=flask&logoColor=white)
![HuggingFace](https://img.shields.io/badge/HuggingFace-FFD21E?style=flat-square&logo=huggingface&logoColor=black)
![LangChain](https://img.shields.io/badge/LangChain-121212?style=flat-square&logoColor=white)

### Infrastructure
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-2088FF?style=flat-square&logo=githubactions&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-232F3E?style=flat-square&logo=amazonaws&logoColor=white)
![HuggingFace Spaces](https://img.shields.io/badge/HF%20Spaces-FFD21E?style=flat-square&logo=huggingface&logoColor=black)

### Frontend
![Vue3](https://img.shields.io/badge/Vue3-4FC08D?style=flat-square&logo=vuedotjs&logoColor=white)
![Axios](https://img.shields.io/badge/Axios-5A29E4?style=flat-square&logo=axios&logoColor=white)
![Bootstrap](https://img.shields.io/badge/Bootstrap-7952B3?style=flat-square&logo=bootstrap&logoColor=white)

### Design
![Figma](https://img.shields.io/badge/Figma-F24E1E?style=flat-square&logo=figma&logoColor=white)

<br>

## 🏗️ 시스템 아키텍처

### ERD 설계
> 프로젝트 전체 데이터베이스 구조 설계 및 관계 정의

<img width="750" alt="ERD 다이어그램" src="https://github.com/user-attachments/assets/73de7969-72af-4495-9417-96d6979582af" />


#### 주요 설계 포인트
- **27개의 관계형 테이블** 설계
  - `USERS` ↔ `PETS`: 사용자별 반려동물 관리
  - `ANIMAL_SHELTER` ↔ 공공 데이터 API 연동: 유기동물 정보 동기화
  - `USERS` ↔ `PRODUCT`: 쇼핑몰 구매 프로세스
  - `BOARD` ↔ `REPLY`: 생성형 게시판 구조
- MongoDB를 활용한 로그 데이터 수집
- Redis 기반 세션 관리 및 캐싱 전략

<br>

## 👥 담당 업무

### 🎯 팀장 (총괄)
- 프로젝트 일정 관리 및 태스크 분배
- 팀원 간 커뮤니케이션 조율

### 📐 기획 및 설계
- **전체 서비스 기획**: 사용자 플로우 및 기능 정의, 기능명세서 작성
- **DB 설계**: ERD 설계 및 팀원 코드 리뷰를 통한 테이블 관계 최적화 (27개 테이블)
- **UI/UX 설계**: Figma를 활용한 전체 화면 와이어프레임 및 디자인 시스템 구축

### 💻 Full Stack 개발
- **백엔드**: 
  - 커스텀 동적 게시판 시스템 (관리자 설정 기반 게시판 자동 생성, 계층형 댓글)
- **프론트엔드**: 
  - 메인 페이지 제작 및 전체 UI 개선

### 🤖 AI 개발
- Python 기반 AI 일기 및 Pet Assistance 챗봇 개발
- LLM 파인튜닝 및 LangChain, RAG 활용
- FastAPI 서버 구축
- HuggingFace Spaces에 모델 등록 및 Spring Boot 연동

### 🚀 DevOps
- GitHub Actions를 활용한 CI/CD 파이프라인 구축
- AWS EC2 배포 및 환경 설정
- Docker 컨테이너화



