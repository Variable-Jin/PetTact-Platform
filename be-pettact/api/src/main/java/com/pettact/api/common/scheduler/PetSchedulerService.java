package com.pettact.api.common.scheduler;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pettact.api.core.base.MapperUtil;
import com.pettact.api.pet.dto.PetAbandonmentDto;
import com.pettact.api.pet.entity.PetAbandonmentEntity;
import com.pettact.api.pet.repository.PetAbandonmentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PetSchedulerService {
	private final MapperUtil mapper;
	private final ObjectMapper objectMapper;
	
    private final PetAbandonmentRepository petAbandonmentRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    
    /*
     * [유기동물 공고 종료 임박 리스트 캐싱 스케줄러]
     * 매일 새벽 4시
     * 오늘 ~ +2일까지 종료 예정 공고 리스트를 Redis에 TTL 1일로 저장
     */
    
    @Scheduled(cron = "20 11 18 * * *")
    public void cacheEndingSoonPets() {
        log.info("[Scheduler] 공고 종료 임박 유기동물 캐싱 시작");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        String startDate = LocalDate.now().format(formatter);
        String endDate = LocalDate.now().plusDays(2).format(formatter);

        List<PetAbandonmentEntity> endingSoonEntityList = petAbandonmentRepository.findEndingSoon(startDate, endDate);

        List<PetAbandonmentDto> endingSoonList = endingSoonEntityList.stream()
                .map(entity -> mapper.map(entity, PetAbandonmentDto.class))
                .toList();

        String redisKey = "pet:endingSoon:list";
//        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
//
//        redisTemplate.delete(redisKey);
//        ops.set(redisKey, endingSoonList, Duration.ofDays(1));

        try {
            String json = objectMapper.writeValueAsString(endingSoonList);
            redisTemplate.opsForValue().set(redisKey, json, Duration.ofDays(1));

            log.info("[Scheduler] 공고 종료 임박 데이터 Redis 저장 완료 (key: {}, size: {})", redisKey, endingSoonList.size());
        } catch (Exception e) {
            log.error("JSON 직렬화 오류", e);
        }
        
        log.info("[Scheduler] 공고 종료 임박 데이터 Redis 저장 완료 (key: {}, size: {})", redisKey, endingSoonList.size());
    }
}
