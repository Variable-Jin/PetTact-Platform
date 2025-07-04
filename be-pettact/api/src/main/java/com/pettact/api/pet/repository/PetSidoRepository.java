package com.pettact.api.pet.repository;

import com.pettact.api.pet.entity.PetSidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PetSidoRepository extends JpaRepository<PetSidoEntity, String> {
    boolean existsByOrgCd(String orgCd);

    PetSidoEntity findByOrgCd(String orgCd);
}
