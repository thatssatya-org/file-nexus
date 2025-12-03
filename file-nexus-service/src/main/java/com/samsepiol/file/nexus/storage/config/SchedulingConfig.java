package com.samsepiol.file.nexus.storage.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class SchedulingConfig {
    // This class enables scheduling in the application context.
    // It does not require any additional configuration or methods.
    // The @EnableScheduling annotation activates Spring's scheduled task execution capability.
}
