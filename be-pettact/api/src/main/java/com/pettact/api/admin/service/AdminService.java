package com.pettact.api.admin.service;

import org.springframework.stereotype.Service;

import com.pettact.api.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {
	private final UserRepository userRepository;
}
