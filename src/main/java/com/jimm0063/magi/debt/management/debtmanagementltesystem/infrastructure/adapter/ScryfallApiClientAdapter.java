package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.adapter;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.out.ScryfallApiClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class ScryfallApiClientAdapter implements ScryfallApiClient {

    private final RestClient restClient;

    public ScryfallApiClientAdapter(RestClient scryfallRestClient) {
        this.restClient = scryfallRestClient;
    }

    @Override
    public Map<String, Object> search(String query, int page) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/cards/search")
                        .queryParam("q", query)
                        .queryParam("page", page)
                        .queryParam("order", "name")
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    @Override
    public Map<String, Object> getById(String id) {
        return restClient.get()
                .uri("/cards/{id}", id)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    @Override
    public Map<String, Object> getByName(String name) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/cards/named")
                        .queryParam("fuzzy", name)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
