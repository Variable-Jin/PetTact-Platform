package com.pettact.api.pet.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class PetDataMergeService {
	@PersistenceContext
    private final EntityManager entityManager;

    @Transactional
    public void mergeSido() {
        entityManager.createNativeQuery("CALL merge_pet_sido()").executeUpdate();
    }

    @Transactional
    public void mergeSigungu() {
        entityManager.createNativeQuery("CALL merge_pet_sigungu()").executeUpdate();
    }

    @Transactional
    public void mergeShelter() {
        entityManager.createNativeQuery("CALL merge_pet_shelter()").executeUpdate();
    }

    @Transactional
    public void mergeKind() {
        entityManager.createNativeQuery("CALL merge_pet_kind()").executeUpdate();
    }

    @Transactional(timeout = 600)
    public void mergeFacility() {
        entityManager.createNativeQuery("CALL merge_pet_facility()").executeUpdate();
    }

    @Transactional
    public void mergeAbandonment() {
        entityManager.createNativeQuery("CALL merge_pet_abandonment()").executeUpdate();
    }

    @Transactional(timeout = 600)
    public void mergeAll() {
        mergeSido();
        mergeSigungu();
        mergeKind();
        mergeShelter(); 
        mergeAbandonment();
        mergeFacility();
    }
}
