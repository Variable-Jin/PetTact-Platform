package com.pettact.api.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pettact.api.user.dto.UserJoinDTO;
import com.pettact.api.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/user")
public class UserController {
    private final UserService userService;

//    @PostMapping
//    public ResponseEntity<> join(@RequestBody UserJoinDTO dto) {
//    	
//    	if(userService.isEmailDuplicated(dto.getEmail())) {
//    		
//    	}
//
//    	if(userService.isNicknameDuplicated(dto.getNickname())) {
//    		
//    	}
//    	
//        userService.join(dto);
//        return "회원가입 완료";
//    }
}
