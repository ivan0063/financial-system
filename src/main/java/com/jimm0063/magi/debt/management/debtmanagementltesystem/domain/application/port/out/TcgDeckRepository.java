package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.out;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.TcgDeck;

import java.util.List;
import java.util.Optional;

public interface TcgDeckRepository {
    List<TcgDeck> findAll();
    Optional<TcgDeck> findById(Long id);
    TcgDeck save(TcgDeck deck);
    void deleteById(Long id);
}
