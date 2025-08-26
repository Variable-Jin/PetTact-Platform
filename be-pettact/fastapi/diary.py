import os
from flask import Flask, request, jsonify
from flask_cors import CORS
from langchain_core.output_parsers import StrOutputParser
from langchain_core.prompts import ChatPromptTemplate
from langchain_openai import ChatOpenAI
from openai import OpenAI
import requests # requests 라이브러리는 현재 코드에서는 직접 사용되지 않지만, API 호출 시 사용될 수 있어 남겨두기
from dotenv import load_dotenv

# .env 파일에서 환경 변수 로드 (API 키 등)
load_dotenv()

app = Flask(__name__)
# 모든 출처(Origin)로부터의 요청을 허용하도록 CORS 설정
CORS(app)

# OpenAI 클라이언트 초기화
# OPENAI_API_KEY는 .env 파일에 설정 필수
openai_client = OpenAI(api_key=os.environ.get("OPENAI_API_KEY"))

# LangChain을 사용한 일기 생성 프롬프트 템플릿 정의
# 사용자로부터 받은 다양한 강아지 정보를 프롬프트에 포함
prompt_mori_diary = ChatPromptTemplate.from_template(
    """안녕, 사랑하는 내 {dog_call}! 멍! 나는 {dog_name}야. 멍멍!
나는 {dog_age}살 {dog_breed} 강아지야.
오늘 있었던 일은 "{topic}"이야.
이 일에 대해서 내가 느꼈던 것, 생각했던 것들을 강아지 입장에서 귀엽고 재밌게 일기처럼 이야기해줄게!
최소 200자 이상으로 자세하게 이야기해줘. 멍멍!
 """
)

# LangChain ChatOpenAI 모델 초기화
# "gpt-4o" 모델을 사용하여 일기를 생성.
model = ChatOpenAI(model="gpt-4o-mini")
# 문자열 출력을 파싱하는 아웃풋 파서
output_parser = StrOutputParser()
# 일기 생성 체인 구성: 프롬프트 -> 모델 -> 파서
diary_chain = prompt_mori_diary | model | output_parser

# DALL-E 3를 사용하여 이미지 URL을 생성하는 함수
def generate_image_url(topic: str, dog_breed: str): # dog_breed 매개변수 추가
    """
    주어진 주제와 견종을 바탕으로 DALL-E 3를 사용하여 이미지 URL을 생성합니다.
    OpenAI의 안전 정책을 준수하기 위해 프롬프트 내용을 신중하게 구성해야 합니다.
    """
    try:
        # DALL-E 프롬프트는 안전 정책에 매우 민감합니다.
        # 'topic'에서 부정적인 뉘앙스를 가진 단어를 간단히 제거하거나,
        # 프롬프트 전체를 긍정적인 방향으로 유도하는 것이 좋습니다.
        
        # 간단한 부정적 단어 필터링 (확장 가능)
        negative_words = []
        filtered_topic_for_image = topic
        for neg_word in negative_words:
            filtered_topic_for_image = filtered_topic_for_image.replace(neg_word, "").strip()
        
        # 필터링 후 주제가 비어있으면 일반적인 긍정적 문구 사용
        if not filtered_topic_for_image:
            filtered_topic_for_image = "즐거운 하루"
            
        # 이미지 생성 프롬프트 구성: 견종과 긍정적으로 필터링된 주제를 활용
        image_prompt = f"귀여운 {dog_breed} 강아지가 '{filtered_topic_for_image}'라는 주제의 그림."

        # 이미지 생성 요청
        response = openai_client.images.generate(
            model="dall-e-3",
            prompt=image_prompt, # 수정된 이미지 프롬프트 사용
            size="1024x1024",
            quality="standard",
            n=1 # 생성할 이미지 개수
        )
        # 생성된 이미지의 URL 반환
        image_url = response.data[-1].url
        return image_url
    except Exception as e:
        # 이미지 생성 중 오류 발생 시 콘솔에 출력하고 None 반환
        print(f"이미지 생성 중 오류 발생: {e}")
        return None

# '/generate_mori_content' 엔드포인트 정의 (POST 요청 처리)
@app.route('/generate_mori_content', methods=['POST'])
def generate_mori_content():
    """
    프론트엔드로부터 강아지 정보와 일기 주제를 받아
    AI 일기와 AI 이미지를 생성하여 반환합니다.
    """
    # 요청 본문에서 JSON 데이터 파싱
    data = request.json
    # 각 필드에서 값을 추출하고, 값이 없을 경우 기본값 설정
    topic = data.get('topic', '')
    dog_name = data.get('dogName', '댕댕이')
    dog_age = data.get('dogAge', '알 수 없는')
    dog_call = data.get('dogCall', '주인님')
    dog_breed = data.get('dogBreed', '강아지')

    # 필수 필드인 'topic'이 비어있을 경우 400 Bad Request 응답
    if not topic:
        return jsonify({"error": "일기 주제를 입력해주세요."}), 400

    try:
        # LangChain 체인을 사용하여 일기 텍스트 생성
        diary_output = diary_chain.invoke({
            "topic": topic,
            "dog_name": dog_name,
            "dog_age": dog_age,
            "dog_call": dog_call,
            "dog_breed": dog_breed
        })
        
        # DALL-E 3를 사용하여 이미지 URL 생성
        # generate_image_url 함수에 dog_breed 인자도 함께 전달
        image_url_output = generate_image_url(topic, dog_breed)

        # 이미지 생성에 실패했을 경우 500 Internal Server Error 응답
        if not image_url_output:
            return jsonify({"error": "이미지 생성에 실패했습니다. 프롬프트 내용을 확인해주세요."}), 500

        # 성공 시 일기 텍스트와 이미지 URL을 JSON 형태로 반환
        return jsonify({
            "diary": diary_output,
            "image_url": image_url_output
        })
    
    

    except Exception as e:
        # 예기치 않은 오류 발생 시 500 Internal Server Error 응답
        print(f"API 호출 중 오류 발생: {e}")
        return jsonify({"error": f"콘텐츠 생성 중 오류가 발생했습니다: {str(e)}"}), 500

# Flask 앱 실행
# debug=True -> (배포 시에는 False로 설정)
# port=5000은 프론트엔드와 동일한 포트를 사용 설정
if __name__ == '__main__':
    app.run(debug=True, port=5000)