package com.pettact.api.recommend.boardRecommend.service;

import com.pettact.api.board.entity.Board;
import com.pettact.api.board.repository.BoardRepository;
import com.pettact.api.recommend.boardRecommend.dto.BoardRecommendDto;
import com.pettact.api.recommend.boardRecommend.entity.BoardRecommend;
import com.pettact.api.recommend.boardRecommend.repository.BoardRecommendRepository;
import com.pettact.api.user.entity.Users;
import com.pettact.api.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class BoardRecommendService {

    @Autowired
    private BoardRecommendRepository boardRecommendRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void createRecommend(Long boardNo, Long userNo) {
        // 중복 추천 확인
        if (boardRecommendRepository.existsByBoardAndUser(boardNo, userNo)) {
            throw new IllegalArgumentException("이미 추천한 게시글입니다.");
        }

        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
        Users users = userRepository.findById(userNo)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 없습니다"));

        BoardRecommend boardRecommend = new BoardRecommend(
                board,
                users,
                LocalDateTime.now()
        );

        boardRecommendRepository.save(boardRecommend);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getRecommendStatus(Long boardNo, Long userNo) {
        long count = boardRecommendRepository.countByBoard_BoardNo(boardNo);
        boolean isRecommended = boardRecommendRepository.existsByBoard_BoardNoAndUsers_UserNo(boardNo, userNo);

        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("recommended", isRecommended);
        return result;
    }

    @Transactional
    public void cancelRecommend(Long boardNo, Long userNo) {
        BoardRecommend boardRecommend = boardRecommendRepository
                .findByBoardAndUser(boardNo, userNo)  // 직접 파라미터 사용!
                .orElseThrow(() -> new IllegalArgumentException("추천하지 않은 게시글입니다."));
        boardRecommendRepository.delete(boardRecommend);
    }
}
