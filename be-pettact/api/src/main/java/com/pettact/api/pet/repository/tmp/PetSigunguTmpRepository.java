package com.pettact.api.pet.repository.tmp;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pettact.api.pet.entity.tmp.PetSigunguTmpEntity;

public interface PetSigunguTmpRepository extends JpaRepository<PetSigunguTmpEntity, String> {
    boolean existsByOrgCd(String orgCd);
}