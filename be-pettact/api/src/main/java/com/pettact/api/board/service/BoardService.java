package com.pettact.api.board.service;

import com.pettact.api.board.entity.Board;
import com.pettact.api.board.repository.BoardRepository;
import com.pettact.api.common.ViewCountService;
import com.pettact.api.common.scheduler.ViewCountScheduler.ViewCountSyncable;
import com.pettact.api.board.dto.BoardCreateDto;
import com.pettact.api.board.dto.BoardResponseDto;
import com.pettact.api.Category.entity.BoardCategory;
import com.pettact.api.Category.repository.CategoryRepository;
import com.pettact.api.file.dto.FileDto;
import com.pettact.api.file.entity.File;
import com.pettact.api.file.service.MultiFileService;
import com.pettact.api.product.dto.ProductDTO;
import com.pettact.api.product.entity.ProductEntity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
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
    @Autowired
    private MultiFileService multiFileService;

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

            String today = LocalDate.now().toString();
            Long categoryId = board.getBoardCategory().getBoardCategoryNo();

            // 전체 인기 ZSet
            String globalKey = "board:popular:" + today;
            redisTemplate.opsForZSet().incrementScore(globalKey, boardNo.toString(), 1);
            redisTemplate.expire(globalKey, Duration.ofDays(8));

            // 카테고리별 인기 ZSet
            String categoryKey = "board:popular:" + categoryId + ":" + today;
            redisTemplate.opsForZSet().incrementScore(categoryKey, boardNo.toString(), 1);
            redisTemplate.expire(categoryKey, Duration.ofDays(8));

            redisTemplate.opsForValue().set(preventKey, "1", Duration.ofMinutes(60));
        }
        
        List<ReplyResponseDto> replyList = replyService.getAllReplies(boardNo);
        int recommendCount = boardRecommendRepository.countByBoardNo(boardNo);
        BoardResponseDto boardResponseDto = BoardResponseDto.fromEntity(board);
        boardResponseDto.setReplies(replyList);
        boardResponseDto.setRecommendCount(recommendCount);
        return boardResponseDto;
    }
//    @Transactional
//    public BoardResponseDto updateBoard(Long boardNo, BoardCreateDto boardCreateDto, Long userNo) {
//        Board board = boardRepository.findById(boardNo)
//                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. No: " + boardNo));
//        if (!board.getUser().getUserNo().equals(userNo)) {
//            throw new IllegalArgumentException("수정 권한이 없습니다. 작성자만 수정할 수 있습니다.");
//        }
//        board.updateBoard(boardCreateDto);
//        Board updated = boardRepository.save(board);
//        return BoardResponseDto.fromEntity(updated);
//    }

    @Transactional
    public BoardResponseDto updateBoard(
            Long boardNo,
            BoardCreateDto boardCreateDto,
            Long userNo,
            MultipartFile[] files,
            List<Long> deletedFileIds
    ) {
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. No: " + boardNo));

        if (!board.getUser().getUserNo().equals(userNo)) {
            throw new IllegalArgumentException("수정 권한이 없습니다. 작성자만 수정할 수 있습니다.");
        }

        board.updateBoard(boardCreateDto);
        boardRepository.save(board);

        log.info("삭제 요청된 파일 ID 목록: {}", deletedFileIds);

        // 1. 파일 삭제 처리
        if (deletedFileIds != null && !deletedFileIds.isEmpty()) {
            for (Long fileNo : deletedFileIds) {
                log.info("파일 삭제 호출됨: fileNo={}, userNo={}", fileNo, userNo);
                multiFileService.delete(fileNo, userNo);
                log.info("파일 삭제 완료: fileNo={}", fileNo);
            }
            // 영속성 컨텍스트 즉시 반영
            boardRepository.flush();
        }


        // 2. 새 파일 업로드
        if (files != null && files.length > 0) {
            multiFileService.createFiles(
                    File.ReferenceTable.BOARD,
                    boardNo,
                    files,
                    userNo
            );
        }

        // 3. 응답 생성 - 삭제와 추가가 모두 완료된 후 조회
        BoardResponseDto responseDto = BoardResponseDto.fromEntity(board);
        List<FileDto> uploadedFiles = multiFileService.getFilesByReference(File.ReferenceTable.BOARD, boardNo);
        responseDto.setFiles(uploadedFiles);

        return responseDto;
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
    
    // ------------------ 인기 게시글 TOP 10 ------------------    
    public List<BoardResponseDto> getPopularBoards(Long categoryNo, int count) {
        List<String> dateKeys = IntStream.rangeClosed(0, 6)
            .mapToObj(i -> {
                String date = LocalDate.now().minusDays(i).toString();
                return (categoryNo == null)
                    ? "board:popular:" + date
                    : "board:popular:" + categoryNo + ":" + date;
            })
            .toList();

        String tempKey = "board:popular:temp:" + UUID.randomUUID();
        redisTemplate.opsForZSet().unionAndStore(dateKeys.get(0), dateKeys.subList(1, dateKeys.size()), tempKey);

        Set<String> boardNos = redisTemplate.opsForZSet()
            .reverseRange(tempKey, 0, count - 1);

        redisTemplate.delete(tempKey);

        if (boardNos == null || boardNos.isEmpty()) return List.of();

        List<Long> ids = boardNos.stream().map(Long::parseLong).toList();
        List<Board> boards = boardRepository.findAllById(ids);

        Map<Long, Board> boardMap = boards.stream()
            .collect(Collectors.toMap(Board::getBoardNo, Function.identity()));

        return ids.stream()
            .map(boardMap::get)
            .filter(Objects::nonNull)
            .map(BoardResponseDto::fromEntity)
            .toList();
    }

}