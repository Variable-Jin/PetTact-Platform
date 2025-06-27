package com.pettact.api.admin.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pettact.api.admin.dto.BoardStatsDTO;
import com.pettact.api.admin.dto.DailyUserStatsDTO;
import com.pettact.api.admin.dto.DashboardStatsDTO;
import com.pettact.api.admin.dto.UserRegStatsDTO;
import com.pettact.api.admin.service.AdminDashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/stats")
public class AdminDashboardController {
	private final AdminDashboardService dashboardService;
	
	// 전체 대시보드용
	@GetMapping("/summary")
	public DashboardStatsDTO getDashBoardStats() {
		return dashboardService.getDashboardStats();
	}

	// 가입자 통계
	@GetMapping("/user/registration")
	public UserRegStatsDTO getUserRegStats() {
		return dashboardService.getUserRegStats();
	}
	
	// 일별 가입자 통계(그래프 출력용)
	@GetMapping("/user/daily")
	public List<DailyUserStatsDTO> getDailyUserStats(@RequestParam(value = "days", defaultValue = "7") int days) {
		return dashboardService.getDailyUserStats(days);
	}
	
	// 게시물 통계
	@GetMapping("/board")
	public BoardStatsDTO getBoardStats() {
		return dashboardService.getBoardStats();
	}
}
