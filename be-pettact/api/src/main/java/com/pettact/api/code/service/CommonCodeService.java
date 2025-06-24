package com.pettact.api.code.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.pettact.api.code.repository.CommonCodeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommonCodeService {

    private final CommonCodeRepository commonCodeRepository;

    public List<Map<String, String>> getCodeOptions(String groupCode) {
        return commonCodeRepository.findByGroup_GroupCodeOrderByCodeOrder(groupCode)
                .stream()
                .map(code -> Map.of(
                        "value", code.getCodeId(),
                        "label", code.getCodeName()
                ))
                .collect(Collectors.toList());
    }
}