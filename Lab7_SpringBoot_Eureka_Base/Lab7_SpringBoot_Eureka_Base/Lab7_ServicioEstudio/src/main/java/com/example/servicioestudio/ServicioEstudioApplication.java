package com.example.servicioestudio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ServicioEstudioApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServicioEstudioApplication.class, args);
    }
}
