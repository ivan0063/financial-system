package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.adapter;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.out.TcgDeckRepository;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.TcgDeck;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.entity.TcgDeckEntity;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.mapper.TcgDeckMapper;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.persistence.TcgDeckJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TcgDeckRepositoryAdapter implements TcgDeckRepository {

    private final TcgDeckJpaRepository jpaRepository;
    private final TcgDeckMapper mapper;

    public TcgDeckRepositoryAdapter(TcgDeckJpaRepository jpaRepository, TcgDeckMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<TcgDeck> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toModel).toList();
    }

    @Override
    public Optional<TcgDeck> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toModel);
    }

    @Override
    public TcgDeck save(TcgDeck deck) {
        TcgDeckEntity entity = mapper.toEntity(deck);
        return mapper.toModel(jpaRepository.save(entity));
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
