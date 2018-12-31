package org.kumoricon.registration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@SpringBootApplication
public class RegistrationApplication {
    private static final Logger log = LoggerFactory.getLogger(RegistrationApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(RegistrationApplication.class, args);
    }

    @Bean
    CommandLineRunner loadDefaultData(BaseDataService baseDataService) {
        return (args) -> {
            baseDataService.createDefaultData();
        };
    }

    @Configuration
    @EnableGlobalMethodSecurity(
            prePostEnabled = true,
            securedEnabled = true,
            jsr250Enabled = true)
    public class MethodSecurityConfig
            extends GlobalMethodSecurityConfiguration {
    }
}

