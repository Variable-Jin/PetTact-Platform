package com.pettact.api.board.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pettact.api.board.entity.Board;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
	
	// 필터링,검색 기능 포함한 게시물 목록 조회
	@Query("""
		    SELECT b FROM Board b
		    JOIN FETCH b.boardCategory c
		    JOIN FETCH b.user u
		    WHERE (:keyword IS NULL OR b.boardTitle LIKE CONCAT('%', :keyword, '%'))
		    AND (:categoryNo IS NULL OR c.boardCategoryNo = :categoryNo)
		    AND (:isDeleted IS NULL OR b.isDeleted = :isDeleted)
		    AND (:startDate IS NULL OR b.createdAt >= :startDate)
		    AND (:endDate IS NULL OR b.createdAt <= :endDate)
		    ORDER BY b.createdAt DESC
		""")
	List<Board> findBoardsWithFilters(
	    @Param("keyword") String keyword,
	    @Param("categoryNo") Long categoryNo,
	    @Param("isDeleted") Boolean isDeleted,
	    @Param("startDate") LocalDateTime startDate,
	    @Param("endDate") LocalDateTime endDate
	);

	// 게시물 상세보기
    @EntityGraph(attributePaths = {"boardCategory", "user"})
    Board findByBoardNo(Long boardNo);

    /* 대시보드 */
    @Query("SELECT COUNT(b) FROM Board b WHERE b.isDeleted = false")
    long countTotalBoards();

    @Query("SELECT COUNT(b) FROM Board b WHERE DATE(b.createdAt) = CURRENT_DATE")
	long countDailyNewBoards();

    @Query("SELECT COUNT(b) FROM Board b WHERE b.isDeleted = true AND DATE(b.deletedAt) = CURRENT_DATE")
	long countDailyDeletedBoards();

    @Query("SELECT b FROM Board b JOIN FETCH b.boardCategory")
    List<Board> findAllWithCategory();
    
    @Query("SELECT COUNT(b) FROM Board b WHERE b.createdAt BETWEEN :startDate AND :endDate")
    Long countBoardsBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT b FROM Board b WHERE b.isDeleted = true ORDER BY b.deletedAt DESC")
    List<Board> findRecentDeletedBoards(Pageable pageable);

    @Query("SELECT b FROM Board b WHERE b.isDeleted = true AND DATE(b.deletedAt) = CURRENT_DATE ORDER BY b.deletedAt DESC")
    List<Board> findTodayDeletedBoards(Pageable pageable);
	
    @Modifying
    @Transactional
    @Query("UPDATE Board b SET b.boardViewCnt = b.boardViewCnt + :count WHERE b.boardNo = :boardNo")
    void updateViewCount(@Param("boardNo") Long boardNo, @Param("count") int count);

}
