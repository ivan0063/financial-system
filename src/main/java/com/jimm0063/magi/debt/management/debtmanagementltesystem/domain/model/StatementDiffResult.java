package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.enums.AccountStatementType;

import java.io.Serializable;
import java.util.List;

public record StatementDiffResult(
        String debtAccountCode,
        AccountStatementType parserUsed,
        List<DebtDiff> newDebts,
        List<DebtDiff> updatedDebts,
        List<DebtDiff> completedDebts,
        List<DebtDiff> unchangedDebts,
        List<DebtDiff> ignoredDebts
) implements Serializable {

    public boolean isEmpty() {
        return newDebts.isEmpty() && updatedDebts.isEmpty()
                && completedDebts.isEmpty() && unchangedDebts.isEmpty();
    }

    public int totalChanges() {
        return newDebts.size() + updatedDebts.size() + completedDebts.size();
    }
}
