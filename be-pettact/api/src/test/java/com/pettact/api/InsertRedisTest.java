package com.pettact.api;

import java.time.Duration;
import java.time.LocalDate;

import com.pettact.api.board.entity.Board;
import com.pettact.api.board.repository.BoardRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
public class InsertRedisTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private BoardRepository boardRepository;

    /**
     * Redis에 조회수와 중복방지 키 주입
     */
//    @Test
//    void insertSampleViewCountRange() {
//        for (long boardNo = 12L; boardNo <= 20L; boardNo++) {
//            String redisKey = "board:views:" + boardNo;
//            int viewCount = 100 + (int) boardNo;
//
//            redisTemplate.opsForValue().set(redisKey, String.valueOf(viewCount));
//            redisTemplate.expire(redisKey, Duration.ofHours(1)); // 조회수 키는 1시간 유지
//
//            String preventKey = "board:viewed:sample-session-id:" + boardNo;
//            redisTemplate.opsForValue().set(preventKey, "1", Duration.ofMinutes(60)); // 중복 방지 키
//        }
//    }

    /**
     * Redis에 인기글 ZSet 주입 (전체 + 카테고리별)
     */
//    @Test
//    void insertSamplePopularRange() {
//
//        for (long boardNo = 12L; boardNo <= 20L; boardNo++) {
//            final long currentBoardNo = boardNo; // final 변수로 복사
//
//            Board board = boardRepository.findById(currentBoardNo)
//                    .orElseThrow(() -> new IllegalArgumentException("게시글 없음: " + currentBoardNo));
//            Long categoryId = board.getBoardCategory().getBoardCategoryNo();
//            int viewCount = 100 + (int) currentBoardNo;
//
//            String today = LocalDate.now().toString();
//
//            // 전체 인기글
//            String globalKey = "board:popular:" + today;
//            redisTemplate.opsForZSet().incrementScore(globalKey, String.valueOf(currentBoardNo), viewCount);
//            redisTemplate.expire(globalKey, Duration.ofDays(8));
//
//            // 카테고리별 인기글
//            String categoryKey = "board:popular:" + categoryId + ":" + today;
//            redisTemplate.opsForZSet().incrementScore(categoryKey, String.valueOf(currentBoardNo), viewCount);
//            redisTemplate.expire(categoryKey, Duration.ofDays(8));
//
//            System.out.println("✅ boardNo=" + currentBoardNo + ", categoryId=" + categoryId + ", viewCount=" + viewCount);
//        }
//
//    }
}
