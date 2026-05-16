package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.persistence;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.entity.TcgCollectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TcgCollectionJpaRepository extends JpaRepository<TcgCollectionEntity, Long> {
}
