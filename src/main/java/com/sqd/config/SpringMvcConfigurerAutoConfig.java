package com.sqd.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@ConditionalOnClass(WebMvcConfigurer.class)
public class SpringMvcConfigurerAutoConfig {

    @Bean
    @ConditionalOnMissingClass
    public WebMvcConfig webMvcConfig(){
        return new WebMvcConfig();
    }
}
