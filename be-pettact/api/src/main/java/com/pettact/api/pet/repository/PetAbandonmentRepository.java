package com.pettact.api.pet.repository;

import com.pettact.api.pet.entity.PetAbandonmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetAbandonmentRepository extends JpaRepository<PetAbandonmentEntity, String> {
    boolean existsByDesertionNo(String desertionNo);
}