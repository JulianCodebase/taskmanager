package de.personal.taskservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.personal.common.util.SecurityErrorWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonBeanConfig {

    @Bean
    public SecurityErrorWriter securityErrorWriter(ObjectMapper objectMapper) {
        return new SecurityErrorWriter(objectMapper);
    }
}
