package edu.njust.homework_manager.backend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.njust.homework_manager.adapters.GsonDateAdapter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;

@SpringBootApplication
public class BackendApplication {
    // Create a Gson bean with custom adapters
    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .registerTypeAdapter(Date.class, new GsonDateAdapter())
                .create();
    }
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

}
