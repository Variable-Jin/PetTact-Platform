package com.pettact.api.admin.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.pettact.api.admin.dto.AdminBoardDetailDTO;
import com.pettact.api.admin.dto.AdminBoardListDTO;
import com.pettact.api.admin.dto.AdminUserDetailDTO;
import com.pettact.api.admin.dto.AdminUserListDTO;
import com.pettact.api.board.entity.Board;
import com.pettact.api.board.repository.BoardRepository;
import com.pettact.api.code.entity.CommonCode;
import com.pettact.api.code.service.CommonCodeService;
import com.pettact.api.user.entity.Users;
import com.pettact.api.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {
	private final UserRepository userRepository;
	private final CommonCodeService commonCodeService;
	private final BoardRepository boardRepository;
	
	// 회원 목록 조회
    public List<AdminUserListDTO> getUserList(String keyword, String status, String role,
            							LocalDateTime startDate, LocalDateTime endDate) {

    	List<Users> users = userRepository.findUsersWithFilters(
		(keyword == null || keyword.isBlank()) ? null : keyword,
		(status == null || status.isBlank()) ? null : status,
		(role == null || role.isBlank()) ? null : role,
		startDate,
		endDate
		);
		
		return users.stream()
					.map(AdminUserListDTO::from)
					.collect(Collectors.toList());
	}

    // 회원 정보 상세보기
	public AdminUserDetailDTO getUserDetail(Long userNo) {
	    Users user = userRepository.findById(userNo)
	            .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

	    return AdminUserDetailDTO.from(user);
	}
    
	// 회원 잠금
	public boolean lockUserByUserNo(Long userNo) {
	    Users user = userRepository.findById(userNo)
	            .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

	    if (user.getUserBlacklist()) {
	        return false;
	    }

	    user.setUserBlacklist(true);
	    userRepository.save(user);

	    return true;
	}
	
	// 회원 잠금 해제
	public boolean unlockUserByUserNo(Long userNo) {
	    Users user = userRepository.findById(userNo)
	            .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

	    if (!user.getUserBlacklist()) {
	        return false;
	    }

	    user.setUserBlacklist(false);
	    userRepository.save(user);

	    return true;
	}
	
	// 판매자 목록 조회
	public List<AdminUserListDTO> getSellerList(String keyword, String status,
											LocalDateTime startDateTime, LocalDateTime endDateTime) {
	    // ROLE_SELLER로 필터링된 사용자만 조회하기 위해 하드코딩해놓음
	    String role = "ROLE_SELLER";

	    List<Users> sellers = userRepository.findUsersWithFilters(
	        keyword,
	        status,
	        role,
	        startDateTime,
	        endDateTime
	    );
	    
	    return sellers.stream()
	                  .map(AdminUserListDTO::from)
	                  .toList();
	}

	// 판매자 권한 승인
	public boolean approveSellerByUserNo(Long userNo) {
	    Users user = userRepository.findById(userNo)
	            .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

	    if ("ROLE_SELLER".equals(user.getRoleCode())) {
	        return false; // 이미 판매자
	    }

	    CommonCode sellerRole = commonCodeService.getCodeById("ROLE_SELLER");
	    user.setRoleCode(sellerRole);
	    userRepository.save(user);

	    return true;
	}

	// 게시물 목록 조회
	public List<AdminBoardListDTO> getBoardList(String keyword, Long categoryNo, Boolean isDeleted,
            LocalDateTime startDateTime, LocalDateTime endDateTime) {

		List<Board> board = boardRepository.findBoardsWithFilters(
			(keyword == null || keyword.isBlank()) ? null : keyword,
			(categoryNo == null) ? null : categoryNo,
			(isDeleted == null) ? null : isDeleted,
			startDateTime,
			endDateTime
		);
		
		return board.stream()
					.map(AdminBoardListDTO::from)
					.collect(Collectors.toList());
	}

	public AdminBoardDetailDTO getBoardDetail(Long boardNo) {
	    Board board = boardRepository.findByBoardNo(boardNo);

	    if (board == null) {
	        throw new IllegalArgumentException("해당 게시물이 존재하지 않습니다.");
	    }

	    return AdminBoardDetailDTO.from(board);
	}



	
}
