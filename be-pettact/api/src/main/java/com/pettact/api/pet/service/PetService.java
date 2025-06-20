package com.pettact.api.pet.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.pettact.api.pet.dto.PetFacilityWrapper;
import com.pettact.api.pet.entity.PetFacilityEntity;
import com.pettact.api.pet.repository.PetFacilityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PetService {

    private final RestTemplate restTemplate;
    
    private final PetFacilityRepository petFacilityRepository;

    @Value("${pet.api.service-key-decoded}")
    private String apiKey;
    
    //https://www.data.go.kr/iim/api/selectAPIAcountView.do
    @Value("${pet.facility.api-url}")
    private String petFacilityApiUrl;
    // 각각 api 호출 
    public void fetchAllApi() {
        fetchPetFacility();
    }
    
    public void fetchPetFacility() {
        int perPage = 1000;
        int page = 1;

        // 일단 첫 페이지로 totalCount 받아오기
        String initialUrl = petFacilityApiUrl + "?page=" + page + "&perPage=" + 
        		            perPage+ "&returnType=JSON" + "&serviceKey=" + apiKey;

        ResponseEntity<PetFacilityWrapper> initialResponse = restTemplate.getForEntity(initialUrl, PetFacilityWrapper.class);

        PetFacilityWrapper initialBody = initialResponse.getBody();

        if (initialBody == null || initialBody.getData() == null) return;

        int totalCount = initialBody.getTotalCount();
        int totalPages = (int) Math.ceil((double) totalCount / perPage);

        for (int i = 1; i <= totalPages; i++) {
            String url = petFacilityApiUrl
                    + "?page=" + i
                    + "&perPage=" + perPage
                    + "&returnType=JSON"
                    + "&serviceKey=" + apiKey;

            ResponseEntity<PetFacilityWrapper> response =
                    restTemplate.getForEntity(url, PetFacilityWrapper.class);

            PetFacilityWrapper body = response.getBody();

            if (body != null && body.getData() != null) {
                body.getData().forEach(dto -> {
                    PetFacilityEntity entity = PetFacilityEntity.builder()
                            .buildingNumber(dto.getBuildingNumber())
                            .longitude(dto.getLongitude())
                            .placeDescription(dto.getPlaceDescription())
                            .roadName(dto.getRoadName())
                            .roadAddress(dto.getRoadAddress())
                            .riName(dto.getRiName())
                            .petAllowed(dto.getPetAllowed())
                            .petOnly(dto.getPetOnly())
                            .petLimit(dto.getPetLimit())
                            .lotNumber(dto.getLotNumber())
                            .legalDongName(dto.getLegalDongName())
                            .sigunguName(dto.getSigunguName())
                            .sidoName(dto.getSidoName())
                            .facilityName(dto.getFacilityName())
                            .additionalFee(dto.getAdditionalFee())
                            .postalCode(dto.getPostalCode())
                            .openHours(dto.getOpenHours())
                            .latitude(dto.getLatitude())
                            .allowedPetSize(dto.getAllowedPetSize())
                            .entranceFeeInfo(dto.getEntranceFeeInfo())
                            .isIndoor(dto.getIsIndoor())
                            .isOutdoor(dto.getIsOutdoor())
                            .phone(dto.getPhone())
                            .parkingAvailable(dto.getParkingAvailable())
                            .lotAddress(dto.getLotAddress())
                            .lastUpdatedDate(dto.getLastUpdatedDate())
                            .category1(dto.getCategory1())
                            .category2(dto.getCategory2())
                            .category3(dto.getCategory3())
                            .homepage(dto.getHomepage())
                            .closedDays(dto.getClosedDays())
                            .build();
                    petFacilityRepository.save(entity);
                });
            }
        }
    }


}