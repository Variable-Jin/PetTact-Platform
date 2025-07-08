<template>
  <div class="container mt-5" style="max-width: 700px;">
    <h2 class="text-center mb-4 fw-bold">회원가입</h2>
    <hr class="flex-grow-1">
    <form @submit.prevent="handleJoin">
      <!-- 이메일 -->
      <div class="mb-3 row align-items-center">
        <label for="userEmail" class="col-sm-3 col-form-label">이메일 <span class="text-danger">*</span></label>
        <div class="col-sm-9">
          <div class="input-group">
            <input type="email" v-model="userEmail" id="userEmail" class="form-control" placeholder="이메일을 입력해주세요"
              required>
            <button v-if="!isEmailChecked" type="button" class="btn btn-outline-secondary"
              @click="checkEmailDuplication">
              중복 확인
            </button>

            <button v-else-if="!emailSent" type="button" class="btn btn-outline-primary" @click="sendVerificationEmail">
              인증메일 발송
            </button>

            <button v-else-if="!emailVerified" type="button" class="btn btn-success" @click="refreshEmailStatus">
              인증완료 확인
            </button>
          </div>

          <!-- 안내 문구 -->
          <div class="form-text mt-1">
            <span v-if="!isEmailChecked" class="text-muted">이메일 중복 여부를 확인해주세요.</span>
            <span v-else-if="!emailSent" class="text-success">중복확인이 완료되었습니다. 인증메일을 발송해주세요.</span>
            <span v-else-if="!emailVerified" class="text-warning">인증메일을 발송했습니다. 메일함에서 인증 후 '인증완료 확인'을 눌러주세요.</span>
            <span v-else class="text-success">✅ 이메일 인증이 완료되었습니다.</span>
          </div>
        </div>
      </div>

      <!-- 비밀번호 -->
      <div class="mb-3 row align-items-center">
        <label for="userPassword" class="col-sm-3 col-form-label">비밀번호 <span class="text-danger">*</span></label>
        <div class="col-sm-9">
          <input type="password" v-model="userPassword" id="userPassword" class="form-control"
            placeholder="비밀번호를 입력해주세요" required>
        </div>
      </div>

      <!-- 이름 -->
      <div class="mb-3 row align-items-center">
        <label for="userName" class="col-sm-3 col-form-label">이름 <span class="text-danger">*</span></label>
        <div class="col-sm-9">
          <input type="text" v-model="userName" id="userName" class="form-control" required>
        </div>
      </div>

      <!-- 닉네임 -->
      <div class="mb-3 row align-items-center">
        <label for="userNickname" class="col-sm-3 col-form-label">닉네임 <span class="text-danger">*</span></label>
        <div class="col-sm-9">
          <div class="input-group">
            <input type="text" v-model="userNickname" id="userNickname" class="form-control" required>
            <button type="button" class="btn btn-outline-secondary" @click="checkNicknameDuplication">중복 확인</button>
          </div>
          <span v-if="isNicknameChecked" class="text-success">✓ 중복확인 완료</span>
        </div>
      </div>

      <!-- 휴대폰 번호 -->
      <div class="mb-3 row align-items-center">
        <label for="userTel" class="col-sm-3 col-form-label">휴대폰 번호 <span class="text-danger">*</span></label>
        <div class="col-sm-9">
          <input type="tel" v-model="userTel" id="userTel" class="form-control" placeholder="숫자만 입력해주세요" required>
        </div>
      </div>

      <!-- 생년월일 -->
      <div class="mb-3 row align-items-center">
        <label for="userBirth" class="col-sm-3 col-form-label">생년월일 <span class="text-danger">*</span></label>
        <div class="col-sm-9">
          <input type="date" v-model="userBirth" id="userBirth" class="form-control" required>
        </div>
      </div>

      <!-- 우편번호 -->
      <div class="mb-3 row align-items-center">
        <label for="userZipcode" class="col-sm-3 col-form-label">우편번호</label>
        <div class="col-sm-9">
          <div class="input-group">
            <input type="text" v-model="userZipcode" id="userZipcode" class="form-control" readonly
              placeholder="우편번호를 입력해주세요">
            <button type="button" class="btn btn-outline-secondary" @click="openPostcode">찾기</button>
          </div>
        </div>
      </div>

      <!-- 주소 -->
      <div class="mb-3 row align-items-center">
        <label for="userStreet1" class="col-sm-3 col-form-label">주소</label>
        <div class="col-sm-9">
          <input type="text" v-model="userStreet1" id="userStreet1" class="form-control" readonly
            placeholder="주소를 입력해주세요">
        </div>
      </div>

      <!-- 상세주소 -->
      <div class="mb-3 row align-items-center">
        <label for="userDetailAddress" class="col-sm-3 col-form-label">상세주소</label>
        <div class="col-sm-9">
          <input type="text" v-model="userDetailAddress" id="userDetailAddress" class="form-control"
            placeholder="상세주소를 입력해주세요">
        </div>
      </div>

      <hr class="flex-grow-1">

      <!-- 약관동의 -->
      <div class="mb-4">
        <div class="card">
          <div class="card-body">
            <label class="form-label fw-bold mb-2">약관동의 <span class="text-danger">*</span></label>

            <!-- 전체 동의 -->
            <div class="form-check mb-3">
              <input class="form-check-input" type="checkbox" v-model="allAgree" id="allAgree" @change="toggleAll">
              <label class="form-check-label fw-bold" for="allAgree">전체 동의합니다.</label>
            </div>

            <!-- 개별 약관 -->
            <div class="ms-3">
              <!-- 이용약관 -->
              <div class="form-check">
                <input class="form-check-input" type="checkbox" v-model="agreeTerms" id="agreeTerms">
                <label class="form-check-label" for="agreeTerms">
                  이용약관 동의 (필수)
                </label>
              </div>
              <div class="bg-light p-2 mb-3 small rounded">
                본 서비스는 연습용으로 제공되며, 회원 정보는 학습 목적으로만 사용됩니다.
              </div>

              <!-- 개인정보 처리방침 -->
              <div class="form-check">
                <input class="form-check-input" type="checkbox" v-model="agreePrivacy" id="agreePrivacy">
                <label class="form-check-label" for="agreePrivacy">
                  개인정보 처리방침 동의 (필수)
                </label>
              </div>
              <div class="bg-light p-2 mb-3 small rounded">
                수집된 정보는 연습용 DB에 저장되며, 제3자에게 제공되지 않습니다.
              </div>

              <!-- 마케팅 수신 동의 -->
              <div class="form-check">
                <input class="form-check-input" type="checkbox" v-model="agreeMarketing" id="agreeMarketing">
                <label class="form-check-label" for="agreeMarketing">
                  마케팅 정보 수신 동의 (선택)
                </label>
              </div>
              <div class="bg-light p-2 small rounded">
                이벤트, 공지사항, 프로모션 정보를 이메일로 수신합니다.
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 회원가입 버튼 -->
      <div class="mb-4 mt-3">
        <button type="submit" class="btn btn-primary w-100 py-2">회원가입</button>
      </div>
      <!-- 에러 메시지 -->
      <div v-if="errorMessage" class="mt-3 text-danger">
        {{ errorMessage }}
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()

const userEmail = ref('')
const userPassword = ref('')
const userName = ref('')
const userNickname = ref('')
const userTel = ref('')
const userBirth = ref('')
const userZipcode = ref('')
const userStreet1 = ref('')
const userDetailAddress = ref('')
const userHasPet = ref(false)
// TODO: 이메일 수신여부(userEmailChecked) 추가 -> isEmailChecked랑 변수 헷갈리지않기,,,

const isEmailChecked = ref(false)
const emailVerified = ref(false)
const emailSent = ref(false)
const isNicknameChecked = ref(false)

const allAgree = ref(false)
const agreeTerms = ref(false)
const agreePrivacy = ref(false)
const agreeMarketing = ref(false)

const errorMessage = ref('')

// 이메일 변경 시 모든 상태 초기화
watch(userEmail, () => {
  isEmailChecked.value = false
  emailVerified.value = false
  emailSent.value = false
})

// 닉네임 변경 시 중복확인 초기화
watch(userNickname, () => {
  isNicknameChecked.value = false
})

const isValidEmail = (email) => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
};

// 이메일 중복확인
const checkEmailDuplication = async () => {
  if (!isValidEmail(userEmail.value)) {
    alert('올바른 이메일 형식을 입력해주세요.');
    return;
  }
  try {
    const response = await axios.get(`/v1/user/email/check?email=${userEmail.value}`)
    if (response.data) {
      alert('이미 사용 중인 이메일입니다.')
      isEmailChecked.value = false
    } else {
      alert('사용 가능한 이메일입니다.')
      isEmailChecked.value = true
    }
  } catch (error) {
    console.error(error)
    alert('이메일 중복 확인 실패')
    isEmailChecked.value = false
  }
}

// 이메일 인증 메일 발송
const sendVerificationEmail = async () => {
  if (!isValidEmail(userEmail.value)) {
    alert('올바른 이메일 형식을 입력해주세요.');
    return;
  }
  if (!isEmailChecked.value) {
    alert("중복확인을 먼저 진행해주세요.")
    return;
  }
  try {
    await axios.post('/v1/user/email/send', null, { params: { userEmail: userEmail.value } })
    alert('인증 메일을 발송했습니다.')
    emailSent.value = true
  } catch (error) {
    console.error(error)
    alert('인증 메일 발송 실패')
  }
}

// 이메일 인증 상태 갱신
const refreshEmailStatus = async () => {
  try {
    const response = await axios.get(`/v1/user/email/verified?email=${userEmail.value}`)
    emailVerified.value = response.data

    if (emailVerified.value) {
      alert('이메일 인증이 확인되었습니다!')
    } else {
      alert('아직 인증이 완료되지 않았습니다. 메일함을 다시 확인해주세요.')
    }
  } catch (error) {
    console.error(error)
    alert('인증 상태 확인 실패')
  }
}

// 닉네임 중복확인
const checkNicknameDuplication = async () => {
  try {
    const response = await axios.get(`/v1/user/nickname/check?nickname=${userNickname.value}`)
    if (response.data) {
      alert('이미 사용 중인 닉네임입니다.')
      isNicknameChecked.value = false
    } else {
      alert('사용 가능한 닉네임입니다.')
      isNicknameChecked.value = true
    }
  } catch (error) {
    console.error(error)
    alert('닉네임 중복 확인 실패')
    isNicknameChecked.value = false
  }
}

// 우편번호 api
const openPostcode = () => {
  new window.daum.Postcode({
    oncomplete: function (data) {
      userZipcode.value = data.zonecode;
      userStreet1.value = data.address;
      document.getElementById("userDetailAddress").focus();
    }
  }).open();
};

// 회원가입 핸들러
const handleJoin = async () => {
  errorMessage.value = ''

  // 입력값 검증
  if (!isEmailChecked.value) {
    errorMessage.value = '이메일 중복확인을 해주세요.'
    return
  }

  if (!emailVerified.value) {
    errorMessage.value = '이메일 인증을 완료해주세요.'
    return
  }

  if (!isNicknameChecked.value) {
    errorMessage.value = '닉네임 중복확인을 해주세요.'
    return
  }

  // 비밀번호 정규식 검증
  const pwRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*?&]).{8,}$/
  if (!pwRegex.test(userPassword.value)) {
    errorMessage.value = '비밀번호는 영문자, 숫자, 특수문자를 포함해 8자 이상이어야 합니다.'
    return
  }

  try {
    await axios.post('/v1/user/join', {
      userEmail: userEmail.value,
      userPassword: userPassword.value,
      userName: userName.value,
      userNickname: userNickname.value,
      userTel: userTel.value,
      userBirth: userBirth.value,
      userZipcode: userZipcode.value,
      userStreet1: userStreet1.value,
      userDetailAddress: userDetailAddress.value,
      userHasPet: userHasPet.value,
      userEmailChecked: true
    })
    alert('회원가입이 완료되었습니다.')

    router.push({ name: 'login' })
  } catch (error) {
    console.error('회원가입 실패:', error)
    errorMessage.value = error.response?.data || '회원가입 실패. 입력값을 확인하세요.'
  }
}

// 전체 동의 토글
const toggleAll = () => {
  agreeTerms.value = allAgree.value
  agreePrivacy.value = allAgree.value
  agreeMarketing.value = allAgree.value
}

// 개별 동의 변경 시 전체 동의 체크 해제/선택
watch([agreeTerms, agreePrivacy, agreeMarketing], ([terms, privacy, marketing]) => {
  if (terms && privacy && marketing) {
    allAgree.value = true
  } else {
    allAgree.value = false
  }
})
</script>