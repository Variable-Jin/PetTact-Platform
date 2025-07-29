package com.pettact.api.pet.entity;

import java.time.LocalDate;

import com.pettact.api.core.base.BaseEntity;
import com.pettact.api.user.entity.Users;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_pet")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPetEntity extends BaseEntity{
	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "pet_no")
	    private Long petId; // 반려동물 고유번호 (PK)

	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "user_no", nullable = false)
	    private Users user; // 사용자 정보 (users 테이블과 ManyToOne 관계)

	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "kind_cd", referencedColumnName = "kind_cd")
	    private PetKindEntity petKind; //kindCd
	    
	    private String rfidNo; // 등록된 동물의 RFID 번호

	    private String petName; // 반려동물 이름

	    private String petGender; // 성별 ("M" 또는 "F")

	    private String isNeutered; // 중성화 여부 ("Y" 또는 "N")

	    private LocalDate petBirth; // 생년월일

	    private Float petWeight; // 몸무게

	    private String petImageUrl; // 반려동물 사진 (일단은 url 저장.)
	    
	    private String specialNotes; // 특이사항

		private String OwnerRelation;



}
