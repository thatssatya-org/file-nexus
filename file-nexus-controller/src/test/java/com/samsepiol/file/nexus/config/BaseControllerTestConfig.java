package com.samsepiol.file.nexus.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.samsepiol.*"})
public class BaseControllerTestConfig {

    public static void main(String[] args) {
        SpringApplication.run(BaseControllerTestConfig.class, args);
    }

}