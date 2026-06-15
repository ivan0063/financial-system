package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.persistence;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.entity.IgnorableDebtEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IgnorableDebtJpaRepository extends JpaRepository<IgnorableDebtEntity, Integer> {
    List<IgnorableDebtEntity> findAllByActiveTrue();
    List<IgnorableDebtEntity> findByHashSumInAndActiveTrue(List<String> hashSums);
    Optional<IgnorableDebtEntity> findByHashSum(String hashSum);
}
