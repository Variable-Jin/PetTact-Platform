package com.pettact.api.pet.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pettact.api.pet.entity.PetFacilityEntity;

@Repository
public interface PetFacilityRepository extends JpaRepository<PetFacilityEntity, Long> {
	boolean existsByFacilityKey(String facilityKey);

	@Query("SELECT f FROM PetFacilityEntity f " +
		       "WHERE (:sidoName IS NULL OR f.sidoName = :sidoName) " +
		       "AND (:sigunguName IS NULL OR f.sigunguName = :sigunguName) " +
		       "AND (:facilityName IS NULL OR f.facilityName LIKE %:facilityName%)")
		Page<PetFacilityEntity> searchByConditions(
		        @Param("sidoName") String sidoName,
		        @Param("sigunguName") String sigunguName,
		        @Param("facilityName") String facilityName,
		        Pageable pageable);


	Optional<PetFacilityEntity> findByFacilityNo(Long facilityNo);
}
