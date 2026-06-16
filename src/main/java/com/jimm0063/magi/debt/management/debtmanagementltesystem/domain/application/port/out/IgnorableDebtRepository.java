package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.out;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.IgnorableDebt;

import java.util.List;

public interface IgnorableDebtRepository {
    List<IgnorableDebt> findAllActive();
    List<IgnorableDebt> findByHashSumIn(List<String> hashSums);
    IgnorableDebt save(IgnorableDebt ignorableDebt);
    void deactivate(String hashSum);
}
