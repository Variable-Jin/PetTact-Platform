package com.pettact.api.pet.service;

import com.pettact.api.pet.dto.*;
import com.pettact.api.pet.dto.wrapper.*;
import com.pettact.api.pet.entity.*;
import com.pettact.api.pet.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PetDataInitService {

    private final RestTemplate restTemplate;
    private final PetFacilityRepository petFacilityRepository;
    private final PetShelterRepository petShelterRepository;
    private final PetKindRepository petKindRepository;
    private final PetSidoRepository petSidoRepository;
    private final PetSigunguRepository petSigunguRepository;
    private final PetAbandonmentRepository petAbandonmentRepository;

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

    public void fetchAllApi() {
        fetchPetFacility();
        fetchPetShelter();
        fetchPetKind();
        fetchPetSido();
        fetchPetSigungu();
        fetchPetAbandonment();
    }
    @Transactional
    public void fetchPetFacility() {
        int perPage = 1000;
        int page = 1;

        String initialUrl = petFacilityApiUrl + "?page=" + page + "&perPage=" + perPage + "&returnType=JSON"
                + "&serviceKey=" + apiKey;

        ResponseEntity<PetFacilityWrapper> initialResponse = restTemplate.getForEntity(initialUrl, PetFacilityWrapper.class);
        PetFacilityWrapper initialBody = initialResponse.getBody();
        if (initialBody == null || initialBody.getData() == null) return;

        int totalCount = initialBody.getTotalCount();
        int totalPages = (int) Math.ceil((double) totalCount / perPage);

        for (int i = 1; i <= totalPages; i++) {
            String url = petFacilityApiUrl + "?page=" + i + "&perPage=" + perPage + "&returnType=JSON" + "&serviceKey=" + apiKey;
            ResponseEntity<PetFacilityWrapper> response = restTemplate.getForEntity(url, PetFacilityWrapper.class);
            PetFacilityWrapper body = response.getBody();

            if (body != null && body.getData() != null) {
                for (PetFacilityDto dto : body.getData()) {

                    // 중복 제거용 key 생성
                    String facilityKey = (dto.getFacilityName() == null ? "" : dto.getFacilityName().trim()) + "-" +
                                         (dto.getLotAddress() == null ? "" : dto.getLotAddress().trim());

                    // 이미 동일한 key가 있으면 skip
                    boolean exists = petFacilityRepository.existsByFacilityKey(facilityKey);
                    if (exists) continue;

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
                            .facilityKey(facilityKey) 
                            .build();

                    petFacilityRepository.save(entity);
                }
            }
        }
    }



	@Transactional
	public void fetchPetShelter() {
	    int numOfRows = 100;
	    int pageNo = 1;
	    String initialUrl = petShelterApiUrl + "?serviceKey=" + apiKey + "&numOfRows=" + numOfRows + "&pageNo=" + pageNo
	            + "&_type=json";
	    ResponseEntity<PetShelterWrapper> initialResponse = restTemplate.getForEntity(initialUrl,
	            PetShelterWrapper.class);
	    PetShelterWrapper initialWrapper = initialResponse.getBody();
	    if (initialWrapper == null || initialWrapper.getResponse() == null
	            || initialWrapper.getResponse().getBody() == null)
	        return;
	    int totalCount = initialWrapper.getResponse().getBody().getTotalCount();
	    int totalPages = (int) Math.ceil((double) totalCount / numOfRows);
	    for (int i = 1; i <= totalPages; i++) {
	        String url = petShelterApiUrl + "?serviceKey=" + apiKey + "&numOfRows=" + numOfRows + "&pageNo=" + i
	                + "&_type=json";
	        try {
	            ResponseEntity<PetShelterWrapper> response = restTemplate.getForEntity(url, PetShelterWrapper.class);
	            PetShelterWrapper wrapper = response.getBody();
	            if (wrapper == null || wrapper.getResponse() == null || wrapper.getResponse().getBody() == null)
	                continue;
	            List<PetShelterDto> list = wrapper.getResponse().getBody().getItems().getItem();
	            if (list == null || list.isEmpty())
	                continue;
	            for (PetShelterDto dto : list) {
	                PetShelterEntity entity = PetShelterEntity.builder()
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
	                petShelterRepository.save(entity);
	            }
	        } catch (Exception e) {
	            log.error(e.getMessage());
	        }
	    }
	}

	
	@Transactional
    public void fetchPetKind() {
        for (String upKindCd : Arrays.asList("417000", "422400", "429900")) {
            int pageNo = 1;
            int numOfRows = 100;
            while (true) {
                String url = kindApiUrl + "?serviceKey=" + apiKey + "&up_kind_cd=" + upKindCd + "&_type=json"
                        + "&numOfRows=" + numOfRows + "&pageNo=" + pageNo;
                try {
                    ResponseEntity<PetKindWrapper> response = restTemplate.getForEntity(url, PetKindWrapper.class);
                    PetKindWrapper wrapper = response.getBody();
                    if (wrapper == null || wrapper.getResponse() == null || wrapper.getResponse().getBody() == null
                            || wrapper.getResponse().getBody().getItems() == null)
                        break;
                    List<PetKindDto> kindList = wrapper.getResponse().getBody().getItems().getItem();
                    if (kindList == null || kindList.isEmpty())
                        break;
                    for (PetKindDto dto : kindList) {
                        PetKindEntity entity = PetKindEntity.builder()
                                .kindCd(dto.getKindCd())
                                .kindNm(dto.getKindNm())
                                .upKindCd(upKindCd)
                                .build();
                        petKindRepository.save(entity);
                    }
                    pageNo++;
                } catch (Exception e) {
                    log.error("upKindCd={} 페이지 {} 실패: {}", upKindCd, pageNo, e.getMessage());
                    break;
                }
            }
        }
    }

    @Transactional
    public void fetchPetSido() {
        String url = sidoApiUrl + "?serviceKey=" + apiKey + "&numOfRows=50&_type=json";
        try {
            ResponseEntity<PetSidoWrapper> response = restTemplate.getForEntity(url, PetSidoWrapper.class);
            PetSidoWrapper wrapper = response.getBody();
            if (wrapper == null || wrapper.getResponse() == null || wrapper.getResponse().getBody() == null
                    || wrapper.getResponse().getBody().getItems() == null
                    || wrapper.getResponse().getBody().getItems().getItem() == null) return;
            List<PetSidoDto> sidoList = wrapper.getResponse().getBody().getItems().getItem();
            if (sidoList.isEmpty()) return;
            for (PetSidoDto dto : sidoList) {
                PetSidoEntity entity = PetSidoEntity.builder()
                        .orgCd(dto.getOrgCd())
                        .orgdownNm(dto.getOrgdownNm())
                        .build();
                petSidoRepository.save(entity);
            }
        } catch (Exception e) {
            log.error("시도 API 호출 실패: {}", e.getMessage());
        }
    }

    @Transactional
    public void fetchPetSigungu() {
        List<PetSidoEntity> sidoList = petSidoRepository.findAll();
        for (PetSidoEntity sido : sidoList) {
            String uprCd = sido.getOrgCd();
            int pageNo = 1;
            int numOfRows = 100;
            while (true) {
                String url = sigunguApiUrl + "?serviceKey=" + apiKey + "&upr_cd=" + uprCd + "&_type=json"
                        + "&numOfRows=" + numOfRows + "&pageNo=" + pageNo;
                try {
                    ResponseEntity<PetSigunguWrapper> response = restTemplate.getForEntity(url, PetSigunguWrapper.class);
                    PetSigunguWrapper wrapper = response.getBody();
                    if (wrapper == null || wrapper.getResponse() == null || wrapper.getResponse().getBody() == null
                            || wrapper.getResponse().getBody().getItems() == null) break;
                    List<PetSigunguDto> sigunguList = wrapper.getResponse().getBody().getItems().getItem();
                    if (sigunguList == null || sigunguList.isEmpty()) break;
                    for (PetSigunguDto dto : sigunguList) {
                        PetSigunguEntity entity = PetSigunguEntity.builder()
                                .orgCd(dto.getOrgCd())
                                .orgdownNm(dto.getOrgdownNm())
                                .uprCd(dto.getUprCd())
                                .build();
                        petSigunguRepository.save(entity);
                    }
                    pageNo++;
                } catch (Exception e) {
                    log.error("시군구 API 오류: uprCd={}, page={} => {}", uprCd, pageNo, e.getMessage());
                    break;
                }
            }
        }
    }

    @Transactional
    public void fetchPetAbandonment() {
        int page = 1;
        int numOfRows = 1000;
        while (true) {
            String url = abandonmentPublicApiurl + "?serviceKey=" + apiKey + "&_type=json"
                    + "&numOfRows=" + numOfRows + "&pageNo=" + page;
            try {
                ResponseEntity<PetAbandonmentWrapper> response = restTemplate.getForEntity(url, PetAbandonmentWrapper.class);
                PetAbandonmentWrapper wrapper = response.getBody();
                if (wrapper == null || wrapper.getResponse() == null || wrapper.getResponse().getBody() == null
                        || wrapper.getResponse().getBody().getItems() == null
                        || wrapper.getResponse().getBody().getItems().getItem() == null) break;
                List<PetAbandonmentDto> itemList = wrapper.getResponse().getBody().getItems().getItem();
                if (itemList.isEmpty()) break;
                for (PetAbandonmentDto dto : itemList) {
                    PetAbandonmentEntity entity = PetAbandonmentEntity.builder()
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
                            .careNm(dto.getCareNm())
                            .careTel(dto.getCareTel())
                            .careAddr(dto.getCareAddr())
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
                    petAbandonmentRepository.save(entity);
                }
                int totalCount = Integer.parseInt(wrapper.getResponse().getBody().getTotalCount());
                int totalPages = (int) Math.ceil((double) totalCount / numOfRows);
                if (page >= totalPages) break;
                page++;
            } catch (Exception e) {
                log.error("API 호출 실패 (page={}): {}", page, e.getMessage());
                break;
            }
        }
    }
}
