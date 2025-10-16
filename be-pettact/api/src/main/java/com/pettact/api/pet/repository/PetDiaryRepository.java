package com.pettact.api.pet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pettact.api.pet.entity.PetDiaryEntity;

public interface PetDiaryRepository extends JpaRepository<PetDiaryEntity, Long> {

	List<PetDiaryEntity> findByPetIdAndIsDeletedFalse(Long petId);
	
}
