package com.pettact.api.pet.service;

import com.pettact.api.pet.dto.*;
import com.pettact.api.pet.dto.wrapper.*;
import com.pettact.api.pet.entity.tmp.*;
import com.pettact.api.pet.repository.tmp.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PetTmpDataService {

    private final RestTemplate restTemplate;

    private final PetFacilityTmpRepository petFacilityTmpRepository;
    private final PetShelterTmpRepository petShelterTmpRepository;
    private final PetKindTmpRepository petKindTmpRepository;
    private final PetSidoTmpRepository petSidoTmpRepository;
    private final PetSigunguTmpRepository petSigunguTmpRepository;
    private final PetAbandonmentTmpRepository petAbandonmentTmpRepository;

    @Value("${pet-api-service-key-decoded}")
    private String apiKey;
    @Value("${pet-facility-api-url}")
    private String petFacilityApiUrl;
    @Value("${pet-shelter-api-url}")
    private String petShelterApiUrl;
    @Value("${sigungu-api-url}")
    private String sigunguApiUrl;
    @Value("${sido-api-url}")
    private String sidoApiUrl;
    @Value("${kind-api-url}")
    private String kindApiUrl;
    @Value("${abandonment-public-api-url}")
    private String abandonmentPublicApiurl;
    
    @Transactional
    public void fetchPetFacilityToTmp() {
        int perPage = 1000;
        int page = 1;

        while (true) {
            String url = petFacilityApiUrl + "?page=" + page
                    + "&perPage=" + perPage
                    + "&returnType=JSON"
                    + "&serviceKey=" + apiKey;

            ResponseEntity<PetFacilityWrapper> response = restTemplate.getForEntity(url, PetFacilityWrapper.class);
            PetFacilityWrapper body = response.getBody();

            if (body == null || body.getData() == null || body.getData().isEmpty()) break;

            // facility_key 중복 체크
            for (PetFacilityDto dto : body.getData()) {
                String checkKey = (dto.getFacilityName() == null ? "" : dto.getFacilityName().trim()) + "-" +
                        (dto.getLotAddress() == null ? "" : dto.getLotAddress().trim());
                if (petFacilityTmpRepository.existsByFacilityKey(checkKey)) {
                    continue;
                }

                PetFacilityTmpEntity entity = PetFacilityTmpEntity.builder()
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

                petFacilityTmpRepository.save(entity);
            }

            page++;
        }
    }



    @Transactional
    public void fetchPetShelterToTmp() {
    	
        int numOfRows = 100;
        int pageNo = 1;
        ResponseEntity<PetShelterWrapper> initialResponse = restTemplate.getForEntity(
                petShelterApiUrl + "?serviceKey=" + apiKey + "&numOfRows=" + numOfRows + "&pageNo=" + pageNo + "&_type=json",
                PetShelterWrapper.class);
        PetShelterWrapper initialWrapper = initialResponse.getBody();
        if (initialWrapper == null || initialWrapper.getResponse() == null || initialWrapper.getResponse().getBody() == null)
            return;
        int totalCount = initialWrapper.getResponse().getBody().getTotalCount();
        int totalPages = (int) Math.ceil((double) totalCount / numOfRows);

        for (int i = 1; i <= totalPages; i++) {
            String url = petShelterApiUrl + "?serviceKey=" + apiKey + "&numOfRows=" + numOfRows + "&pageNo=" + i + "&_type=json";
            try {
                ResponseEntity<PetShelterWrapper> response = restTemplate.getForEntity(url, PetShelterWrapper.class);
                List<PetShelterDto> list = response.getBody().getResponse().getBody().getItems().getItem();
                if (list == null || list.isEmpty()) continue;
                for (PetShelterDto dto : list) {
                    PetShelterTmpEntity entity = PetShelterTmpEntity.builder()
                            .careNm(dto.getCareNm())
                            .careRegNo(dto.getCareRegNo())
                            .orgNm(dto.getOrgNm())
                            .divisionNm(dto.getDivisionNm())
                            .saveTrgtAnimal(dto.getSaveTrgtAnimal())
                            .careAddr(dto.getCareAddr())
                            .jibunAddr(dto.getJibunAddr())
                            .lat(dto.getLat())
                            .lng(dto.getLng())
                            .dsignationDate(dto.getDsignationDate())
                            .weekOprStime(dto.getWeekOprStime())
                            .weekOprEtime(dto.getWeekOprEtime())
                            .weekCellStime(dto.getWeekCellStime())
                            .weekCellEtime(dto.getWeekCellEtime())
                            .weekendOprStime(dto.getWeekendOprStime())
                            .weekendOprEtime(dto.getWeekendOprEtime())
                            .weekendCellStime(dto.getWeekendCellStime())
                            .weekendCellEtime(dto.getWeekendCellEtime())
                            .closeDay(dto.getCloseDay())
                            .vetPersonCnt(dto.getVetPersonCnt())
                            .specsPersonCnt(dto.getSpecsPersonCnt())
                            .medicalCnt(dto.getMedicalCnt())
                            .breedCnt(dto.getBreedCnt())
                            .quarabtineCnt(dto.getQuarabtineCnt())
                            .feedCnt(dto.getFeedCnt())
                            .transCarCnt(dto.getTransCarCnt())
                            .careTel(dto.getCareTel())
                            .dataStdDt(dto.getDataStdDt())
                            .build();
                    petShelterTmpRepository.save(entity);
                }
            } catch (Exception e) {
                log.error("fetchPetShelterToTmp error: {}", e.getMessage());
            }
        }
    }

    @Transactional
    public void fetchPetKindToTmp() {
        for (String upKindCd : Arrays.asList("417000", "422400", "429900")) {
            int pageNo = 1;
            int numOfRows = 100;
            while (true) {
                String url = kindApiUrl + "?serviceKey=" + apiKey + "&up_kind_cd=" + upKindCd + "&_type=json"
                        + "&numOfRows=" + numOfRows + "&pageNo=" + pageNo;
                try {
                    ResponseEntity<PetKindWrapper> response = restTemplate.getForEntity(url, PetKindWrapper.class);
                    List<PetKindDto> kindList = response.getBody().getResponse().getBody().getItems().getItem();
                    if (kindList == null || kindList.isEmpty()) break;
                    for (PetKindDto dto : kindList) {
                        PetKindTmpEntity entity = PetKindTmpEntity.builder()
                                .kindCd(dto.getKindCd())
                                .kindNm(dto.getKindNm())
                                .upKindCd(upKindCd)
                                .build();
                        petKindTmpRepository.save(entity);
                    }
                    pageNo++;
                } catch (Exception e) {
                    log.error("fetchPetKindToTmp error: {}", e.getMessage());
                    break;
                }
            }
        }
    }

    @Transactional
    public void fetchPetSidoToTmp() {
        try {
            ResponseEntity<PetSidoWrapper> response = restTemplate.getForEntity(
                    sidoApiUrl + "?serviceKey=" + apiKey + "&numOfRows=50&_type=json",
                    PetSidoWrapper.class);
            List<PetSidoDto> sidoList = response.getBody().getResponse().getBody().getItems().getItem();
            for (PetSidoDto dto : sidoList) {
                PetSidoTmpEntity entity = PetSidoTmpEntity.builder()
                        .orgCd(dto.getOrgCd())
                        .orgdownNm(dto.getOrgdownNm())
                        .build();
                petSidoTmpRepository.save(entity);
            }
        } catch (Exception e) {
            log.error("fetchPetSidoToTmp error: {}", e.getMessage());
        }
    }

    @Transactional
    public void fetchPetSigunguToTmp() {
        List<PetSidoTmpEntity> sidoList = petSidoTmpRepository.findAll();
        for (PetSidoTmpEntity sido : sidoList) {
            String uprCd = sido.getOrgCd();
            int pageNo = 1;
            int numOfRows = 100;
            while (true) {
                String url = sigunguApiUrl + "?serviceKey=" + apiKey + "&upr_cd=" + uprCd + "&_type=json"
                        + "&numOfRows=" + numOfRows + "&pageNo=" + pageNo;
                try {
                    ResponseEntity<PetSigunguWrapper> response = restTemplate.getForEntity(url, PetSigunguWrapper.class);
                    List<PetSigunguDto> sigunguList = response.getBody().getResponse().getBody().getItems().getItem();
                    if (sigunguList == null || sigunguList.isEmpty()) break;
                    for (PetSigunguDto dto : sigunguList) {
                        PetSigunguTmpEntity entity = PetSigunguTmpEntity.builder()
                                .orgCd(dto.getOrgCd())
                                .orgdownNm(dto.getOrgdownNm())
                                .uprCd(dto.getUprCd())
                                .build();
                        petSigunguTmpRepository.save(entity);
                    }
                    pageNo++;
                } catch (Exception e) {
                    log.error("fetchPetSigunguToTmp error: {}", e.getMessage());
                    break;
                }
            }
        }
    }

    @Transactional
    public void fetchPetAbandonmentToTmp() {
        int page = 1;
        int numOfRows = 1000;
        while (true) {
            String url = abandonmentPublicApiurl + "?serviceKey=" + apiKey + "&_type=json"
                    + "&numOfRows=" + numOfRows + "&pageNo=" + page;
            try {
                ResponseEntity<PetAbandonmentWrapper> response = restTemplate.getForEntity(url, PetAbandonmentWrapper.class);
                List<PetAbandonmentDto> itemList = response.getBody().getResponse().getBody().getItems().getItem();
                if (itemList == null || itemList.isEmpty()) break;
                for (PetAbandonmentDto dto : itemList) {
                	PetAbandonmentTmpEntity entity = PetAbandonmentTmpEntity.builder()
                		    .desertionNo(dto.getDesertionNo())
                		    .noticeNo(dto.getNoticeNo())
                		    .kindCd(dto.getKindCd())
                		    .colorCd(dto.getColorCd())
                		    .age(dto.getAge())
                		    .weight(dto.getWeight())
                		    .happenPlace(dto.getHappenPlace())
                		    .happenDt(dto.getHappenDt())
                		    .noticeSdt(dto.getNoticeSdt())
                		    .noticeEdt(dto.getNoticeEdt())
                		    .processState(dto.getProcessState())
                		    .sexCd(dto.getSexCd())
                		    .neuterYn(dto.getNeuterYn())
                		    .specialMark(dto.getSpecialMark())
                		    .careRegNo(dto.getCareRegNo())
                		    .careNm(dto.getCareNm())
                		    .careTel(dto.getCareTel())
                		    .careAddr(dto.getCareAddr())
                		    .careOwnerNm(dto.getCareOwnerNm())       
                		    .orgNm(dto.getOrgNm())
                		    .kindNm(dto.getKindNm())
                		    .popfile1(dto.getPopfile1())
                		    .popfile2(dto.getPopfile2())
                		    .popfile3(dto.getPopfile3())
                		    .popfile4(dto.getPopfile4())
                		    .popfile5(dto.getPopfile5())
                		    .popfile6(dto.getPopfile6())
                		    .popfile7(dto.getPopfile7())
                		    .popfile8(dto.getPopfile8())
                		    .build();
                    petAbandonmentTmpRepository.save(entity);
                }
                page++;
            } catch (Exception e) {
                log.error("fetchPetAbandonmentToTmp error: {}", e.getMessage());
                break;
            }
        }
    }
    
    public void fetchAllTmpApi() {
    	clearAllTmpTables();
    	fetchPetShelterToTmp();
    	fetchPetKindToTmp();
    	fetchPetSidoToTmp();
    	fetchPetSigunguToTmp();
    	fetchPetAbandonmentToTmp();
    	fetchPetFacilityToTmp();
    }
    @Transactional
    public void clearAllTmpTables() {
        petFacilityTmpRepository.deleteAll();
        petShelterTmpRepository.deleteAll();
        petKindTmpRepository.deleteAll();
        petSidoTmpRepository.deleteAll();
        petSigunguTmpRepository.deleteAll();
        petAbandonmentTmpRepository.deleteAll();
    }
    
}