package com.pettact.api.report.controller;

import com.pettact.api.report.dto.ReportCreateDto;
import com.pettact.api.report.dto.ReportResponseDto;
import com.pettact.api.report.service.ReportService;
import com.pettact.api.security.vo.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * user
     * ! url 주소는 해당 사항에 맞게 변경
     * 1. 신고 신청
     * 2. 상세 신고 내역 조획
     * 3. my 신고 list
     * @param createDto
     * @return
     */

    @PostMapping
    public ResponseEntity<ReportResponseDto> submitReport(@RequestBody ReportCreateDto createDto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userNo = userDetails.getUser().getUserNo();
        ReportResponseDto responseDto = reportService.submitReport(createDto, userNo);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/my/{reportNo}")
    public ResponseEntity<ReportResponseDto> getMyReport(@PathVariable Long reportNo,
                                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userNo = userDetails.getUser().getUserNo();
        ReportResponseDto responseDto = reportService.getMyReport(reportNo, userNo);
        return ResponseEntity.ok(responseDto);
    }


    @GetMapping("/my")
    public ResponseEntity<List<ReportResponseDto>> getListReport(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userNo = userDetails.getUser().getUserNo();
        List<ReportResponseDto> responseDto = reportService.getListReport(userNo);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * admin
     * -> 추후 도메인 변경 고려 plz,,
     * 1. 신고 list 조회
     * 2. 신고 {reportNo} 조회
     * 3. 신고 처리 -> status change
     * 4. 신고 삭제
     */



}
