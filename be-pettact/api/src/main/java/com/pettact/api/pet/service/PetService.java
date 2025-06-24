package com.pettact.api.pet.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.pettact.api.pet.dto.PetAbandonmentDto;
import com.pettact.api.pet.dto.PetKindDto;
import com.pettact.api.pet.dto.PetShelterDto;
import com.pettact.api.pet.dto.PetSidoDto;
import com.pettact.api.pet.dto.PetSigunguDto;
import com.pettact.api.pet.dto.wrapper.PetAbandonmentWrapper;
import com.pettact.api.pet.dto.wrapper.PetFacilityWrapper;
import com.pettact.api.pet.dto.wrapper.PetKindWrapper;
import com.pettact.api.pet.dto.wrapper.PetShelterWrapper;
import com.pettact.api.pet.dto.wrapper.PetSidoWrapper;
import com.pettact.api.pet.dto.wrapper.PetSigunguWrapper;
import com.pettact.api.pet.entity.PetAbandonmentEntity;
import com.pettact.api.pet.entity.PetFacilityEntity;
import com.pettact.api.pet.entity.PetKindEntity;
import com.pettact.api.pet.entity.PetShelterEntity;
import com.pettact.api.pet.entity.PetSidoEntity;
import com.pettact.api.pet.entity.PetSigunguEntity;
import com.pettact.api.pet.repository.PetAbandonmentRepository;
import com.pettact.api.pet.repository.PetFacilityRepository;
import com.pettact.api.pet.repository.PetKindRepository;
import com.pettact.api.pet.repository.PetShelterRepository;
import com.pettact.api.pet.repository.PetSidoRepository;
import com.pettact.api.pet.repository.PetSigunguRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PetService {

	private final RestTemplate restTemplate;

	private final PetFacilityRepository petFacilityRepository;
	private final PetShelterRepository petShelterRepository;
	private final PetKindRepository petKindRepository;
	private final PetSidoRepository petSidoRepository;
	private final PetSigunguRepository petSigunguRepository;
	private final PetAbandonmentRepository petAbandonmentRepository;
	// api 인증키
	@Value("${pet-api-service-key-decoded}")
	private String apiKey;

	// 반려동물 동반가능 업장 api url
	@Value("${pet-facility-api-url}")
	private String petFacilityApiUrl;

	// 동물 보호소 정보 api url
	@Value("${pet-shelter-api-url}")
	private String petShelterApiUrl;

	// 시군구 정보 url
	@Value("${sigungu-api-url}")
	private String sigunguApiUrl;

	// 시도 정보 url
	@Value("${sido-api-url}")
	private String sidoApiUrl;

	// 동물품종 정보 url
	@Value("${kind-api-url}")
	private String kindApiUrl;

	// 유기동물 조회 url
	@Value("${abandonment-public-api-url}")
	private String abandonmentPublicApiurl;

	// 각각 api 호출
	public void fetchAllApi() {
		fetchPetFacility(); // 반려동물 동반가능 업장 - 중복 제거 x
		fetchPetShelter(); // 동물 보호소 - 중복제거 x
		fetchPetKind(); // 동물품종
		fetchPetSido();
		fetchPetSigungu();
		fetchPetAbandonment();
	}

    @Transactional
    public void fetchPetAbandonment() {
        int page = 1;
        int numOfRows = 1000;

        while (true) {
            String url = abandonmentPublicApiurl
                    + "?serviceKey=" + apiKey
                    + "&_type=json"
                    + "&numOfRows=" + numOfRows
                    + "&pageNo=" + page;

            try {
                ResponseEntity<PetAbandonmentWrapper> response =
                        restTemplate.getForEntity(url, PetAbandonmentWrapper.class);

                PetAbandonmentWrapper wrapper = response.getBody();

                if (wrapper == null || wrapper.getResponse() == null || wrapper.getResponse().getBody() == null
                        || wrapper.getResponse().getBody().getItems() == null
                        || wrapper.getResponse().getBody().getItems().getItem() == null) {
                    log.warn("유기동물 데이터 응답이 없습니다. page={}", page);
                    break;
                }

                List<PetAbandonmentDto> itemList = wrapper.getResponse().getBody().getItems().getItem();
                if (itemList.isEmpty()) {
                    log.info("더 이상 수집할 데이터가 없습니다. page={}", page);
                    break;
                }

                for (PetAbandonmentDto dto : itemList) {
                    if (!petAbandonmentRepository.existsByDesertionNo(dto.getDesertionNo())) {
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
                        log.info("저장: {}", dto.getDesertionNo());
                    } else {
                        log.info("중복 제외: {}", dto.getDesertionNo());
                    }
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



	public void fetchPetFacility() {
		int perPage = 1000;
		int page = 1;

		// 일단 첫 페이지로 totalCount 받아오기
		String initialUrl = petFacilityApiUrl + "?page=" + page + "&perPage=" + perPage + "&returnType=JSON"
				+ "&serviceKey=" + apiKey;

		ResponseEntity<PetFacilityWrapper> initialResponse = restTemplate.getForEntity(initialUrl,
				PetFacilityWrapper.class);

		PetFacilityWrapper initialBody = initialResponse.getBody();

		if (initialBody == null || initialBody.getData() == null)
			return;

		int totalCount = initialBody.getTotalCount();
		int totalPages = (int) Math.ceil((double) totalCount / perPage);

		for (int i = 1; i <= totalPages; i++) {
			String url = petFacilityApiUrl + "?page=" + i + "&perPage=" + perPage + "&returnType=JSON" + "&serviceKey="
					+ apiKey;

			ResponseEntity<PetFacilityWrapper> response = restTemplate.getForEntity(url, PetFacilityWrapper.class);

			PetFacilityWrapper body = response.getBody();

			if (body != null && body.getData() != null) {
				body.getData().forEach(dto -> {
					PetFacilityEntity entity = PetFacilityEntity.builder().buildingNumber(dto.getBuildingNumber())
							.longitude(dto.getLongitude()).placeDescription(dto.getPlaceDescription())
							.roadName(dto.getRoadName()).roadAddress(dto.getRoadAddress()).riName(dto.getRiName())
							.petAllowed(dto.getPetAllowed()).petOnly(dto.getPetOnly()).petLimit(dto.getPetLimit())
							.lotNumber(dto.getLotNumber()).legalDongName(dto.getLegalDongName())
							.sigunguName(dto.getSigunguName()).sidoName(dto.getSidoName())
							.facilityName(dto.getFacilityName()).additionalFee(dto.getAdditionalFee())
							.postalCode(dto.getPostalCode()).openHours(dto.getOpenHours()).latitude(dto.getLatitude())
							.allowedPetSize(dto.getAllowedPetSize()).entranceFeeInfo(dto.getEntranceFeeInfo())
							.isIndoor(dto.getIsIndoor()).isOutdoor(dto.getIsOutdoor()).phone(dto.getPhone())
							.parkingAvailable(dto.getParkingAvailable()).lotAddress(dto.getLotAddress())
							.lastUpdatedDate(dto.getLastUpdatedDate()).category1(dto.getCategory1())
							.category2(dto.getCategory2()).category3(dto.getCategory3()).homepage(dto.getHomepage())
							.closedDays(dto.getClosedDays()).build();
					petFacilityRepository.save(entity);
				});
			}
		}
	}

	public void fetchPetShelter() {
		int numOfRows = 100;
		int pageNo = 1;

		String initialUrl = petShelterApiUrl + "?serviceKey=" + apiKey + "&numOfRows=" + numOfRows + "&pageNo=" + pageNo
				+ "&_type=json";

		ResponseEntity<PetShelterWrapper> initialResponse = restTemplate.getForEntity(initialUrl,
				PetShelterWrapper.class);

		PetShelterWrapper initialWrapper = initialResponse.getBody();
		if (initialWrapper == null || initialWrapper.getResponse() == null
				|| initialWrapper.getResponse().getBody() == null) {
			return;
		}

		int totalCount = initialWrapper.getResponse().getBody().getTotalCount();
		int totalPages = (int) Math.ceil((double) totalCount / numOfRows);

		for (int i = 1; i <= totalPages; i++) {
			String url = petShelterApiUrl + "?serviceKey=" + apiKey + "&numOfRows=" + numOfRows + "&pageNo=" + i
					+ "&_type=json";

			try {
				ResponseEntity<PetShelterWrapper> response = restTemplate.getForEntity(url, PetShelterWrapper.class);

				PetShelterWrapper wrapper = response.getBody();
				if (wrapper == null || wrapper.getResponse() == null || wrapper.getResponse().getBody() == null) {
					continue;
				}

				List<PetShelterDto> list = wrapper.getResponse().getBody().getItems().getItem();
				if (list == null || list.isEmpty()) {
					continue;
				}

				for (PetShelterDto dto : list) {
					PetShelterEntity entity = PetShelterEntity.builder().careNm(dto.getCareNm())
							.careRegNo(dto.getCareRegNo()).orgNm(dto.getOrgNm()).divisionNm(dto.getDivisionNm())
							.saveTrgtAnimal(dto.getSaveTrgtAnimal()).careAddr(dto.getCareAddr())
							.jibunAddr(dto.getJibunAddr()).lat(dto.getLat()).lng(dto.getLng())
							.dsignationDate(dto.getDsignationDate()).weekOprStime(dto.getWeekOprStime())
							.weekOprEtime(dto.getWeekOprEtime()).weekCellStime(dto.getWeekCellStime())
							.weekCellEtime(dto.getWeekCellEtime()).weekendOprStime(dto.getWeekendOprStime())
							.weekendOprEtime(dto.getWeekendOprEtime()).weekendCellStime(dto.getWeekendCellStime())
							.weekendCellEtime(dto.getWeekendCellEtime()).closeDay(dto.getCloseDay())
							.vetPersonCnt(dto.getVetPersonCnt()).specsPersonCnt(dto.getSpecsPersonCnt())
							.medicalCnt(dto.getMedicalCnt()).breedCnt(dto.getBreedCnt())
							.quarabtineCnt(dto.getQuarabtineCnt()).feedCnt(dto.getFeedCnt())
							.transCarCnt(dto.getTransCarCnt()).careTel(dto.getCareTel()).dataStdDt(dto.getDataStdDt())
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
							|| wrapper.getResponse().getBody().getItems() == null) {
						break;
					}

					List<PetKindDto> kindList = wrapper.getResponse().getBody().getItems().getItem();
					if (kindList == null || kindList.isEmpty()) {
						break;
					}

					for (PetKindDto dto : kindList) {
						boolean exists = petKindRepository.existsByKindCd(dto.getKindCd());

						if (!exists) {
							PetKindEntity entity = PetKindEntity.builder().kindCd(dto.getKindCd())
									.kindNm(dto.getKindNm()).upKindCd(upKindCd).build();
							petKindRepository.save(entity);
							log.info("저장 : {}", dto.getKindCd());
						} else {
							log.info("중복 : {}", dto.getKindCd());
						}
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
		String url = sidoApiUrl + "?serviceKey=" + apiKey + "&numOfRows=50" + "&_type=json";
		try {
			ResponseEntity<PetSidoWrapper> response = restTemplate.getForEntity(url, PetSidoWrapper.class);
			PetSidoWrapper wrapper = response.getBody();
			if (wrapper == null || wrapper.getResponse() == null || wrapper.getResponse().getBody() == null
					|| wrapper.getResponse().getBody().getItems() == null
					|| wrapper.getResponse().getBody().getItems().getItem() == null) {
				log.warn("시도 데이터 응답이 없습니다.");
				return;
			}
			List<PetSidoDto> sidoList = wrapper.getResponse().getBody().getItems().getItem();
			if (sidoList.isEmpty()) {
				log.warn("시도 데이터가 비어 있습니다.");
				return;
			}
			for (PetSidoDto dto : sidoList) {
				boolean exists = petSidoRepository.existsByOrgCd(dto.getOrgCd());

				if (!exists) {
					PetSidoEntity entity = PetSidoEntity.builder().orgCd(dto.getOrgCd()).orgdownNm(dto.getOrgdownNm())
							.build();

					petSidoRepository.save(entity);
					log.info("시도 저장 완료: {} {}", dto.getOrgCd(), dto.getOrgdownNm());
				} else {
					log.info("중복된 시도 데이터로 인해 저장하지 않음: {} {}", dto.getOrgCd(), dto.getOrgdownNm());
				}
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
					ResponseEntity<PetSigunguWrapper> response = restTemplate.getForEntity(url,
							PetSigunguWrapper.class);
					PetSigunguWrapper wrapper = response.getBody();

					if (wrapper == null || wrapper.getResponse() == null || wrapper.getResponse().getBody() == null
							|| wrapper.getResponse().getBody().getItems() == null) {
						log.warn("응답 없음: uprCd={}, page={}", uprCd, pageNo);
						break;
					}

					List<PetSigunguDto> sigunguList = wrapper.getResponse().getBody().getItems().getItem();
					if (sigunguList == null || sigunguList.isEmpty()) {
						log.warn("빈 리스트 수신: uprCd={}, page={}", uprCd, pageNo);
						break;
					}

					for (PetSigunguDto dto : sigunguList) {
						boolean exists = petSigunguRepository.existsByOrgCd(dto.getOrgCd());

						if (!exists) {
							PetSigunguEntity entity = PetSigunguEntity.builder().orgCd(dto.getOrgCd())
									.orgdownNm(dto.getOrgdownNm()).uprCd(dto.getUprCd()).build();

							petSigunguRepository.save(entity);
							log.info("시군구 저장 완료: {} {}", dto.getOrgCd(), dto.getOrgdownNm());
						} else {
							log.info("중복으로 저장 생략: {} {}", dto.getOrgCd(), dto.getOrgdownNm());
						}
					}

					pageNo++;

				} catch (Exception e) {
					log.error("시군구 API 오류: uprCd={}, page={} => {}", uprCd, pageNo, e.getMessage());
					break;
				}
			}
		}
	}

}