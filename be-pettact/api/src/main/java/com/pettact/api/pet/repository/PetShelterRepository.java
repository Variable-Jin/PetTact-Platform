package com.pettact.api.pet.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pettact.api.pet.entity.PetShelterEntity;


@Repository
public interface PetShelterRepository extends JpaRepository<PetShelterEntity, Long> {

	Optional<PetShelterEntity> findByCareRegNo(String careRegNo);
	
	Optional<PetShelterEntity> findByShelterNo(Long shelterNo);

	@Query("""
		    SELECT s FROM PetShelterEntity s
		    WHERE (:sido IS NULL OR s.orgNm LIKE CONCAT('%', :sido, '%'))
		""")
		Page<PetShelterEntity> findBySido(
		    @Param("sido") String sido,
		    Pageable pageable
		);

	boolean existsByCareRegNo(String careRegNo);

}
