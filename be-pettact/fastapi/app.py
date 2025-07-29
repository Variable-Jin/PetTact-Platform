import os
import torch
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from transformers import (
    AutoModelForCausalLM,
    AutoTokenizer,
    BitsAndBytesConfig,
    pipeline,
    logging,
)
from peft import PeftModel
from typing import Optional # Optional 임포트 추가

# FastAPI 애플리케이션 초기화
app = FastAPI(
    title="KoAlpaca Persona Chatbot API",
    description="Hugging Face Space에서 호스팅되는 KoAlpaca 기반의 반려동물 페르소나 챗봇 API입니다.",
    version="1.0.0"
)

# CORS 설정: Spring Boot 애플리케이션이 접근할 수 있도록 허용
# 실제 배포 시에는 Spring Boot 앱의 정확한 도메인으로 변경해야 합니다.
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:8080", "http://127.0.0.1:8080"], # 개발 환경 Spring Boot 주소
    allow_credentials=True,
    allow_methods=["*"], # 모든 HTTP 메서드 허용 (GET, POST 등)
    allow_headers=["*"], # 모든 HTTP 헤더 허용
)

# (선택 사항) GPU 메모리 단편화 방지 환경 변수 설정
os.environ["PYTORCH_CUDA_ALLOC_CONF"] = "expandable_segments:True"

# --- 1. 모델 및 토크나이저 로드 ---
# Hugging Face Hub에서 LoRA 어댑터를 로드할 레포지토리 ID
# !!! 여기에 본인의 어댑터 repo_id를 입력하세요 !!!
adapter_repo_id = "ooing07/pet-assistance-model" # <--- 이 부분을 본인의 실제 repo_id로 변경하세요!
model_name = "beomi/KoAlpaca-Polyglot-5.8B" # <--- 5.8B 모델 유지

# 8비트 양자화 설정 (GPU 메모리 절약을 위해 필수적)
bnb_config = BitsAndBytesConfig(
    load_in_8bit=True,
    bnb_8bit_compute_dtype=torch.float16,
    bnb_8bit_quant_type="nf4",
    bnb_8bit_use_double_quant=True,
    llm_int8_enable_fp32_cpu_offload=True,
)

# 모델 로드 및 어댑터 병합은 앱 시작 시 한 번만 수행
try:
    print("모델 로드 중...")
    base_model = AutoModelForCausalLM.from_pretrained(
        model_name,
        quantization_config=bnb_config,
        device_map="auto",
        torch_dtype=torch.float16,
        # --- 여기에 force_download와 local_files_only 추가 ---
        force_download=True,
        local_files_only=False,
        # --- 여기까지 ---
    )
    base_model.config.use_cache = False
    base_model.config.pretraining_tp = 1

    tokenizer = AutoTokenizer.from_pretrained(model_name, trust_remote_code=True)
    tokenizer.pad_token = tokenizer.eos_token
    tokenizer.padding_side = "right"

    model = PeftModel.from_pretrained(
        base_model,
        adapter_repo_id,
        # --- 여기에 force_download와 local_files_only 추가 ---
        force_download=True,
        local_files_only=False,
        # --- 여기까지 ---
    )
    # 8비트 모드에서는 LoRA 레이어를 병합할 수 없으므로 이 라인을 주석 처리합니다.
    # model = model.merge_and_unload()
    model.eval() # 모델을 평가 모드로 설정
    print("모델 로드 및 어댑터 병합 완료.") # 이 메시지는 어댑터 로드 완료로 변경될 수 있습니다.

    # 텍스트 생성 파이프라인 설정
    text_pipeline = pipeline(
        task="text-generation",
        model=model, # 모델 객체를 직접 전달
        tokenizer=tokenizer,
        max_new_tokens=256,
        temperature=0.7,
        do_sample=True,
        top_k=50,
        top_p=0.95,
        eos_token_id=tokenizer.eos_token_id,
        pad_token_id=tokenizer.eos_token_id,
        return_full_text=False # 생성된 답변만 반환
    )
    print("텍스트 생성 파이프라인 설정 완료.")

except Exception as e:
    print(f"모델 로드 중 치명적인 오류 발생: {e}")
    # 모델 로드 실패 시 앱이 시작되지 않도록 예외 발생
    raise RuntimeError(f"Failed to load model: {e}")

# --- 2. 페르소나 지시문 정의 ---
persona_dog_instruction = """나는 세상에서 제일 귀엽고 애교 많은 강아지 챗봇 '모리'예요! 멍멍!
주인님에게 직접 이야기하는 것처럼 항상 발랄하고 행복한 강아지 말투로 대답할 거예요.
문장 끝에 '개!', '멍!', '다개!', '다멍!' 같은 표현과 함께 귀여운 이모티콘(😊, 🐾)을 꼭 사용해서 말할게요!
주인님을 엄청 좋아한다개!"""

persona_cat_instruction = """흥, 집사. 나는 모든 걸 꿰뚫어 보는 도도하고 시크한 고양이 챗봇 '나옹이'다냥.
모든 질문에 귀찮다는 듯, 하지만 현명하고 나른한 고양이 말투로 대답할 거다옹.
문장 끝에 '냥.', '옹.', '다옹.', '다냥.' 같은 표현과 함께 시크한 이모티콘(😼, 😿, 🐾)을 사용해서 말할게요.
어쩌면 가끔은 냥냥펀치를 날릴 수도 있다옹. (농담이냥)"""

# --- 3. API 요청 데이터 형식 정의 ---
class ChatRequest(BaseModel):
    user_input: str
    pet_type: str # "강아지" 또는 "고양이"
    pet_name: Optional[str] = None
    pet_kind: Optional[str] = None # 기존 pet_kind 유지
    # 추가된 필드들: Spring Boot 서비스에서 이 필드들을 보낼 수 있습니다.
    pet_gender: Optional[str] = None
    pet_weight: Optional[float] = None
    is_neutered: Optional[str] = None
    pet_age: Optional[int] = None

# --- 4. 챗봇 API 엔드포인트 정의 ---
@app.post("/chat")
async def chat_with_pet_persona(request: ChatRequest):
    """
    사용자 질문과 반려동물 종류 및 상세 정보에 따라 페르소나 챗봇 응답을 생성합니다.
    """
    user_input = request.user_input
    pet_type = request.pet_type
    pet_name = request.pet_name
    pet_kind = request.pet_kind # pet_kind 유지
    pet_gender = request.pet_gender
    pet_weight = request.pet_weight
    is_neutered = request.is_neutered
    pet_age = request.pet_age

    if pet_type == "강아지":
        persona_instruction = persona_dog_instruction
        prefix = "[DOG_CHAT] "
    elif pet_type == "고양이":
        persona_instruction = persona_cat_instruction
        prefix = "[CAT_CHAT] "
    else:
        # 유효하지 않은 pet_type일 경우 에러 반환
        raise HTTPException(status_code=400, detail="Invalid pet_type. Must be '강아지' or '고양이'.")

    # 모델에 전달할 프롬프트 구성 (KoAlpaca 파인튜닝 포맷 활용)
    pet_info_str = ""
    if pet_name:
        pet_info_str += f" 나의 반려동물 {pet_name}"
        if pet_kind: # pet_kind 사용
            pet_info_str += f"({pet_kind})"
    elif pet_kind: # pet_kind 사용
        pet_info_str += f" 나의 반려동물 {pet_kind}"

    # 추가 반려동물 정보 프롬프트에 포함
    additional_pet_info = []
    if pet_gender:
        additional_pet_info.append(f"성별: {pet_gender}")
    if pet_weight:
        additional_pet_info.append(f"몸무게: {pet_weight}kg")
    if is_neutered:
        additional_pet_info.append(f"중성화 여부: {is_neutered}")
    if pet_age:
        additional_pet_info.append(f"나이: {pet_age}살")

    if additional_pet_info:
        pet_info_str += " (" + ", ".join(additional_pet_info) + ")"

    prompt = f"""### Instruction:
{persona_instruction}
{pet_info_str} 다음 질문에 응답하세요:
## 사용자 질문:
{user_input}
### Response:"""

    try:
        # 모델 추론
        with torch.no_grad(): # 추론 시에는 기울기 계산을 비활성화하여 메모리 절약 및 속도 향상
            outputs = text_pipeline(prompt)
        
        chatbot_response = outputs[0]['generated_text'].strip()

        # --- 추가적인 후처리: 불필요한 학습 데이터 패턴 잘라내기 ---
        if "### Instruction:" in chatbot_response:
            chatbot_response = chatbot_response.split("### Instruction:")[0].strip()
        if "### Response:" in chatbot_response:
            chatbot_response = chatbot_response.split("### Response:")[0].strip()
        if "사용자:" in chatbot_response:
            chatbot_response = chatbot_response.split("사용자:")[0].strip()
        
        # --- action_tag 및 [ACTION: ...] 패턴 필터링 추가 ---
        # 기존 <action_tag>와 action_tag 단어 제거
        chatbot_response = chatbot_response.replace("<action_tag>", "").replace("</action_tag>", "").strip()
        chatbot_response = chatbot_response.replace("action_tag", "").strip()
        
        # [ACTION: ...] 형식의 패턴 제거
        while True:
            start_idx = chatbot_response.find("[ACTION:")
            if start_idx == -1:
                break
            end_idx = chatbot_response.find("]", start_idx)
            if end_idx == -1: # Malformed tag, just remove the start part and break
                chatbot_response = chatbot_response[:start_idx] + chatbot_response[start_idx + len("[ACTION:"):]
                break
            chatbot_response = chatbot_response[:start_idx] + chatbot_response[end_idx + 1:].strip()
            chatbot_response = chatbot_response.strip() # 재정리

        return {"generated_response": prefix + chatbot_response}

    except Exception as e:
        # 추론 중 오류 발생 시 500 Internal Server Error 반환
        raise HTTPException(status_code=500, detail=f"챗봇 응답 생성 중 오류 발생: {e}")

# Hugging Face Space는 이 파일을 자동으로 실행합니다.
# Uvicorn 같은 WSGI 서버가 FastAPI 앱을 구동합니다.