package com.pettact.api.pet.entity.tmp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "pet_sido_tmp")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetSidoTmpEntity {

    @Id
    private String orgCd;        // 시도 코드 (Primary Key)

    private String orgdownNm;    // 시도 명칭
    
    
    @Builder.Default
    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isUpdated = false;
}
