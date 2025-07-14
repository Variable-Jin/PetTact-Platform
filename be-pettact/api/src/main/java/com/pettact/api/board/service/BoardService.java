package com.pettact.api.board.service;

import com.pettact.api.board.entity.Board;
import com.pettact.api.board.repository.BoardRepository;
import com.pettact.api.common.ViewCountService;
import com.pettact.api.common.scheduler.ViewCountScheduler.ViewCountSyncable;
import com.pettact.api.board.dto.BoardCreateDto;
import com.pettact.api.board.dto.BoardResponseDto;
import com.pettact.api.Category.entity.BoardCategory;
import com.pettact.api.Category.repository.CategoryRepository;
import com.pettact.api.recommend.boardRecommend.repository.BoardRecommendRepository;
import com.pettact.api.reply.dto.ReplyResponseDto;
import com.pettact.api.reply.service.ReplyService;
import com.pettact.api.user.entity.Users;
import com.pettact.api.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BoardService implements ViewCountSyncable<Long> {
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private ReplyService replyService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BoardRecommendRepository boardRecommendRepository;
    @Autowired
    private ViewCountService viewCountService;
    @Autowired    
    private StringRedisTemplate redisTemplate;
    @Transactional
    public BoardResponseDto createBoard(BoardCreateDto boardCreateDto,  Long userNo) {
        Users users = userRepository.findById(userNo)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));
        BoardCategory boardCategory = categoryRepository
                .findById(boardCreateDto.getBoardCategoryNo())
                .orElseThrow(() -> new RuntimeException("게시글 카테고리가 존재하지 않습니다."));
        Board board = BoardCreateDto.toEntity(boardCreateDto, boardCategory, users);
        Board savedBoard = boardRepository.save(board);
        return BoardResponseDto.fromEntity(savedBoard);
    }
    public List<BoardResponseDto> getAllBoard() {
        List<Board> boards = boardRepository.findAll();
        return boards.stream()
                .map(BoardResponseDto::getAllBoard)
                .collect(Collectors.toList());
    }
    public BoardResponseDto getBoardByNo(Long boardNo, String sessionId) {
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new IllegalArgumentException("게시글 정보를 찾을 수 없습니다. No: " + boardNo));
        String preventKey = "board:viewed:" + sessionId + ":" + boardNo;
        if (Boolean.FALSE.equals(redisTemplate.hasKey(preventKey))) {
            viewCountService.increaseViewCount("board", boardNo, 120);
            redisTemplate.opsForValue().set(preventKey, "1", Duration.ofMinutes(60));
        }
        
        List<ReplyResponseDto> replyList = replyService.getAllReplies(boardNo);
        int recommendCount = boardRecommendRepository.countByBoardNo(boardNo);
        BoardResponseDto boardResponseDto = BoardResponseDto.fromEntity(board);
        boardResponseDto.setReplies(replyList);
        boardResponseDto.setRecommendCount(recommendCount);
        return boardResponseDto;
    }
    @Transactional
    public BoardResponseDto updateBoard(Long boardNo, BoardCreateDto boardCreateDto, Long userNo) {
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. No: " + boardNo));
        if (!board.getUser().getUserNo().equals(userNo)) {
            throw new IllegalArgumentException("수정 권한이 없습니다. 작성자만 수정할 수 있습니다.");
        }
        board.updateBoard(boardCreateDto);
        Board updated = boardRepository.save(board);
        return BoardResponseDto.fromEntity(updated);
    }
    @Transactional
    public void deleteBoard(Long boardNo, Long userNo) {
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new IllegalArgumentException("게시글 정보를 찾을 수 없습니다. No: " + boardNo));
        if (!board.getUser().getUserNo().equals(userNo)) {
            throw new IllegalArgumentException("해당 사용자가 아닙니다. 삭제를 할 수 없습니다.");
        }
        boardRepository.deleteById(boardNo);
    }
    
    // ------------------ 게시글 조회수 db 갱신------------------
    
    @Override
    @Transactional
    public void updateViewCount(Long boardNo, int count) {
    	boardRepository.updateViewCount(boardNo, count);
    }
}