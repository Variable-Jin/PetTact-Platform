package com.pettact.api.pet.repository.tmp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pettact.api.pet.entity.tmp.PetFacilityTmpEntity;

@Repository
public interface PetFacilityTmpRepository extends JpaRepository<PetFacilityTmpEntity, Long> {

	boolean existsByFacilityKey(String facilityKey);
}
