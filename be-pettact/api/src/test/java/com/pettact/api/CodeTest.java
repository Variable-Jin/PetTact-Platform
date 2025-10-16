package com.pettact.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.pettact.api.code.entity.CommonCode;
import com.pettact.api.code.entity.GroupCode;
import com.pettact.api.code.repository.CommonCodeRepository;
import com.pettact.api.code.repository.GroupCodeRepository;

@SpringBootTest
public class CodeTest {
	/* 코드 관리 */
    @Autowired
    private CommonCodeRepository commonCodeRepository;

    @Autowired
    private GroupCodeRepository groupCodeRepository;
    
    @Test
    void addSocialInfoPendingCode() {
        GroupCode group = groupCodeRepository.findById("USER_STATUS")
                .orElseThrow(() -> new RuntimeException("USER_STATUS 그룹코드가 존재하지 않습니다."));
        groupCodeRepository.findAll().forEach(g -> System.out.println("✅ groupCode: " + g.getGroupCode()));

    	
        // given
        CommonCode code = new CommonCode();
        code.setCodeId("STATUS_SOCIAL_PENDING");
        code.setGroup(group);
        code.setCodeName("소셜 추가정보 대기");
        code.setCodeOrder(2);
        code.setDescription("소셜 로그인 후 추가 정보 입력 대기 상태");

        // when
        commonCodeRepository.save(code);

        // then
        CommonCode saved = commonCodeRepository.findById("STATUS_SOCIAL_PENDING")
                .orElseThrow(() -> new RuntimeException("저장 실패"));

        System.out.println("✅ 저장된 코드: " + saved);
    }
}
