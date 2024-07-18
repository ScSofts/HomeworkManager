package edu.njust.homework_manager.configurations;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "server.cors")
public class CorsConfig {
    List<String> allowedOrigins;
}
