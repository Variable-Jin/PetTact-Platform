package com.pettact.api.pet.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.pettact.api.pet.entity.UserPetEntity;

import java.util.List;

public interface UserPetRepository extends JpaRepository<UserPetEntity, Long> {
    
    Page<UserPetEntity> findByUser_UserNoAndIsDeletedFalse(Long userNo, Pageable pageable);


    List<UserPetEntity> findByUserUserNo(Long userNo);
}
