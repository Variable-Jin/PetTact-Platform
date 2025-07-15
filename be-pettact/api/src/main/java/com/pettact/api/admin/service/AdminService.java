package com.pettact.api.admin.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pettact.api.admin.dto.board.AdminBoardDetailDTO;
import com.pettact.api.admin.dto.board.AdminBoardListDTO;
import com.pettact.api.admin.dto.report.AdminReportListDTO;
import com.pettact.api.admin.dto.user.AdminUserDetailDTO;
import com.pettact.api.admin.dto.user.AdminUserListDTO;
import com.pettact.api.board.entity.Board;
import com.pettact.api.board.repository.BoardRepository;
import com.pettact.api.code.entity.CommonCode;
import com.pettact.api.code.service.CommonCodeService;
import com.pettact.api.common.dto.PageResponseDto;
import com.pettact.api.notification.dto.NotificationReqDTO;
import com.pettact.api.notification.enums.NotificationType;
import com.pettact.api.notification.enums.TargetType;
import com.pettact.api.notification.service.NotificationService;
import com.pettact.api.report.Repository.ReportRepository;
import com.pettact.api.report.entity.Report;
import com.pettact.api.user.entity.Users;
import com.pettact.api.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {
	private final UserRepository userRepository;
	private final CommonCodeService commonCodeService;
	private final BoardRepository boardRepository;
	private final NotificationService notificationService;
	private final ReportRepository reportRepository;
	
	// 회원 목록 조회
	public PageResponseDto<AdminUserListDTO> getUserList(
	        String keyword, String status, String role,
	        LocalDateTime startDate, LocalDateTime endDate,
	        int page, int size) {

	    Pageable pageable = PageRequest.of(page - 1, size); // 0-based

	    Page<Users> result = userRepository.findUsersWithFilters(
	        (keyword == null || keyword.isBlank()) ? null : keyword,
	        (status == null || status.isBlank()) ? null : status,
	        (role == null || role.isBlank()) ? null : role,
	        startDate, endDate, pageable
	    );

	    List<AdminUserListDTO> dtoList = result.getContent()
	                                           .stream()
	                                           .map(AdminUserListDTO::from)
	                                           .toList();

	    return new PageResponseDto<>(dtoList, (int) result.getTotalElements(), page, size);
	}


    // 회원 정보 상세보기
	public AdminUserDetailDTO getUserDetail(Long userNo) {
	    Users user = userRepository.findById(userNo)
	            .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

	    return AdminUserDetailDTO.from(user);
	}
    
	// 회원 잠금
	@Transactional
	public boolean lockUserByUserNo(Long userNo) {
	    Users user = userRepository.findById(userNo)
	            .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

	    CommonCode blockedCode = commonCodeService.getCodeById("STATUS_BLOCKED");
	    
	    if ("STATUS_BLOCKED".equals(user.getStatusCode())) {
	        return false;
	    }

	    user.setStatusCode(blockedCode);
	    userRepository.save(user);
	    return true;
	}
	
	// 회원 잠금 해제
	@Transactional
	public boolean unlockUserByUserNo(Long userNo) {
	    Users user = userRepository.findById(userNo)
	            .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

	    CommonCode activeCode = commonCodeService.getCodeById("STATUS_ACTIVE");
	    
	    if (!"STATUS_BLOCKED".equals(user.getStatusCode())) {
	        return false;
	    }

	    user.setStatusCode(activeCode);
	    userRepository.save(user);
	    return true;
	}
	
	// 판매자 목록 조회
	public PageResponseDto<AdminUserListDTO> getSellerList(
		    String keyword, String status,
		    LocalDateTime startDateTime, LocalDateTime endDateTime,
		    int page, int size
	) {
	    Pageable pageable = PageRequest.of(page - 1, size);
	    Page<Users> sellers = userRepository.findSellersWithFilters(
	        keyword, status, startDateTime, endDateTime, pageable
	    );

	    List<AdminUserListDTO> dtoList = sellers.getContent().stream()
	        .map(AdminUserListDTO::from)
	        .toList();

	    return new PageResponseDto<>(dtoList, (int) sellers.getTotalElements(), page, size);
	}



	// 판매자 권한 요청 대기중 목록
	public PageResponseDto<AdminUserListDTO> getSellerRequests(int page, int size) {
	    Pageable pageable = PageRequest.of(page - 1, size);
	    Page<Users> requests = userRepository.findUsersWithFilters(
	        null, "STATUS_PENDING", "ROLE_USER", null, null, pageable
	    );

	    List<AdminUserListDTO> dtoList = requests.getContent().stream()
	        .map(AdminUserListDTO::from)
	        .toList();

	    return new PageResponseDto<>(dtoList, (int) requests.getTotalElements(), page, size);
	}
	
	// 판매자 권한 승인
	public boolean approveSellerByUserNo(Long userNo, Long adminUserNo) {
	    Users user = userRepository.findById(userNo)
	            .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

	    CommonCode sellerRoleCode = commonCodeService.getCodeById("ROLE_SELLER");
	    CommonCode activeStatusCode = commonCodeService.getCodeById("STATUS_ACTIVE");

	    if ("ROLE_SELLER".equals(user.getRoleCode())) {
	        throw new IllegalArgumentException("이미 판매자 권한이 있습니다.");
	    }

	    user.setRoleCode(sellerRoleCode);
	    user.setStatusCode(activeStatusCode);
	    userRepository.save(user);

	    NotificationReqDTO dto = NotificationReqDTO.of(
	        adminUserNo,
	        userNo,
	        NotificationType.APPROVAL,
	        userNo,
	        TargetType.USER,
	        "판매자 권한 승인 완료",
	        "판매자 권한이 성공적으로 승인되었습니다."
	    );

	    notificationService.sendNotification(dto);

	    return true;
	}

	// 게시물 목록 조회
	public PageResponseDto<AdminBoardListDTO> getBoardList(
		    String keyword, Long categoryNo, Boolean isDeleted,
		    LocalDateTime startDateTime, LocalDateTime endDateTime,
		    int page, int size
	) {
	    Pageable pageable = PageRequest.of(page - 1, size);
	    Page<Board> boardList = boardRepository.findBoardsWithFilters(
	        keyword, categoryNo, isDeleted, startDateTime, endDateTime, pageable
	    );

	    List<AdminBoardListDTO> dtoList = boardList.getContent().stream()
	        .map(AdminBoardListDTO::from)
	        .toList();

	    return new PageResponseDto<>(dtoList, (int) boardList.getTotalElements(), page, size);
	}

	public AdminBoardDetailDTO getBoardDetail(Long boardNo) {
	    Board board = boardRepository.findByBoardNo(boardNo);

	    if (board == null) {
	        throw new IllegalArgumentException("해당 게시물이 존재하지 않습니다.");
	    }

	    return AdminBoardDetailDTO.from(board);
	}

	
}
