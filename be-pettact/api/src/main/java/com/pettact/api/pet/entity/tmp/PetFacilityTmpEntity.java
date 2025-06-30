package com.pettact.api.pet.entity.tmp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pet_facility_tmp")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PetFacilityTmpEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long facilityNo;

    private String buildingNumber;
    private String longitude;
    private String placeDescription;
    private String roadName;
    private String roadAddress;
    private String riName;
    private String petAllowed;
    private String petOnly;
    private String petLimit;
    private String lotNumber;
    private String legalDongName;
    private String sigunguName;
    private String sidoName;
    private String facilityName;
    private String additionalFee;
    private Integer postalCode;
    private String openHours;
    private String latitude;
    private String allowedPetSize;
    private String entranceFeeInfo;
    private String isIndoor;
    private String isOutdoor;
    private String phone;
    private String parkingAvailable;
    private String lotAddress;
    private String lastUpdatedDate;
    private String category1;
    private String category2;
    private String category3;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isUpdated;

    @Column(columnDefinition = "TEXT")
    private String homepage;

    private String closedDays;

    /** 중복 제거용 key: facility_name + '-' + lot_address */
    @Column(name = "facilityKey", nullable = false, length = 300)
    private String facilityKey;

    /**
     * entity 저장 또는 업데이트 전 자동으로 facilityKey 생성
     */
    @PrePersist
    @PreUpdate
    private void generateFacilityKey() {
        this.facilityKey = (facilityName == null ? "" : facilityName.trim()) + "-" +
                           (lotAddress == null ? "" : lotAddress.trim());
    }
}
