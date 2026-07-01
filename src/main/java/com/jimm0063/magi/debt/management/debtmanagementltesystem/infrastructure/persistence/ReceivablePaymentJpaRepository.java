package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.persistence;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.entity.ReceivablePaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceivablePaymentJpaRepository extends JpaRepository<ReceivablePaymentEntity, Integer> {
    List<ReceivablePaymentEntity> findAllByReceivable_IdOrderByCreatedAtDesc(Integer receivableId);
}
