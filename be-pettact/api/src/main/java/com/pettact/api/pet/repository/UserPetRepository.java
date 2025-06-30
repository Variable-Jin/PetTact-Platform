package com.pettact.api.pet.repository;

import com.pettact.api.pet.entity.UserPetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPetRepository extends JpaRepository<UserPetEntity, Long> {
    List<UserPetEntity> findByUser_UserNo(Long userNo); 
}
