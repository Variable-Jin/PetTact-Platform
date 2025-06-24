package com.pettact.api.pet.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "pet_sido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetSidoEntity {

    @Id
    private String orgCd;        // 시도 코드 (Primary Key)

    private String orgdownNm;    // 시도 명칭
}
