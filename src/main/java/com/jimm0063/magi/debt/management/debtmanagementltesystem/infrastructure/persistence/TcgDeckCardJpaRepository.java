package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.persistence;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.entity.TcgDeckCardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TcgDeckCardJpaRepository extends JpaRepository<TcgDeckCardEntity, Long> {
    List<TcgDeckCardEntity> findByDeck_Id(Long deckId);
}
