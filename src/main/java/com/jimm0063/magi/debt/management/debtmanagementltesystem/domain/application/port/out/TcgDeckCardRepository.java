package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.out;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.TcgDeckCard;

import java.util.List;
import java.util.Optional;

public interface TcgDeckCardRepository {
    List<TcgDeckCard> findByDeckId(Long deckId);
    Optional<TcgDeckCard> findById(Long id);
    TcgDeckCard save(TcgDeckCard card);
    void deleteById(Long id);
}
