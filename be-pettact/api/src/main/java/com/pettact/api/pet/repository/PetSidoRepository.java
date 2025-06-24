package com.pettact.api.pet.repository;

import com.pettact.api.pet.entity.PetSidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetSidoRepository extends JpaRepository<PetSidoEntity, String> {
    boolean existsByOrgCd(String orgCd);
}
