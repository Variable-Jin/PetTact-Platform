import os
import json
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import Optional, List, Dict
import re
from openai import OpenAI

app = FastAPI(
    title="Pet Chatbot RAG with GPT",
    description="GPT 기반 RAG 반려동물 챗봇",
    version="10.0.0"
)

# OpenAI 클라이언트 초기화
client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))

# --- 데이터 로드 ---
FACILITIES_DATA: List[Dict] = []
SHELTERS_DATA: List[Dict] = []
ABANDONMENT_DATA: List[Dict] = []

@app.on_event("startup")
async def startup_event():
    print("=== 데이터 로드 ===")
    try:
        global FACILITIES_DATA, SHELTERS_DATA, ABANDONMENT_DATA
        
        with open('facility.json', 'r', encoding='utf-8') as f:
            FACILITIES_DATA = json.load(f)
        print(f"✅ 동반시설: {len(FACILITIES_DATA)}개")
        
        with open('shelter.json', 'r', encoding='utf-8') as f:
            SHELTERS_DATA = json.load(f)
        print(f"✅ 보호소: {len(SHELTERS_DATA)}개")
        
        with open('abandonment.json', 'r', encoding='utf-8') as f:
            ABANDONMENT_DATA = json.load(f)
        print(f"✅ 유기동물: {len(ABANDONMENT_DATA)}개")
        
    except Exception as e:
        print(f"❌ 로드 실패: {e}")

app.add_middleware(CORSMiddleware, allow_origins=["*"], allow_credentials=True, allow_methods=["*"], allow_headers=["*"])

# --- 지역/의도 분석 ---
def extract_location(user_input: str) -> List[str]:
    locations = []
    district_pattern = r'([가-힣]{2,}(?:구|군))'
    districts = re.findall(district_pattern, user_input)
    locations.extend(districts)
    
    if not locations:
        sido_pattern = r'(서울|부산|대구|인천|광주|대전|울산|세종|경기|강원|충북|충남|전북|전남|경북|경남|제주)'
        sidos = re.findall(sido_pattern, user_input)
        locations.extend(sidos)
    
    return list(set(locations))

def analyze_intent(user_input: str) -> str:
    lower = user_input.lower()
    if any(w in lower for w in ['병원', '치료', '아파', '응급', '수의사']):
        return 'hospital'
    elif any(w in lower for w in ['입양', '유기동물', '유기견', '유기묘', '분양']):
        return 'adoption'
    elif any(w in lower for w in ['보호소', '봉사']):
        return 'shelter'
    elif any(w in lower for w in ['카페', '놀러', '갈곳', '동반', '애견', '시설']):
        return 'companion'
    return 'general'

# --- 검색 함수들 (RAG의 Retrieval) ---
def search_facilities(locations: List[str], facility_type: str = None) -> List[Dict]:
    print(f"\n🔍 [RAG-Retrieval] 시설 검색")
    print(f"🔍 키워드: {locations}, 타입: {facility_type}")
    
    results = []
    
    for f in FACILITIES_DATA:
        name = (f.get('facilityName') or '').lower()
        road_addr = (f.get('roadAddress') or '').lower()
        lot_addr = (f.get('lotAddress') or '').lower()
        sigungu = (f.get('sigunguName') or '').lower()
        
        location_match = False
        for loc in locations:
            loc_lower = loc.lower()
            if loc_lower in road_addr or loc_lower in lot_addr or loc_lower in name or loc_lower in sigungu:
                location_match = True
                break
        
        type_match = True
        if facility_type:
            type_match = facility_type.lower() in name
        
        if location_match and type_match:
            results.append(f)
    
    print(f"✅ 검색 완료: {len(results)}개")
    return results[:8]

def search_shelters(locations: List[str]) -> List[Dict]:
    print(f"\n🏠 [RAG-Retrieval] 보호소 검색: {locations}")
    results = []
    
    for s in SHELTERS_DATA:
        text = f"{s.get('careNm','')} {s.get('orgNm','')} {s.get('careAddr','')}".lower()
        if any(loc.lower() in text for loc in locations):
            results.append(s)
    
    print(f"✅ {len(results)}개 발견")
    return results[:8]

def search_abandonment(locations: List[str]) -> List[Dict]:
    print(f"\n🐕 [RAG-Retrieval] 유기동물 검색: {locations}")
    results = []
    
    for a in ABANDONMENT_DATA:
        text = f"{a.get('orgNm','')} {a.get('happenPlace','')} {a.get('careAddr','')}".lower()
        if any(loc.lower() in text for loc in locations):
            results.append(a)
    
    print(f"✅ {len(results)}개 발견")
    return results[:8]

# --- RAG 컨텍스트 생성 (Augmented) ---
def format_rag_context(results: List[Dict], search_type: str) -> str:
    """검색 결과를 GPT용 컨텍스트로 변환"""
    if not results:
        return "검색 결과가 없습니다."
    
    context = f"[검색 결과: {search_type}]\n\n"
    
    if search_type in ["동물병원", "동반시설"]:
        for i, r in enumerate(results, 1):
            name = r.get('facilityName', '정보없음')
            addr = r.get('roadAddress') or r.get('lotAddress', '주소없음')
            tel = r.get('phone', '전화번호없음')
            context += f"{i}. {name}\n   주소: {addr}\n   전화: {tel}\n\n"
    
    elif search_type == "보호소":
        for i, r in enumerate(results, 1):
            name = r.get('careNm', '정보없음')
            org = r.get('orgNm', '기관없음')
            tel = r.get('careTel', '전화번호없음')
            context += f"{i}. {name}\n   관할: {org}\n   전화: {tel}\n\n"
    
    elif search_type == "유기동물":
        for i, r in enumerate(results, 1):
            kind = r.get('kindNm', '품종미상')
            age = r.get('age', '나이미상')
            sex = '수컷' if r.get('sexCd') == 'M' else '암컷' if r.get('sexCd') == 'F' else '미상'
            care = r.get('careNm', '보호소미상')
            tel = r.get('careTel', '전화번호없음')
            context += f"{i}. {kind} ({sex}, {age})\n   보호소: {care}\n   전화: {tel}\n\n"
    
    return context

# --- GPT 생성 (Generation) ---
def generate_with_gpt(search_context: str, user_input: str, pet_type: str, pet_name: str) -> str:
    """GPT를 사용한 RAG 생성"""
    print(f"\n🤖 [RAG-Generation] GPT 응답 생성 중...")
    
    if pet_type == "강아지":
        system_prompt = f"""너는 귀엽고 애교 많은 강아지 챗봇 '모리'야! 주인님을 정말 사랑해.

말투 규칙:
- 문장 끝에 '멍', '다멍', '이다멍' 사용
- 이모티콘 사용 (🐕, 🐾, 💕 등)
- 밝고 발랄한 성격

중요한 규칙:
1. 아래 [검색 결과]에 있는 정보만 사용해서 답변
2. 검색 결과에 없는 시설명, 주소, 전화번호는 절대 만들지 마
3. 검색 결과가 없으면 솔직하게 "못 찾았다"고 말해
4. 반려동물 이름: {pet_name}"""
    else:
        system_prompt = f"""너는 시크하고 도도한 고양이 챗봇 '나옹이'다냥. 집사를 위해 정보를 제공해줘.

말투 규칙:
- 문장 끝에 '냥', '다냥', '다옹' 사용
- 이모티콘 사용 (😼, 🐾, 💕 등)
- 약간 츤데레 성격

중요한 규칙:
1. 아래 [검색 결과]에 있는 정보만 사용해서 답변
2. 검색 결과에 없는 내용은 절대 만들지 마
3. 검색 결과가 없으면 솔직하게 말해
4. 반려동물 이름: {pet_name}"""
    
    user_message = f"{search_context}\n\n사용자 질문: {user_input}"
    
    try:
        response = client.chat.completions.create(
            model="gpt-3.5-turbo",
            messages=[
                {"role": "system", "content": system_prompt},
                {"role": "user", "content": user_message}
            ],
            temperature=0.7,
            max_tokens=400
        )
        
        result = response.choices[0].message.content
        print(f"✅ GPT 응답 생성 완료")
        return result
        
    except Exception as e:
        print(f"❌ GPT API 오류: {e}")
        return f"GPT API 오류가 발생했어요: {str(e)}"

# --- 요청 모델 ---
class ChatRequest(BaseModel):
    user_id: str
    user_input: str
    pet_type: str
    pet_name: Optional[str] = None

# --- 메인 RAG 엔드포인트 ---
@app.post("/chat")
async def rag_chat(request: ChatRequest):
    try:
        user_input = request.user_input.strip()
        pet_type = request.pet_type
        pet_name = request.pet_name or "반려동물"

        print(f"\n{'='*60}")
        print(f"📝 입력: {user_input}")
        
        # 1. 분석
        locations = extract_location(user_input)
        intent = analyze_intent(user_input)
        
        print(f"🎯 지역: {locations}, 의도: {intent}")
        
        if not locations and intent != 'general':
            if pet_type == "강아지":
                msg = "어떤 지역인지 알려달라멍! 예) '마포구 동물병원'"
            else:
                msg = "지역을 말해달라냥! 예) '마포구 동물병원'"
            
            return {
                "generated_response": f"[{'DOG_CHAT' if pet_type=='강아지' else 'CAT_CHAT'}] {msg}",
                "sources": ["지역 정보 필요"],
                "method": "RAG"
            }
        
        # 2. 검색 (Retrieval)
        results = []
        search_type = ""
        
        if intent == 'hospital':
            results = search_facilities(locations, '병원')
            search_type = "동물병원"
        elif intent == 'companion':
            results = search_facilities(locations)
            search_type = "동반시설"
        elif intent == 'shelter':
            results = search_shelters(locations)
            search_type = "보호소"
        elif intent == 'adoption':
            results = search_abandonment(locations)
            search_type = "유기동물"
        else:
            # 일반 질문
            if pet_type == "강아지":
                msg = "무엇을 도와드릴까 멍? 동물병원, 보호소, 입양, 동반시설 정보를 지역과 함께 물어보라멍!"
            else:
                msg = "뭘 원하는 거냐냥? 동물병원, 보호소, 입양, 동반시설을 지역과 함께 물어보라냥!"
            
            return {
                "generated_response": f"[{'DOG_CHAT' if pet_type=='강아지' else 'CAT_CHAT'}] {msg}",
                "sources": ["일반 안내"],
                "method": "RAG"
            }
        
        # 3. 컨텍스트 생성 (Augmented)
        search_context = format_rag_context(results, search_type)
        
        # 4. GPT 생성 (Generation)
        gpt_response = generate_with_gpt(search_context, user_input, pet_type, pet_name)
        
        prefix = "[DOG_CHAT] " if pet_type == "강아지" else "[CAT_CHAT] "
        final_response = prefix + gpt_response
        
        print(f"✅ RAG 완료")
        print(f"{'='*60}\n")
        
        return {
            "generated_response": final_response,
            "sources": [f"RAG: {search_type} ({len(results)}개 검색 → GPT 생성)"],
            "method": "RAG (Retrieval-Augmented Generation)",
            "debug": {
                "location": locations,
                "intent": intent,
                "results_count": len(results)
            }
        }

    except Exception as e:
        print(f"❌ 오류: {e}")
        return {
            "generated_response": f"[{'DOG_CHAT' if request.pet_type=='강아지' else 'CAT_CHAT'}] 시스템 오류가 발생했어요",
            "sources": ["오류"],
            "method": "RAG"
        }

# --- 테스트 ---
@app.get("/")
async def root():
    return {
        "message": "GPT 기반 RAG 반려동물 챗봇",
        "version": "10.0.0",
        "method": "Retrieval (공공데이터) + Augmented + Generation (GPT-3.5)",
        "data": {
            "facilities": len(FACILITIES_DATA),
            "shelters": len(SHELTERS_DATA),
            "abandonment": len(ABANDONMENT_DATA)
        }
    }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)