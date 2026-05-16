package com.jimm0063.magi.debt.management.debtmanagementltesystem.application.service;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.ScryfallSearchUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.out.ScryfallApiClient;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ScryfallService implements ScryfallSearchUseCase {

    private final ScryfallApiClient scryfallApiClient;

    public ScryfallService(ScryfallApiClient scryfallApiClient) {
        this.scryfallApiClient = scryfallApiClient;
    }

    @Override
    public Map<String, Object> search(String query, int page) {
        return scryfallApiClient.search(query, page);
    }

    @Override
    public Map<String, Object> getById(String id) {
        return scryfallApiClient.getById(id);
    }

    @Override
    public Map<String, Object> getByName(String name) {
        return scryfallApiClient.getByName(name);
    }
}
