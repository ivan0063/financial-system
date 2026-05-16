package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.out;

import java.util.Map;

public interface ScryfallApiClient {
    Map<String, Object> search(String query, int page);
    Map<String, Object> getById(String id);
    Map<String, Object> getByName(String name);
}
