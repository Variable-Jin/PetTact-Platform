package com.pettact.api.report.Repository;

import com.pettact.api.report.entity.Report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query("SELECT COUNT(r) > 0 FROM Report r WHERE r.users.userNo = :userNo AND r.reportTargetLocation = :reportTargetLocation AND r.reportTargetNo = :targetNo")
    boolean isDuplicate(@Param("userNo") Long userNo,
                        @Param("reportTargetLocation") Report.ReportTargetLocation reportTargetLocation,
                        @Param("targetNo") Long reportTargetNo);

    @Query("SELECT r FROM Report r WHERE r.users.userNo = :userNo ORDER BY r.createdAt DESC")
    List<Report> findByReports(@Param("userNo") Long userNo);

    @Query("SELECT r FROM Report r WHERE " +
            "(:reportTargetLocation IS NULL OR r.reportTargetLocation = :reportTargetLocation) AND " +
            "(:status IS NULL OR r.reportStatus = :status) AND " +
            "(:reason IS NULL OR r.reportReason LIKE CONCAT('%', :reason, '%')) AND " +
            "(:startDate IS NULL OR DATE(r.createdAt) >= :startDate) AND " +
            "(:endDate IS NULL OR DATE(r.createdAt) <= :endDate) " +
            "ORDER BY r.createdAt DESC")
    List<Report> findAllWithFilters(@Param("reportTargetLocation") Report.ReportTargetLocation location,
                                    @Param("status") Integer status,
                                    @Param("reason") String reason,
                                    @Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);

}
