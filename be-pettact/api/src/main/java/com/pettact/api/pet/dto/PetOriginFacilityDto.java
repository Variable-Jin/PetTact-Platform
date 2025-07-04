package com.pettact.api.pet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetOriginFacilityDto {
	
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

    private String homepage;

    private String closedDays;
    
    private Boolean isUpdated;
}
