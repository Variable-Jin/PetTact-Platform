package com.pettact.api.pet.repository.tmp;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pettact.api.pet.entity.tmp.PetAbandonmentTmpEntity;

public interface PetAbandonmentTmpRepository extends JpaRepository<PetAbandonmentTmpEntity, String> {
    boolean existsByDesertionNo(String desertionNo);
}