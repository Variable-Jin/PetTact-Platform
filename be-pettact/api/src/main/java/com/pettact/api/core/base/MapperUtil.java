package com.pettact.api.core.base;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

@Component
public class MapperUtil extends ModelMapper {
   // 주문 내역 맵핑시 사용
   MapperUtil() {
      this.getConfiguration()
         .setFieldAccessLevel(AccessLevel.PRIVATE)
         .setFieldMatchingEnabled(true)
         .setMatchingStrategy(MatchingStrategies.STRICT);
   }

}
