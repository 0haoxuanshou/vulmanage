package org.demon.vulmanage.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Web配置类
 */
@Configuration
public class WebConfig {
    
    /**
     * RestTemplate Bean配置
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}