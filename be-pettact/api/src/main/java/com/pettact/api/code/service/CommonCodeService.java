package com.pettact.api.code.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.pettact.api.code.entity.CommonCode;
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
    
    public CommonCode getCodeById(String codeId) {
        return commonCodeRepository.findById(codeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 코드가 존재하지 않습니다: " + codeId));
    }
}