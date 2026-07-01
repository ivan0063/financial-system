package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.persistence;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.entity.ReceivableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceivableJpaRepository extends JpaRepository<ReceivableEntity, Integer> {
    List<ReceivableEntity> findAllByDebtSysUser_Email(String email);
    List<ReceivableEntity> findAllByDebtSysUser_EmailAndActiveTrue(String email);
}
