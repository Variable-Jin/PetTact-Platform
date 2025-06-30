package com.pettact.api.pet.dto.wrapper;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pettact.api.pet.dto.PetSigunguDto;
import com.pettact.api.pet.dto.wrapper.PetSidoWrapper.Body;
import com.pettact.api.pet.dto.wrapper.PetSidoWrapper.Header;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter 
@Setter
public class PetSigunguWrapper {
    private Response response;

    @Getter 
    @Setter
    public static class Response {
        private Header header;
        private Body body;
    }

    @Getter 
    @Setter
    public static class Header {
        private String reqNo;
        private String resultCode;
        private String resultMsg;
        private String errorMsg;
    }

    @Getter 
    @Setter
    public static class Body {
        private String pageNo;
        private String totalCount;
        private String numOfRows;
        private Items items;

        @Getter 
        @Setter
        public static class Items {
            @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            private List<PetSigunguDto> item;
        }
    }
}