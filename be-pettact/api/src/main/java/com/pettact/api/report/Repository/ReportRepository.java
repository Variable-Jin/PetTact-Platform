package com.pettact.api.report.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pettact.api.report.entity.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query("SELECT COUNT(r) > 0 FROM Report r WHERE r.users.userNo = :userNo AND r.reportTargetLocation = :reportTargetLocation AND r.reportTargetNo = :targetNo")
    boolean isDuplicate(@Param("userNo") Long userNo,
                        @Param("reportTargetLocation") Report.ReportTargetLocation reportTargetLocation,
                        @Param("targetNo") Long reportTargetNo);

    @Query("SELECT r FROM Report r WHERE r.users.userNo = :userNo ORDER BY r.createdAt DESC")
    List<Report> findByReports(@Param("userNo") Long userNo);

    @Query("""
    		SELECT r FROM Report r
    		JOIN FETCH r.users u
    		WHERE (:reportTargetLocation IS NULL OR r.reportTargetLocation = :reportTargetLocation)
    		AND (:status IS NULL OR r.reportStatus = :status)
    		AND (:reason IS NULL OR r.reportReason LIKE %:reason%)
    		AND (:startDate IS NULL OR DATE(r.createdAt) >= :startDate)
    		AND (:endDate IS NULL OR DATE(r.createdAt) <= :endDate)
    		ORDER BY r.createdAt DESC
    		""")
	List<Report> findAllWithFilters(
	    @Param("reportTargetLocation") Report.ReportTargetLocation location,
	    @Param("status") Integer status,
	    @Param("reason") String reason,
	    @Param("startDate") LocalDate startDate,
	    @Param("endDate") LocalDate endDate
	);

    @Query("SELECT COUNT(r) FROM Report r WHERE r.createdAt BETWEEN :startDate AND :endDate")
    Long countReportsBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

}
