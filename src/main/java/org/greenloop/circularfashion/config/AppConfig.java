package org.greenloop.circularfashion.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableScheduling
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // Schedule task to update sustainability scores daily
    @Scheduled(cron = "0 0 0 * * *") // Run at 00:00 every day
    public void scheduleTaskEveryDay() {
        // Update sustainability scores
        // Update item statuses
        // Clean up expired tokens
    }

    // Schedule task to update system metrics
    @Scheduled(cron = "0 * * * * *") // Run every minute
    public void scheduleTaskEveryMinute() {
        // Update marketplace listing statuses
        // Update collection request statuses
        // Clean up expired sessions
    }
}
