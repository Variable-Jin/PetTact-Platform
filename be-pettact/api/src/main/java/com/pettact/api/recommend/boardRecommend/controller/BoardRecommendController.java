package com.pettact.api.recommend.boardRecommend.controller;

import com.pettact.api.recommend.boardRecommend.dto.BoardRecommendDto;
import com.pettact.api.recommend.boardRecommend.service.BoardRecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/board/{boardNo}/recommend")
public class BoardRecommendController {

    @Autowired
    private BoardRecommendService boardRecommendService;

    @PostMapping
    public ResponseEntity<Void> createBoardRecommend(@PathVariable("boardNo") Long boardNo,
            @RequestBody BoardRecommendDto boardRecommendDto) {
        boardRecommendService.createRecommend(boardNo, boardRecommendDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteBoardRecommend(@PathVariable("boardNo") Long boardNo,
            @RequestBody BoardRecommendDto boardRecommendDto) {
        boardRecommendService.cancelRecommend(boardNo, boardRecommendDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }
}
