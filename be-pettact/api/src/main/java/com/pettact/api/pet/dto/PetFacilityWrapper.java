package com.pettact.api.pet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PetFacilityWrapper {

    @JsonProperty("data")
    private List<PetFacilityDto> data;

    @JsonProperty("currentCount")
    private int currentCount;

    @JsonProperty("matchCount")
    private int matchCount;

    @JsonProperty("page")
    private int page;

    @JsonProperty("perPage")
    private int perPage;

    @JsonProperty("totalCount")
    private int totalCount;
}
