package com.pettact.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /files/** 요청은 Desktop 경로에서 처리
        registry.addResourceHandler("/files/**")
                //.addResourceLocations("file:///C:/Users/samsung-user/Desktop/");
        		.addResourceLocations("file:///C:/uploads/");
    }
}
