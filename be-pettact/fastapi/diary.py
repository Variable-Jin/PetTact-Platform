import os
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from langchain_core.output_parsers import StrOutputParser
from langchain_core.prompts import ChatPromptTemplate
from langchain_openai import ChatOpenAI
from openai import OpenAI
import uvicorn
from dotenv import load_dotenv
from typing import Optional

# .env 파일에서 환경 변수 로드 (API 키 등)
load_dotenv()

# FastAPI 앱 초기화
app = FastAPI(title="Mori Diary API", description="강아지 일기 생성 API", version="1.0.0")

# CORS 설정
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # 실제 배포 환경에서는 특정 출처만 허용하는 것이 좋습니다.
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# OpenAI 클라이언트 초기화
openai_client = OpenAI(api_key=os.environ.get("OPENAI_API_KEY"))

# 요청 데이터 모델 정의
class MoriContentRequest(BaseModel):
    topic: str
    dogName: str = "댕댕이"
    dogAge: str = "알 수 없는"
    dogCall: str = "주인님"
    dogBreed: str = "강아지"

# 응답 데이터 모델 정의
class MoriContentResponse(BaseModel):
    diary: str
    image_url: str

# LangChain을 사용한 일기 생성 프롬프트 템플릿 정의
prompt_mori_diary = ChatPromptTemplate.from_template(
    """안녕, 사랑하는 내 {dog_call}! 멍! 나는 {dog_name}야. 멍멍!
나는 {dog_age}살 {dog_breed} 강아지야.
오늘 있었던 일은 "{topic}"이야.
이 일에 대해서 내가 느꼈던 것, 생각했던 것들을 강아지 입장에서 귀엽고 재밌게 일기처럼 이야기해줄게!
최소 200자 이상으로 자세하게 이야기해줘. 멍멍!
 """
)

# LangChain ChatOpenAI 모델 초기화
model = ChatOpenAI(model="gpt-4o-mini")
output_parser = StrOutputParser()
diary_chain = prompt_mori_diary | model | output_parser

def generate_image_url(topic: str, dog_breed: str) -> Optional[str]:
    """
    주어진 주제와 견종을 바탕으로 DALL-E 3를 사용하여 이미지 URL을 생성합니다.
    """
    try:
        # 간단한 부정적 단어 필터링
        negative_words = []
        filtered_topic_for_image = topic
        for neg_word in negative_words:
            filtered_topic_for_image = filtered_topic_for_image.replace(neg_word, "").strip()
        
        # 필터링 후 주제가 비어있으면 일반적인 긍정적 문구 사용
        if not filtered_topic_for_image:
            filtered_topic_for_image = "즐거운 하루"
            
        # 이미지 생성 프롬프트 구성
        image_prompt = f"귀여운 {dog_breed} 강아지가 '{filtered_topic_for_image}'라는 주제의 그림."

        # 이미지 생성 요청
        response = openai_client.images.generate(
            model="dall-e-3",
            prompt=image_prompt,
            size="1024x1024",
            quality="standard",
            n=1
        )
        
        image_url = response.data[-1].url
        return image_url
        
    except Exception as e:
        print(f"이미지 생성 중 오류 발생: {e}")
        return None

@app.get("/")
async def root():
    """기본 엔드포인트"""
    return {"message": "Mori Diary API가 실행 중입니다!"}

@app.post("/generate_mori_content", response_model=MoriContentResponse)
async def generate_mori_content(request: MoriContentRequest):
    """
    프론트엔드로부터 강아지 정보와 일기 주제를 받아
    AI 일기와 AI 이미지를 생성하여 반환합니다.
    """
    # 필수 필드 검증
    if not request.topic.strip():
        raise HTTPException(status_code=400, detail="일기 주제를 입력해주세요.")

    try:
        # LangChain 체인을 사용하여 일기 텍스트 생성
        diary_output = diary_chain.invoke({
            "topic": request.topic,
            "dog_name": request.dogName,
            "dog_age": request.dogAge,
            "dog_call": request.dogCall,
            "dog_breed": request.dogBreed
        })
        
        # DALL-E 3를 사용하여 이미지 URL 생성
        image_url_output = generate_image_url(request.topic, request.dogBreed)

        # 이미지 생성에 실패했을 경우
        if not image_url_output:
            raise HTTPException(status_code=500, detail="이미지 생성에 실패했습니다. 프롬프트 내용을 확인해주세요.")

        # 성공 시 일기 텍스트와 이미지 URL 반환
        return MoriContentResponse(
            diary=diary_output,
            image_url=image_url_output
        )

    except HTTPException:
        # HTTPException은 그대로 다시 발생시킴
        raise
    except Exception as e:
        # 예기치 않은 오류 발생 시
        print(f"API 호출 중 오류 발생: {e}")
        raise HTTPException(status_code=500, detail=f"콘텐츠 생성 중 오류가 발생했습니다: {str(e)}")

# 건강 상태 확인 엔드포인트
@app.get("/health")
async def health_check():
    """서버 상태 확인"""
    return {"status": "healthy", "message": "서버가 정상적으로 실행 중입니다."}

# FastAPI 앱 실행
if __name__ == "__main__":
    uvicorn.run(
        "main:app",  # 파일명이 main.py라고 가정
        host="127.0.0.1",
        port=5000,
        reload=True  # 개발 중 자동 리로드
    )