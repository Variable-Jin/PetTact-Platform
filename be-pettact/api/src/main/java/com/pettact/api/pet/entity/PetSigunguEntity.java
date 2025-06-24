package com.pettact.api.pet.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pet_sigungu")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetSigunguEntity {
    @Id
    private String orgCd;
    private String orgdownNm;
    private String uprCd;
}