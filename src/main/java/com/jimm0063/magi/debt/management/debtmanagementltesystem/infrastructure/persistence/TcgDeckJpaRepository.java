package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.persistence;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.entity.TcgDeckEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TcgDeckJpaRepository extends JpaRepository<TcgDeckEntity, Long> {
}
