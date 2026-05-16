package com.jimm0063.magi.debt.management.debtmanagementltesystem.application.service;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.TcgDeckCardUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.TcgDeckUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.out.TcgCollectionCardRepository;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.out.TcgDeckCardRepository;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.out.TcgDeckRepository;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.dto.DeckCardOwnershipDto;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.exceptions.EntityNotFoundException;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.TcgDeck;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.TcgDeckCard;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model.tcg.AddCardToDeckReq;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model.tcg.CreateDeckReq;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model.tcg.UpdateDeckCardReq;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model.tcg.UpdateDeckReq;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TcgDeckService implements TcgDeckUseCase, TcgDeckCardUseCase {

    private final TcgDeckRepository deckRepository;
    private final TcgDeckCardRepository deckCardRepository;
    private final TcgCollectionCardRepository collectionCardRepository;

    public TcgDeckService(TcgDeckRepository deckRepository,
                          TcgDeckCardRepository deckCardRepository,
                          TcgCollectionCardRepository collectionCardRepository) {
        this.deckRepository = deckRepository;
        this.deckCardRepository = deckCardRepository;
        this.collectionCardRepository = collectionCardRepository;
    }

    @Override
    public List<TcgDeck> findAll() {
        return deckRepository.findAll();
    }

    @Override
    public TcgDeck findById(Long id) {
        return deckRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Deck " + id + " not found"));
    }

    @Override
    public TcgDeck create(CreateDeckReq req) {
        TcgDeck deck = new TcgDeck();
        deck.setName(req.getName());
        deck.setDescription(req.getDescription());
        deck.setFormat(req.getFormat());
        return deckRepository.save(deck);
    }

    @Override
    public TcgDeck update(Long id, UpdateDeckReq req) {
        TcgDeck deck = findById(id);
        deck.setName(req.getName());
        deck.setDescription(req.getDescription());
        deck.setFormat(req.getFormat());
        return deckRepository.save(deck);
    }

    @Override
    public void delete(Long id) {
        findById(id);
        deckRepository.deleteById(id);
    }

    @Override
    public List<DeckCardOwnershipDto> getDeckWithOwnership(Long deckId) {
        findById(deckId);
        return deckCardRepository.findByDeckId(deckId).stream()
                .map(deckCard -> {
                    int ownedQty = collectionCardRepository.sumQuantityByScryfallId(deckCard.getScryfallId());
                    DeckCardOwnershipDto dto = new DeckCardOwnershipDto();
                    dto.setId(deckCard.getId());
                    dto.setScryfallId(deckCard.getScryfallId());
                    dto.setCardName(deckCard.getCardName());
                    dto.setSetCode(deckCard.getSetCode());
                    dto.setCollectorNumber(deckCard.getCollectorNumber());
                    dto.setImageUri(deckCard.getImageUri());
                    dto.setQuantity(deckCard.getQuantity());
                    dto.setBoard(deckCard.getBoard());
                    dto.setOwnedQuantity(ownedQty);
                    dto.setOwned(ownedQty >= deckCard.getQuantity());
                    return dto;
                })
                .toList();
    }

    @Override
    public TcgDeckCard addCard(Long deckId, AddCardToDeckReq req) {
        findById(deckId);
        TcgDeckCard card = new TcgDeckCard();
        card.setDeckId(deckId);
        card.setScryfallId(req.getScryfallId());
        card.setCardName(req.getCardName());
        card.setSetCode(req.getSetCode());
        card.setCollectorNumber(req.getCollectorNumber());
        card.setImageUri(req.getImageUri());
        card.setQuantity(req.getQuantity());
        card.setBoard(req.getBoard());
        return deckCardRepository.save(card);
    }

    @Override
    public TcgDeckCard updateCard(Long deckId, Long cardId, UpdateDeckCardReq req) {
        findById(deckId);
        TcgDeckCard card = deckCardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Deck card " + cardId + " not found"));
        card.setQuantity(req.getQuantity());
        if (req.getBoard() != null) {
            card.setBoard(req.getBoard());
        }
        return deckCardRepository.save(card);
    }

    @Override
    public void removeCard(Long deckId, Long cardId) {
        findById(deckId);
        deckCardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Deck card " + cardId + " not found"));
        deckCardRepository.deleteById(cardId);
    }
}
