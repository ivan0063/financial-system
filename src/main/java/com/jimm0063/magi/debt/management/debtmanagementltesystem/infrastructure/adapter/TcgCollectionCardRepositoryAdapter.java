package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.adapter;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.out.TcgCollectionCardRepository;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.TcgCollectionCard;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.entity.TcgCollectionCardEntity;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.entity.TcgCollectionEntity;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.mapper.TcgCollectionCardMapper;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.persistence.TcgCollectionCardJpaRepository;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.persistence.TcgCollectionJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TcgCollectionCardRepositoryAdapter implements TcgCollectionCardRepository {

    private final TcgCollectionCardJpaRepository jpaRepository;
    private final TcgCollectionJpaRepository collectionJpaRepository;
    private final TcgCollectionCardMapper mapper;

    public TcgCollectionCardRepositoryAdapter(TcgCollectionCardJpaRepository jpaRepository,
                                              TcgCollectionJpaRepository collectionJpaRepository,
                                              TcgCollectionCardMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.collectionJpaRepository = collectionJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<TcgCollectionCard> findByCollectionId(Long collectionId) {
        return jpaRepository.findByCollection_Id(collectionId).stream().map(mapper::toModel).toList();
    }

    @Override
    public Optional<TcgCollectionCard> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toModel);
    }

    @Override
    public TcgCollectionCard save(TcgCollectionCard card) {
        TcgCollectionEntity collectionRef = collectionJpaRepository.getReferenceById(card.getCollectionId());
        TcgCollectionCardEntity entity = new TcgCollectionCardEntity();
        entity.setId(card.getId());
        entity.setCollection(collectionRef);
        entity.setScryfallId(card.getScryfallId());
        entity.setCardName(card.getCardName());
        entity.setSetCode(card.getSetCode());
        entity.setCollectorNumber(card.getCollectorNumber());
        entity.setImageUri(card.getImageUri());
        entity.setQuantity(card.getQuantity());
        return mapper.toModel(jpaRepository.save(entity));
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public int sumQuantityByScryfallId(String scryfallId) {
        return jpaRepository.sumQuantityByScryfallId(scryfallId);
    }
}
