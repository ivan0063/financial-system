package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.adapter;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.out.TcgDeckCardRepository;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.TcgDeckCard;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.entity.TcgDeckCardEntity;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.entity.TcgDeckEntity;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.mapper.TcgDeckCardMapper;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.persistence.TcgDeckCardJpaRepository;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.persistence.TcgDeckJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TcgDeckCardRepositoryAdapter implements TcgDeckCardRepository {

    private final TcgDeckCardJpaRepository jpaRepository;
    private final TcgDeckJpaRepository deckJpaRepository;
    private final TcgDeckCardMapper mapper;

    public TcgDeckCardRepositoryAdapter(TcgDeckCardJpaRepository jpaRepository,
                                        TcgDeckJpaRepository deckJpaRepository,
                                        TcgDeckCardMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.deckJpaRepository = deckJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<TcgDeckCard> findByDeckId(Long deckId) {
        return jpaRepository.findByDeck_Id(deckId).stream().map(mapper::toModel).toList();
    }

    @Override
    public Optional<TcgDeckCard> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toModel);
    }

    @Override
    public TcgDeckCard save(TcgDeckCard card) {
        TcgDeckEntity deckRef = deckJpaRepository.getReferenceById(card.getDeckId());
        TcgDeckCardEntity entity = new TcgDeckCardEntity();
        entity.setId(card.getId());
        entity.setDeck(deckRef);
        entity.setScryfallId(card.getScryfallId());
        entity.setCardName(card.getCardName());
        entity.setSetCode(card.getSetCode());
        entity.setCollectorNumber(card.getCollectorNumber());
        entity.setImageUri(card.getImageUri());
        entity.setQuantity(card.getQuantity());
        entity.setBoard(card.getBoard());
        return mapper.toModel(jpaRepository.save(entity));
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
