package com.pettact.api.pet.dto.wrapper;

import java.util.List;

import com.pettact.api.pet.dto.PetSidoDto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class PetSidoWrapper {
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
        private Items items;
        private String numOfRows;
        private String pageNo;
        private String totalCount;

        @Getter 
        @Setter
        public static class Items {
            private List<PetSidoDto> item;
        }
    }
}
