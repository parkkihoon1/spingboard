package com.example.spingboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SpingboardApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpingboardApplication.class, args);
    }

}
