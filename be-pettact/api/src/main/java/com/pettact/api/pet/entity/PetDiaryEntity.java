package com.pettact.api.pet.entity;

import com.pettact.api.core.base.BaseEntity;
import com.pettact.api.user.entity.Users;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pet_diary")
public class PetDiaryEntity extends BaseEntity {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long diaryId;

    @ManyToOne
    @JoinColumn(name= "user_no", nullable = false)
    private Users user;
    
    @Column(name = "pet_id", nullable = false)
    private Long petId; // 나중에 Pet 객체로 교체할것.

    @Lob
    @Column(columnDefinition = "TEXT")
    private String diaryContent;
    
    @Lob
    @Column(columnDefinition = "TEXT")
    private String prompt;
}

