package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.IgnorableDebt;

public interface MarkDebtAsIgnorableUseCase {
    IgnorableDebt markAsIgnorable(String hashSum, String reason);
}
