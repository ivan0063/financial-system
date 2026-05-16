package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.out;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.TcgCollection;

import java.util.List;
import java.util.Optional;

public interface TcgCollectionRepository {
    List<TcgCollection> findAll();
    Optional<TcgCollection> findById(Long id);
    TcgCollection save(TcgCollection collection);
    void deleteById(Long id);
}
