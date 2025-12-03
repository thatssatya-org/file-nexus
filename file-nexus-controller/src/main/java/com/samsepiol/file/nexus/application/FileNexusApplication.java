package com.samsepiol.file.nexus.application;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication(scanBasePackages = "com.samsepiol.*")
@EnableAsync
@OpenAPIDefinition
public class FileNexusApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileNexusApplication.class, args);
    }
}
