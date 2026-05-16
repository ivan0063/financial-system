package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.dto.DeckCardOwnershipDto;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.TcgDeckCard;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model.tcg.AddCardToDeckReq;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model.tcg.UpdateDeckCardReq;

import java.util.List;

public interface TcgDeckCardUseCase {
    List<DeckCardOwnershipDto> getDeckWithOwnership(Long deckId);
    TcgDeckCard addCard(Long deckId, AddCardToDeckReq req);
    TcgDeckCard updateCard(Long deckId, Long cardId, UpdateDeckCardReq req);
    void removeCard(Long deckId, Long cardId);
}
