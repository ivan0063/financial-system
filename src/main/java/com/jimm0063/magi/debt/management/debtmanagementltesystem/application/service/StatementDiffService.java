package com.jimm0063.magi.debt.management.debtmanagementltesystem.application.service;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.enums.AccountStatementType;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.enums.DebtDiffStatus;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.Debt;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.DebtDiff;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.StatementDiffResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Pure diffing logic — no DB access, fully testable in isolation.
 * Compares a list of debts extracted from a statement against the debts
 * currently stored in the database and produces a categorised diff.
 */
@Service
public class StatementDiffService {

    public StatementDiffResult computeDiff(
            String debtAccountCode,
            AccountStatementType parserUsed,
            List<Debt> extractedDebts,
            List<Debt> existingDebts) {

        Map<String, Debt> existingByHash = existingDebts.stream()
                .filter(d -> d.getHashSum() != null)
                .collect(Collectors.toMap(Debt::getHashSum, d -> d, (a, b) -> a));

        List<DebtDiff> newDebts = new ArrayList<>();
        List<DebtDiff> updatedDebts = new ArrayList<>();
        List<DebtDiff> completedDebts = new ArrayList<>();
        List<DebtDiff> unchangedDebts = new ArrayList<>();

        for (Debt extracted : extractedDebts) {
            Debt existing = existingByHash.get(extracted.getHashSum());
            boolean isCompleted = isAtMaxTerm(extracted);

            if (existing == null) {
                if (!isCompleted) {
                    newDebts.add(new DebtDiff(extracted, null, DebtDiffStatus.NEW));
                }
                // A brand-new debt at N/N has no entry to remove — silently skip it.
            } else {
                if (isCompleted) {
                    completedDebts.add(new DebtDiff(extracted, existing, DebtDiffStatus.COMPLETED));
                } else if (installmentAdvanced(extracted, existing)) {
                    updatedDebts.add(new DebtDiff(extracted, existing, DebtDiffStatus.UPDATED));
                } else {
                    unchangedDebts.add(new DebtDiff(extracted, existing, DebtDiffStatus.UNCHANGED));
                }
            }
        }

        return new StatementDiffResult(debtAccountCode, parserUsed,
                newDebts, updatedDebts, completedDebts, unchangedDebts);
    }

    private boolean isAtMaxTerm(Debt debt) {
        return debt.getCurrentInstallment() != null
                && debt.getMaxFinancingTerm() != null
                && debt.getCurrentInstallment() >= debt.getMaxFinancingTerm();
    }

    private boolean installmentAdvanced(Debt extracted, Debt existing) {
        if (extracted.getCurrentInstallment() == null || existing.getCurrentInstallment() == null) return false;
        return !extracted.getCurrentInstallment().equals(existing.getCurrentInstallment());
    }
}
