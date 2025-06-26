package com.pettact.api.recommend.boardRecommend.repository;

import com.pettact.api.recommend.boardRecommend.entity.BoardRecommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface BoardRecommendRepository extends JpaRepository<BoardRecommend, Long> {

    // TODO: 1번 쿼리 - 2번 쿼리 중복 -> 해결 필요

    @Query("SELECT COUNT(br) > 0 FROM BoardRecommend br WHERE br.board.boardNo = :boardNo AND br.users.userNo = :userNo")
    boolean existsByBoardAndUser(@Param("boardNo") Long boardNo, @Param("userNo") Long userNo);

    @Query("SELECT br FROM BoardRecommend br WHERE br.board.boardNo = :boardNo AND br.users.userNo = :userNo")
    Optional<BoardRecommend> findByBoardAndUser(@Param("boardNo") Long boardNo, @Param("userNo") Long userNo);

    @Query("SELECT COUNT(br) FROM BoardRecommend br WHERE br.board.boardNo = :boardNo")
    int countByBoardNo(@Param("boardNo") Long boardNo);
}
