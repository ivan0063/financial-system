package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.enums.BulkItemAction;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

/** A set of statement files extracted together, waiting for the user to review and execute. */
public record BulkStatementBatch(List<BulkStatementItem> items) implements Serializable {

    public int totalNew()       { return sumDiff(StatementDiffResult::newDebts); }
    public int totalUpdated()   { return sumDiff(StatementDiffResult::updatedDebts); }
    public int totalCompleted() { return sumDiff(StatementDiffResult::completedDebts); }
    public int totalUnchanged() { return sumDiff(StatementDiffResult::unchangedDebts); }
    public int totalIgnored()   { return sumDiff(StatementDiffResult::ignoredDebts); }

    public long failedCount() {
        return items.stream().filter(BulkStatementItem::hasError).count();
    }

    public long actionableCount() {
        return items.stream().filter(BulkStatementItem::isActionable).count();
    }

    public List<BulkStatementItem> sourceOfTruthItems() {
        return items.stream()
                .filter(i -> i.isActionable() && i.getAction() == BulkItemAction.SOURCE_OF_TRUTH)
                .toList();
    }

    private int sumDiff(Function<StatementDiffResult, List<DebtDiff>> category) {
        return items.stream()
                .filter(i -> i.getDiff() != null)
                .mapToInt(i -> category.apply(i.getDiff()).size())
                .sum();
    }
}
