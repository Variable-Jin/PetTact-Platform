package com.pettact.api.pet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pettact.api.pet.entity.PetShelterEntity;


@Repository
public interface PetShelterRepository extends JpaRepository<PetShelterEntity, Long> {

	Optional<PetShelterEntity> findByCareRegNo(String careRegNo);

	@Query("""
		    SELECT s FROM PetShelterEntity s
		    WHERE (:sido IS NULL OR s.orgNm LIKE CONCAT('%', :sido, '%'))
		      AND (:sigungu IS NULL OR s.orgNm LIKE CONCAT('%', :sigungu, '%'))
		""")
		List<PetShelterEntity> findBySidoAndSigungu(
		    @Param("sido") String sido,
		    @Param("sigungu") String sigungu
		);

}
