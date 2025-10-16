package com.pettact.api.pet.repository.tmp;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pettact.api.pet.entity.tmp.PetKindTmpEntity;

public interface PetKindTmpRepository extends JpaRepository<PetKindTmpEntity, String> {
	boolean existsByKindCd(String kindCd); 
}
