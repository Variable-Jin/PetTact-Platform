package com.pettact.api.pet.entity.tmp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pet_kind_tmp")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetKindTmpEntity {

    @Id
    private String kindCd;
    private String kindNm;
    private String upKindCd;// 개, 고양이, 기타 축종 구분용
    
    
    @Builder.Default
    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isUpdated = false;
}
