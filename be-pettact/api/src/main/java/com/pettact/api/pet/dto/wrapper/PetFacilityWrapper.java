package com.pettact.api.pet.dto.wrapper;

import java.util.List;

import com.pettact.api.pet.dto.PetFacilityDto;
import com.pettact.api.pet.dto.wrapper.PetSidoWrapper.Body;
import com.pettact.api.pet.dto.wrapper.PetSidoWrapper.Header;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class PetFacilityWrapper {

    private List<PetFacilityDto> data;

    private int currentCount;
    private int matchCount;
    private int page;
    private int perPage;
    private int totalCount;
}
