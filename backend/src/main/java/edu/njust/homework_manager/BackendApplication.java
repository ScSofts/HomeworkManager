package edu.njust.homework_manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.njust.homework_manager.adapters.GsonDateAdapter;
import edu.njust.homework_manager.backend.controllers.FileController;
import edu.njust.homework_manager.configurations.CorsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

import java.io.File;
import java.util.Date;

@SpringBootApplication
@EnableConfigurationProperties(CorsConfig.class)

public class BackendApplication {
    private static final Logger log = LoggerFactory.getLogger(BackendApplication.class);

    // Create a Gson bean with custom adapters
    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .registerTypeAdapter(Date.class, new GsonDateAdapter())
                .create();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(corsConfig.getAllowedOrigins().toArray(String[]::new));
            }
        };
    }

    private final CorsConfig corsConfig;

    @Autowired
    public BackendApplication(CorsConfig corsConfig) {
        this.corsConfig = corsConfig;
    }

    public static void main(String[] args) {
        try{
            if(!new File("data/files").exists()){
               new File("data/files").mkdir();
            }
        }catch (Exception e){
            log.error("Failed to create files directory: {}", e.getMessage());
        }

        SpringApplication.run(BackendApplication.class, args);
    }

}
