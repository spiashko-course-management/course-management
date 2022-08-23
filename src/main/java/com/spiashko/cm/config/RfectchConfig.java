package com.spiashko.cm.config;

import com.spiashko.rfetch.jpa.smart.FetchSmartTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

@Configuration
public class RfectchConfig {

    @Bean
    public FetchSmartTemplate fetchSmartTemplate(EntityManager entityManager) {
        return new FetchSmartTemplate(entityManager);
    }

}
