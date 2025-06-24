package com.pettact.api.pet.dto.wrapper;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pettact.api.pet.dto.PetSigunguDto;
import lombok.Data;
import java.util.List;

@Data
public class PetSigunguWrapper {
    private Response response;

    @Data
    public static class Response {
        private Header header;
        private Body body;
    }

    @Data
    public static class Header {
        private String reqNo;
        private String resultCode;
        private String resultMsg;
        private String errorMsg;
    }

    @Data
    public static class Body {
        private String pageNo;
        private String totalCount;
        private String numOfRows;
        private Items items;

        @Data
        public static class Items {
            @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            private List<PetSigunguDto> item;
        }
    }
}