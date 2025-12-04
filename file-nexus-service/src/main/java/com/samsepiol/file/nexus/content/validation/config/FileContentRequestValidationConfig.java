package com.samsepiol.file.nexus.content.validation.config;

import com.samsepiol.file.nexus.content.validation.FileContentRequestValidation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Comparator;
import java.util.List;

@Configuration
public class FileContentRequestValidationConfig {

    @Bean
    @Primary
    public List<FileContentRequestValidation> requestValidations(List<FileContentRequestValidation> validations) {
        return validations.stream()
                .sorted(Comparator.comparing(FileContentRequestValidation::getOrder))
                .toList();

    }
}
