package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in;

import java.util.Map;

public interface ScryfallSearchUseCase {
    Map<String, Object> search(String query, int page);
    Map<String, Object> getById(String id);
    Map<String, Object> getByName(String name);
}
