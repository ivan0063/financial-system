package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.out;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.TcgCollectionCard;

import java.util.List;
import java.util.Optional;

public interface TcgCollectionCardRepository {
    List<TcgCollectionCard> findByCollectionId(Long collectionId);
    Optional<TcgCollectionCard> findById(Long id);
    TcgCollectionCard save(TcgCollectionCard card);
    void deleteById(Long id);
    int sumQuantityByScryfallId(String scryfallId);
}
