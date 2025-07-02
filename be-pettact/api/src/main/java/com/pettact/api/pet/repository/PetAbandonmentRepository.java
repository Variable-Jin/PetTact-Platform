package com.pettact.api.pet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pettact.api.pet.entity.PetAbandonmentEntity;

public interface PetAbandonmentRepository extends JpaRepository<PetAbandonmentEntity, String> {

	@Query("""
		    SELECT a FROM PetAbandonmentEntity a
		    WHERE (:kindNm IS NULL OR a.kindNm = :kindNm)
		      AND (:orgNm IS NULL OR a.orgNm LIKE CONCAT(:orgNm, '%'))
		      AND (:careRegNo IS NULL OR a.careRegNo = :careRegNo)
		""")
		List<PetAbandonmentEntity> searchByConditions(
		    @Param("kindNm") String kindNm,
		    @Param("orgNm") String orgNm,
		    @Param("careRegNo") String careRegNo
		);

}
