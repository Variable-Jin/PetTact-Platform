package com.pettact.api.pet.entity.tmp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
	    name = "pet_shelter_tmp",
	    uniqueConstraints = {
	        @UniqueConstraint(name = "uk_pet_shelter_care_reg_no", columnNames = "careRegNo")
	    }
	)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetShelterTmpEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shelterNo;
    
    private String careNm;
    @Column(nullable = false)
    private String careRegNo;
    private String orgNm;
    private String divisionNm;
    private String saveTrgtAnimal;
    private String careAddr;
    private String jibunAddr;
    private String lat;
    private String lng;
    private String dsignationDate;
    private String weekOprStime;
    private String weekOprEtime;
    private String weekCellStime;
    private String weekCellEtime;
    private String weekendOprStime;
    private String weekendOprEtime;
    private String weekendCellStime;
    private String weekendCellEtime;
    private String closeDay;
    private Integer vetPersonCnt;
    private Integer specsPersonCnt;
    private Integer medicalCnt;
    private Integer breedCnt;
    private Integer quarabtineCnt;
    private Integer feedCnt;
    private Integer transCarCnt;
    private String careTel;
    private String dataStdDt;
    
    @Builder.Default
    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isUpdated = false;
}
