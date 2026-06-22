package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.enums.DebtDiffStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class DebtDiff implements Serializable {

    /** What the statement currently reports for this debt. */
    private Debt extractedDebt;

    /** The matching record already in the DB. Null when status is NEW. */
    private Debt existingDebt;

    private DebtDiffStatus status;

    /** Populated only when status is IGNORED — the reason stored in the ignorable-debt registry. */
    private String ignoreReason;

    public DebtDiff(Debt extractedDebt, Debt existingDebt, DebtDiffStatus status) {
        this.extractedDebt = extractedDebt;
        this.existingDebt = existingDebt;
        this.status = status;
    }
}
