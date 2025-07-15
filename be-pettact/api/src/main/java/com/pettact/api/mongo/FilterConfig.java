package com.pettact.api.mongo;


import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.*;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<MongoLoggingFilter> loggingFilter(MongoLoggingFilter filter) {
        FilterRegistrationBean<MongoLoggingFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(filter);
        bean.addUrlPatterns("/v1/*");
        bean.setOrder(1);
        return bean;
    }
}
