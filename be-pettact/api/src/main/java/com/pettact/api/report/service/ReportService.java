package com.pettact.api.report.service;

import com.pettact.api.report.Repository.ReportRepository;
import com.pettact.api.report.dto.ReportCreateDto;
import com.pettact.api.report.dto.ReportResponseDto;
import com.pettact.api.report.entity.Report;
import com.pettact.api.user.entity.Users;
import com.pettact.api.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private UserRepository userRepository;


    // user
    @Transactional
    public ReportResponseDto submitReport(ReportCreateDto createDto, Long userNo) {

        Users users = userRepository.findById(userNo)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        if (createDto.getReportTargetNo() == null || createDto.getReportTargetNo()<=0 ||
                createDto.getReportTargetLocation() == null) {
            throw new IllegalArgumentException("신고 대상을 찾을 수 없습니다.");
        }

        boolean duplicateReport = reportRepository.isDuplicate
                (userNo, createDto.getReportTargetLocation(), createDto.getReportTargetNo());
        if (duplicateReport) {
            throw new IllegalArgumentException("이미 신고한 대상입니다. 신고는 한번만 가능합니다.");
        }

        Report report = createDto.toEntity(users);
        Report savedReport = reportRepository.save(report);
        return ReportResponseDto.fromEntity(savedReport);
    }

    public ReportResponseDto getMyReport(Long reportNo, Long userNo) {
        Report report = reportRepository.findById(reportNo)
                .orElseThrow(() -> new IllegalArgumentException("신고 내역을 찾을 수 없습니다."));

        if(!report.getUsers().getUserNo().equals(userNo)) {
            throw new IllegalArgumentException("본인의 신고 내역만 조회할 수 있습니다.");
        }
        return ReportResponseDto.fromEntity(report);
    }

    public List<ReportResponseDto> getListReport(Long userNo) {
        List<Report> reports = reportRepository.findByReports(userNo);

        return reports.stream()
                .map(ReportResponseDto::fromEntity)
                .collect(Collectors.toList());
    }


    // admin
    public List<ReportResponseDto> getAdminListReport(Report.ReportTargetLocation location, Integer status, String reason, LocalDate startDate, LocalDate endDate) {

        List<Report> reports = reportRepository.findAllWithFilters(location, status, reason, startDate, endDate);

        return reports.stream()
                .map(ReportResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public ReportResponseDto getReportDetail(Long reportNo) {
        Report report = reportRepository.findById(reportNo)
                .orElseThrow(() -> new IllegalArgumentException("해당 번호의 신고를 찾을 수 없습니다."));
        return ReportResponseDto.fromEntity(report);
    }

    @Transactional
    public ReportResponseDto updateReport(Long reportNo, Integer status) {

        Report report = reportRepository.findById(reportNo)
                .orElseThrow(() -> new IllegalArgumentException("해당 번호의 신고를 찾을 수 없습니다."));
        if (status <0 || status > 2) {
            throw new IllegalArgumentException("올바르지 않은 상태값입니다.");
        }
        report.setReportStatus(status);
        Report savedReport = reportRepository.save(report);
        return ReportResponseDto.fromEntity(savedReport);
    }

    @Transactional
    public void deleteReport(Long reportNo) {

        Report report = reportRepository.findById(reportNo)
                .orElseThrow(() -> new IllegalArgumentException("해당 번호의 신고를 찾을 수 없습니다."));
        if (report.getReportStatus() != null && report.getReportStatus() == 0) {
            throw new IllegalStateException("처리 중인 신고는 삭제할 수 없습니다.");
        }
        reportRepository.deleteById(reportNo);
    }

}
