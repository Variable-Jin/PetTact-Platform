package com.pettact.api.user.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pettact.api.common.dto.PageResponseDto;
import com.pettact.api.product.dto.ProductDTO;
import com.pettact.api.product.service.ProductService;
import com.pettact.api.report.dto.ReportResponseDto;
import com.pettact.api.report.service.ReportService;
import com.pettact.api.security.vo.CustomUserDetails;
import com.pettact.api.user.dto.EmailFindRequestDTO;
import com.pettact.api.user.dto.EmailFindResponseDTO;
import com.pettact.api.user.dto.PasswordResetDTO;
import com.pettact.api.user.dto.PasswordVerifyDTO;
import com.pettact.api.user.dto.SocialJoinDTO;
import com.pettact.api.user.dto.UserInfoDTO;
import com.pettact.api.user.dto.UserJoinDTO;
import com.pettact.api.user.dto.UserPatchDTO;
import com.pettact.api.user.entity.Users;
import com.pettact.api.user.service.UserService;
import com.pettact.api.verification.EmailService;
import com.pettact.api.verification.VerificationCodeStore;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/user")
public class UserController {
    private final UserService userService;
    private final EmailService emailService;
    private final VerificationCodeStore verificationCodeStore;
    private final ReportService reportService;
    private final ProductService productService;

    /* 회원가입 */
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody @Valid UserJoinDTO dto) {
        try {
            userService.join(dto);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("회원가입 중 오류가 발생했습니다. 다시 시도해주세요.");
        }
    }
    
    @PostMapping("/social/join")
    public ResponseEntity<?> saveSocialAdditionalInfo(@AuthenticationPrincipal CustomUserDetails userDetails,
            										@RequestBody @Valid SocialJoinDTO dto) {
        try {
            userService.updateSocialAdditionalInfo(userDetails.getUserEntity().getUserNo(), dto);
            return ResponseEntity.ok("회원가입이 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("회원가입 중 오류가 발생했습니다. 다시 시도해주세요.");
        }
    }

    /* 이메일 인증 요청 */
    @PostMapping("/email/send")
    public ResponseEntity<?> sendVerificationEmail(@RequestParam("userEmail") String userEmail){
    	String token = UUID.randomUUID().toString();
        verificationCodeStore.saveCode("email-token:" + token, userEmail);
        emailService.sendVerificationLink(userEmail, token);
        return ResponseEntity.ok("이메일 인증 링크를 전송했습니다.");
    }
    
    /* 이메일 인증 */
    @GetMapping("/email/verify")
    public void verifyEmail(@RequestParam("token") String token, HttpServletResponse response) throws IOException {
        String redisKey = "email-token:" + token;
        String email = verificationCodeStore.getCode(redisKey);
        System.out.println("[DEBUG] 인증 시도: redisKey=" + redisKey + ", email=" + email);
        if (email != null) {
            verificationCodeStore.saveCode("verified:" + email, "true");
            verificationCodeStore.remove(redisKey);
            response.sendRedirect("http://localhost:5173/user/email/verify?email=" + email);
        } else {
            response.sendRedirect("http://localhost:5173/");
        }
    }
    
    @GetMapping("/email/verified")
    public ResponseEntity<?> checkEmailVerified(@RequestParam("email") String userEmail) {
        String verified = verificationCodeStore.getCode("verified:" + userEmail);
        boolean isVerified = "true".equals(verified);
        return ResponseEntity.ok(isVerified);
    }

    /* 중복확인 */
    @GetMapping("/email/check")
    public ResponseEntity<?> checkEmail(@RequestParam("email") String userEmail) {
        boolean exists = userService.isEmailDuplicated(userEmail);
        return ResponseEntity.ok(exists);
    }
    
    @GetMapping("/nickname/check")
    public ResponseEntity<?> checkNickname(@RequestParam("nickname") String userNickname) {
        boolean exists = userService.isNicknameDuplicated(userNickname);
        return ResponseEntity.ok(exists);
    }
    
    /* 로그인한 user 정보 조회 */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails){
    	if(userDetails == null) {
    		return ResponseEntity.status(401).body("로그인이 필요합니다.");
    	}
    	
    	Users user = userDetails.getUserEntity();

        Map<String, Object> result = Map.of(
                "userEmail", user.getUserEmail(),
                "userNo", user.getUserNo(),
                "userNickname", user.getUserNickname(),
                "userRole", user.getRoleCode()
        );
        
        return ResponseEntity.ok(result);
    }
    
    /* email 조회 */
    @PostMapping("/email/find")
    public ResponseEntity<?> findEmail(@RequestBody EmailFindRequestDTO dto) {
        try {
            String userEmail = userService.findEmailByNameAndTel(dto.getUserName(), dto.getUserTel());
            return ResponseEntity.ok(new EmailFindResponseDTO(userEmail));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /* pwd 재설정 */
    @PostMapping("/password/send")
    public ResponseEntity<?> sendPasswordResetMail(@RequestParam("userEmail") String userEmail){
    	try {
    		userService.sendPasswordResetMail(userEmail);
    		return ResponseEntity.ok("비밀번호 재설정 링크를 이메일로 전송했습니다.");			
		} catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/password/verify")
    public ResponseEntity<?> verifyPasswordResetToken(@RequestParam("token") String token) {    	
    	String email = verificationCodeStore.getCode("password-reset-token:" + token);

        if (email == null) {
            return ResponseEntity.badRequest().body("유효하지 않거나 만료된 링크입니다.");
        }

        verificationCodeStore.saveCode("verified:" + email, "true");
        verificationCodeStore.remove("password-reset-token:" + token);
        
        return ResponseEntity.ok(Map.of("email", email, "message", "인증 성공"));
    }
    
    @PostMapping("/password/reset")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetDTO resetDTO) {
    	String email = verificationCodeStore.getCode("verified:" + resetDTO.getUserEmail());

    	if (email == null) {
    		return ResponseEntity.badRequest().body("유효하지 않거나 만료된 링크입니다.");
    	}
    	
    	userService.updatePassword(resetDTO.getUserEmail(), resetDTO.getNewPassword());
    	
    	verificationCodeStore.remove(email);
    	
    	return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }
    
    /* mypage */
    @GetMapping("/mypage/myInfo")
    public ResponseEntity<?> getUserDetail(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        Users user = userDetails.getUserEntity();

        UserInfoDTO dto = UserInfoDTO.builder()
            .userEmail(user.getUserEmail())
            .userName(user.getUserName())
            .userBirth(user.getUserBirth())
            .userNickname(user.getUserNickname())
            .userTel(user.getUserTel())
            .userZipcode(user.getUserZipcode())
            .userStreet1(user.getUserStreet1())
            .userDetailAddress(user.getUserDetailAddress())
            .build();

        return ResponseEntity.ok(dto);
    }
    
    @PostMapping("/mypage/verify-password")
    public ResponseEntity<?> verifyUserPassword(@AuthenticationPrincipal CustomUserDetails userDetails,
    										@RequestBody @Valid PasswordVerifyDTO dto) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }
        
        if (dto.getInputPassword() == null || dto.getInputPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("비밀번호를 입력해주세요.");
        }
        
        try {
        	boolean isVerified = userService.verifyUserPassword(userDetails.getUserEntity().getUserNo(), dto.getInputPassword());
        	
            if (isVerified) {
                return ResponseEntity.ok("비밀번호가 일치합니다.");
            } else {
                return ResponseEntity.status(400).body("비밀번호가 일치하지 않습니다.");
            }
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PatchMapping("/mypage/update")
    public ResponseEntity<?> updateUserInfo(@AuthenticationPrincipal CustomUserDetails userDetails,
    										@RequestBody @Valid UserPatchDTO userPatchDTO){
        if (userDetails == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }
        
        try {
        	userService.patchUserInfo(userDetails.getUserEntity().getUserNo(), userPatchDTO);
            return ResponseEntity.ok("회원 정보가 성공적으로 수정되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /* 탈퇴(soft delete) */
    @PostMapping("/mypage/withdraw")
    public ResponseEntity<?> withdraw(@AuthenticationPrincipal CustomUserDetails userDetails){
        if (userDetails == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }
        
        try {
        	userService.withdrawUser(userDetails.getUserEntity().getUserNo());
            return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /* seller 권한 */
    @PostMapping("/seller/request")
    public ResponseEntity<?> requestSeller(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        try {
            userService.requestSeller(userDetails.getUserEntity().getUserNo());
            return ResponseEntity.ok("판매자 권한 요청이 접수되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/seller/request/status")
    public ResponseEntity<?> getSellerRequestStatus(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        String status = userService.getSellerRequestStatus(userDetails.getUserEntity().getUserNo());
        return ResponseEntity.ok(Map.of("status", status));
    }
    
    /* my report */
    @GetMapping("/mypage/my-reports")
    public ResponseEntity<?> getMyReports(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        List<ReportResponseDto> reports = reportService.getListReport(userDetails.getUserEntity().getUserNo());
        return ResponseEntity.ok(reports);
    }
    
    @GetMapping("/mypage/my-report/{reportNo}")
    public ResponseEntity<ReportResponseDto> getMyReportDetail(@PathVariable("reportNo") Long reportNo,
            											@AuthenticationPrincipal CustomUserDetails userDetails) {

        ReportResponseDto dto = reportService.getMyReport(reportNo, userDetails.getUserEntity().getUserNo());
        return ResponseEntity.ok(dto);
    }

    /* my product */
	@GetMapping("/mypage/my-products")
	public ResponseEntity<PageResponseDto<ProductDTO>> getMyProducts(
			@AuthenticationPrincipal CustomUserDetails userDetails,
	        @RequestParam(name = "keyword", required = false) String keyword,
	        @RequestParam(name = "categoryNo", required = false) Long categoryNo,
	        @RequestParam(name = "sort", required = false) String sort,
	        @RequestParam(name = "page", defaultValue = "0") int page,
	        @RequestParam(name = "size", defaultValue = "10") int size
    ) {
		Long userNo = userDetails.getUserEntity().getUserNo();
	    Page<ProductDTO> myProducts = productService.getMyProducts(userNo, keyword, categoryNo, sort, page, size);
	    return ResponseEntity.ok(PageResponseDto.from(myProducts));
	}
}
