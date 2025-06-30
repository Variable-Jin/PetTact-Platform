package com.pettact.api.pet.repository;

import com.pettact.api.pet.entity.PetSigunguEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetSigunguRepository extends JpaRepository<PetSigunguEntity, String> {
    boolean existsByOrgCd(String orgCd);
}