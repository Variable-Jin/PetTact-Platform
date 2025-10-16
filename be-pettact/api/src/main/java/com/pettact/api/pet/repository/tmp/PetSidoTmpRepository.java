package com.pettact.api.pet.repository.tmp;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pettact.api.pet.entity.tmp.PetSidoTmpEntity;

public interface PetSidoTmpRepository extends JpaRepository<PetSidoTmpEntity, String> {
    boolean existsByOrgCd(String orgCd);
}
