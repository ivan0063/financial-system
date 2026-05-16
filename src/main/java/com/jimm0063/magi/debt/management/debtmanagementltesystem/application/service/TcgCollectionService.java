package com.jimm0063.magi.debt.management.debtmanagementltesystem.application.service;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.TcgCollectionCardUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.TcgCollectionUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.out.TcgCollectionCardRepository;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.out.TcgCollectionRepository;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.exceptions.EntityNotFoundException;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.TcgCollection;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.TcgCollectionCard;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model.tcg.AddCardToCollectionReq;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model.tcg.CreateCollectionReq;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model.tcg.UpdateCollectionCardReq;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model.tcg.UpdateCollectionReq;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TcgCollectionService implements TcgCollectionUseCase, TcgCollectionCardUseCase {

    private final TcgCollectionRepository collectionRepository;
    private final TcgCollectionCardRepository cardRepository;

    public TcgCollectionService(TcgCollectionRepository collectionRepository,
                                TcgCollectionCardRepository cardRepository) {
        this.collectionRepository = collectionRepository;
        this.cardRepository = cardRepository;
    }

    @Override
    public List<TcgCollection> findAll() {
        return collectionRepository.findAll();
    }

    @Override
    public TcgCollection findById(Long id) {
        return collectionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Collection " + id + " not found"));
    }

    @Override
    public TcgCollection create(CreateCollectionReq req) {
        TcgCollection collection = new TcgCollection();
        collection.setName(req.getName());
        collection.setDescription(req.getDescription());
        return collectionRepository.save(collection);
    }

    @Override
    public TcgCollection update(Long id, UpdateCollectionReq req) {
        TcgCollection collection = findById(id);
        collection.setName(req.getName());
        collection.setDescription(req.getDescription());
        return collectionRepository.save(collection);
    }

    @Override
    public void delete(Long id) {
        findById(id);
        collectionRepository.deleteById(id);
    }

    @Override
    public List<TcgCollectionCard> findByCollection(Long collectionId) {
        findById(collectionId);
        return cardRepository.findByCollectionId(collectionId);
    }

    @Override
    public TcgCollectionCard addCard(Long collectionId, AddCardToCollectionReq req) {
        findById(collectionId);
        TcgCollectionCard card = new TcgCollectionCard();
        card.setCollectionId(collectionId);
        card.setScryfallId(req.getScryfallId());
        card.setCardName(req.getCardName());
        card.setSetCode(req.getSetCode());
        card.setCollectorNumber(req.getCollectorNumber());
        card.setImageUri(req.getImageUri());
        card.setQuantity(req.getQuantity());
        return cardRepository.save(card);
    }

    @Override
    public TcgCollectionCard updateCard(Long collectionId, Long cardId, UpdateCollectionCardReq req) {
        findById(collectionId);
        TcgCollectionCard card = cardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Collection card " + cardId + " not found"));
        card.setQuantity(req.getQuantity());
        return cardRepository.save(card);
    }

    @Override
    public void removeCard(Long collectionId, Long cardId) {
        findById(collectionId);
        cardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Collection card " + cardId + " not found"));
        cardRepository.deleteById(cardId);
    }
}
