package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.IgnorableDebt;

import java.util.List;

public interface GetIgnorableDebtsUseCase {
    List<IgnorableDebt> getAllIgnorableDebts();
}
