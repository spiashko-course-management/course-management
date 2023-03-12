package com.spiashko.cm.config;

import com.spiashko.rfetch.jpa.smart.SmartFetchTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

@Configuration
public class RfectchConfig {

    @Bean
    public SmartFetchTemplate smartFetchTemplate(EntityManager entityManager) {
        return new SmartFetchTemplate(entityManager);
    }

}
