package com.example.gympt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@SpringBootApplication
public class GymPtApplication {

    public static void main(String[] args) {
        SpringApplication.run(GymPtApplication.class, args);
    }

}
