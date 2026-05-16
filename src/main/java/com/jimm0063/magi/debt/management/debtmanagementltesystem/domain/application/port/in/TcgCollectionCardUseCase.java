package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.TcgCollectionCard;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model.tcg.AddCardToCollectionReq;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model.tcg.UpdateCollectionCardReq;

import java.util.List;

public interface TcgCollectionCardUseCase {
    List<TcgCollectionCard> findByCollection(Long collectionId);
    TcgCollectionCard addCard(Long collectionId, AddCardToCollectionReq req);
    TcgCollectionCard updateCard(Long collectionId, Long cardId, UpdateCollectionCardReq req);
    void removeCard(Long collectionId, Long cardId);
}
