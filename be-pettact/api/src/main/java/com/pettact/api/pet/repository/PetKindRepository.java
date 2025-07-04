package com.pettact.api.pet.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pettact.api.pet.entity.PetKindEntity;

public interface PetKindRepository extends JpaRepository<PetKindEntity, String> {
	boolean existsByKindCd(String kindCd);

	Collection<PetKindEntity> findByUpKindCd(String upKindCd);

	@Query("SELECT p.kindNm FROM PetKindEntity p WHERE p.upKindCd = :upKindCd AND p.kindCd = :kindCd")
	String findKindNmByUpKindCdAndKindCd(@Param("upKindCd") String upKindCd, @Param("kindCd") String kindCd);

}
