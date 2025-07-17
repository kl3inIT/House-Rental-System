package com.rental.houserental;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HouseRentalApplication {

    public static void main(String[] args) {
        SpringApplication.run(HouseRentalApplication.class, args);
    }

}
