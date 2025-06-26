package com.pettact.api.admin.service;

import static com.pettact.api.admin.util.DateRangeUtil.endOfLast7Days;
import static com.pettact.api.admin.util.DateRangeUtil.endOfThisMonthUntilToday;
import static com.pettact.api.admin.util.DateRangeUtil.endOfToday;
import static com.pettact.api.admin.util.DateRangeUtil.endOfYesterday;
import static com.pettact.api.admin.util.DateRangeUtil.startOfLast7Days;
import static com.pettact.api.admin.util.DateRangeUtil.startOfThisMonth;
import static com.pettact.api.admin.util.DateRangeUtil.startOfToday;
import static com.pettact.api.admin.util.DateRangeUtil.startOfYesterday;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.pettact.api.admin.dto.BoardStatsDTO;
import com.pettact.api.admin.dto.DailyUserStatsDTO;
import com.pettact.api.admin.dto.DashboardStatsDTO;
import com.pettact.api.admin.dto.UserRegStatsDTO;
import com.pettact.api.board.repository.BoardRepository;
import com.pettact.api.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {
	private final UserRepository userRepository;
	private final BoardRepository boardRepository;
	
	// 전체 사용자 통계
	public DashboardStatsDTO getDashboardStats() {
		// 날짜 계산용
	    long today = userRepository.countUsersBetween(startOfToday(), endOfToday());
	    long yesterday = userRepository.countUsersBetween(startOfYesterday(), endOfYesterday());
	    long weekly = userRepository.countUsersBetween(startOfLast7Days(), endOfLast7Days());
	    long monthly = userRepository.countUsersBetween(startOfThisMonth(), endOfThisMonthUntilToday());

        // 사용자 통계
        long totalUsers = userRepository.countTotalUsers();
        long activeUsers = userRepository.countActiveUsers();
        long totalSellers = userRepository.countActiveSellers();
        long pendingSellers = userRepository.countPendingSellers();

        // 증가율 계산
        double growthRate = (yesterday == 0) ? 100.0 : ((double)(today - yesterday) / yesterday) * 100;

        return DashboardStatsDTO.of(totalUsers, activeUsers, totalSellers, pendingSellers,
        								today, weekly, monthly, yesterday);
	}
	
	// 사용자 가입 통계
    public UserRegStatsDTO getUserRegStats() {
        long today = userRepository.countUsersBetween(startOfToday(), endOfToday());
        long yesterday = userRepository.countUsersBetween(startOfYesterday(), endOfYesterday());
        long weekly = userRepository.countUsersBetween(startOfLast7Days(), endOfLast7Days());
        long monthly = userRepository.countUsersBetween(startOfThisMonth(), endOfThisMonthUntilToday());

        return UserRegStatsDTO.of(today, yesterday, weekly, monthly);
    }
    
    // 차트 표시용 사용자 통계(
    public List<DailyUserStatsDTO> getDailyUserStats(int days) {
        List<DailyUserStatsDTO> statsList = new ArrayList<>();
        long runningTotal = 0;

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(LocalTime.MAX);

            long newUsers = userRepository.countUsersBetween(start, end);
            runningTotal += newUsers;

            statsList.add(DailyUserStatsDTO.of(date, newUsers, runningTotal));
        }

        return statsList;
    }
    
    // 게시물 통계
    public BoardStatsDTO getBoardStats() {
        long total = boardRepository.countTotalBoards();
        long newToday = boardRepository.countDailyNewBoards();
        long deletedToday = boardRepository.countDailyDeletedBoards();

        return BoardStatsDTO.of(total, newToday, deletedToday);
    }
}
