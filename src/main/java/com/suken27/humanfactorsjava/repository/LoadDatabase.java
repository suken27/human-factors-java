package com.suken27.humanfactorsjava.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.suken27.humanfactorsjava.model.TeamManager;

/**
 * This is a class containing code to test the JPA annotations and the H2 database connection.
 */
@Configuration
public class LoadDatabase {
    
    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(TeamManagerRepository repository) {
        TeamManager teamManager1 = new TeamManager();
        TeamManager teamManager2 = new TeamManager();
        teamManager1.setEmail("pepe@gmail.com");
        teamManager2.setEmail("paco@gmail.com");
        teamManager1.setPassword("pepePassword1");
        teamManager2.setPassword("pacoPassword2");
        return args -> {
            log.info("Prealoading {}", repository.save(teamManager1));
            log.info("Prealoading {}", repository.save(teamManager2));
        };
    }

}
