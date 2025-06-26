package com.pettact.api.recommend.replyRecommend.controller;

import com.pettact.api.recommend.replyRecommend.dto.ReplyRecommendDto;
import com.pettact.api.recommend.replyRecommend.entity.ReplyRecommend;
import com.pettact.api.recommend.replyRecommend.service.ReplyRecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/reply/{replyNo}/recommend")
public class ReplyRecommendController {

    @Autowired
    private ReplyRecommendService replyRecommendService;

    @PostMapping
    public ResponseEntity<Void> createReplyRecommend(@PathVariable("replyNo") Long replyNo,
                                                     @RequestBody ReplyRecommendDto replyRecommendDto) {
        System.out.println("받은 DTO: " + replyRecommendDto.toString());
        replyRecommendService.createRecommend(replyNo, replyRecommendDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> cancelReplyRecommend(@PathVariable("replyNo") Long replyNo,
                                                     @RequestBody ReplyRecommendDto replyRecommendDto) {
        replyRecommendService.cancelRecommend(replyNo, replyRecommendDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
