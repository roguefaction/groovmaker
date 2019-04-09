package com.example.groovmaker.configuration;

import com.example.groovmaker.service.HibernateSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

@EnableAutoConfiguration
@Configuration
public class HibernateSearchConfiguration {


    private EntityManager bentityManager;

    @Autowired
    public HibernateSearchConfiguration(EntityManager bentityManager) {
        this.bentityManager = bentityManager;
    }

//    @Bean
//    HibernateSearchService hibernateSearchService() {
//        HibernateSearchService hibernateSearchService = new HibernateSearchService(bentityManager);
//        hibernateSearchService.initializeHibernateSearch();
//        return hibernateSearchService;
//    }
}
