package com.pettact.api.pet.repository.tmp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pettact.api.pet.entity.tmp.PetShelterTmpEntity;

@Repository
public interface PetShelterTmpRepository extends JpaRepository<PetShelterTmpEntity, Long> {

	Object findByCareRegNo(String careRegNo);
}
