package com.pettact.api.pet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetFacilityDto {

    @JsonProperty("건물 번호")
    private String buildingNumber;

    @JsonProperty("경도")
    private String longitude;

    @JsonProperty("기본 정보_장소설명")
    private String placeDescription;

    @JsonProperty("도로명 이름")
    private String roadName;

    @JsonProperty("도로명주소")
    private String roadAddress;

    @JsonProperty("리 명칭")
    private String riName;

    @JsonProperty("반려동물 동반 가능정보")
    private String petAllowed;

    @JsonProperty("반려동물 전용 정보")
    private String petOnly;

    @JsonProperty("반려동물 제한사항")
    private String petLimit;

    @JsonProperty("번지")
    private String lotNumber;

    @JsonProperty("법정읍면동명칭")
    private String legalDongName;

    @JsonProperty("시군구 명칭")
    private String sigunguName;

    @JsonProperty("시도 명칭")
    private String sidoName;

    @JsonProperty("시설명")
    private String facilityName;

    @JsonProperty("애견 동반 추가 요금")
    private String additionalFee;

    @JsonProperty("우편번호")
    private Integer postalCode;

    @JsonProperty("운영시간")
    private String openHours;

    @JsonProperty("위도")
    private String latitude;

    @JsonProperty("입장 가능 동물 크기")
    private String allowedPetSize;

    @JsonProperty("입장(이용료)가격 정보")
    private String entranceFeeInfo;

    @JsonProperty("장소(실내) 여부")
    private String isIndoor;

    @JsonProperty("장소(실외)여부")
    private String isOutdoor;

    @JsonProperty("전화번호")
    private String phone;

    @JsonProperty("주차 가능여부")
    private String parkingAvailable;

    @JsonProperty("지번주소")
    private String lotAddress;

    @JsonProperty("최종작성일")
    private String lastUpdatedDate;

    @JsonProperty("카테고리1")
    private String category1;

    @JsonProperty("카테고리2")
    private String category2;

    @JsonProperty("카테고리3")
    private String category3;

    @JsonProperty("홈페이지")
    private String homepage;

    @JsonProperty("휴무일")
    private String closedDays;
}
