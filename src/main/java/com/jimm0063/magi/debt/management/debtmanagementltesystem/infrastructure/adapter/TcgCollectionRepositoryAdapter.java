package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.adapter;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.out.TcgCollectionRepository;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.TcgCollection;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.entity.TcgCollectionEntity;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.mapper.TcgCollectionMapper;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.persistence.TcgCollectionJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TcgCollectionRepositoryAdapter implements TcgCollectionRepository {

    private final TcgCollectionJpaRepository jpaRepository;
    private final TcgCollectionMapper mapper;

    public TcgCollectionRepositoryAdapter(TcgCollectionJpaRepository jpaRepository,
                                          TcgCollectionMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<TcgCollection> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toModel).toList();
    }

    @Override
    public Optional<TcgCollection> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toModel);
    }

    @Override
    public TcgCollection save(TcgCollection collection) {
        TcgCollectionEntity entity = mapper.toEntity(collection);
        return mapper.toModel(jpaRepository.save(entity));
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
