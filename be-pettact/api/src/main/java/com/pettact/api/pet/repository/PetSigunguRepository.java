package com.pettact.api.pet.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pettact.api.pet.entity.PetSidoEntity;
import com.pettact.api.pet.entity.PetSigunguEntity;

public interface PetSigunguRepository extends JpaRepository<PetSigunguEntity, String> {
    boolean existsByOrgCd(String orgCd);

	Collection<PetSigunguEntity> findByUprCd(String uprCd);
}