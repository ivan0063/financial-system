package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ScryfallClientConfig {

    @Bean
    public RestClient scryfallRestClient() {
        return RestClient.builder()
                .baseUrl("https://api.scryfall.com")
                .defaultHeader("User-Agent", "TCG-Collection-App/1.0")
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
