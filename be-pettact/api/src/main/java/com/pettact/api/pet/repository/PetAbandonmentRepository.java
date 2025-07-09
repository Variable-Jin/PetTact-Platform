package com.pettact.api.pet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.pettact.api.pet.entity.PetAbandonmentEntity;

public interface PetAbandonmentRepository extends JpaRepository<PetAbandonmentEntity, String> {

	@Query("""
		    SELECT a FROM PetAbandonmentEntity a
		    WHERE (:kindNm IS NULL OR a.kindNm = :kindNm)
		      AND (:orgNm IS NULL OR a.orgNm LIKE CONCAT(:orgNm, '%'))
		""")
		Page<PetAbandonmentEntity> searchByConditionsPaged(
		    @Param("kindNm") String kindNm,
		    @Param("orgNm") String orgNm,
		    Pageable pageable
		);


	Optional<PetAbandonmentEntity> findByDesertionNo(String desertionNo);


    @Modifying
    @Transactional
    @Query("UPDATE PetAbandonmentEntity p SET p.petViewCnt = p.petViewCnt + :count WHERE p.desertionNo = :desertionNo")
    void updateViewCount(@Param("desertionNo") String desertionNo, @Param("count") int count);

}
