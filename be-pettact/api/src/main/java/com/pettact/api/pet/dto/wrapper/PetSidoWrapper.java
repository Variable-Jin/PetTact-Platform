package com.pettact.api.pet.dto.wrapper;

import java.util.List;

import com.pettact.api.pet.dto.PetSidoDto;

import lombok.Data;

@Data
public class PetSidoWrapper {
    private Response response; // 요걸 추가해야 함

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
        private Items items;
        private String numOfRows;
        private String pageNo;
        private String totalCount;

        @Data
        public static class Items {
            private List<PetSidoDto> item;
        }
    }
}
