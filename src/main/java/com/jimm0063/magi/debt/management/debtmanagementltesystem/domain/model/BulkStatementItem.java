package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.enums.BulkItemAction;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * One file ↔ card pair inside a bulk statement import, holding the computed diff
 * (or the error that prevented computing it) and the action the user picked for it.
 */
@Getter
@Setter
public class BulkStatementItem implements Serializable {

    private String fileName;
    private String debtAccountCode;
    private String accountName;
    private String providerName;
    private BulkItemAction action;

    /** Null when extraction failed. */
    private StatementDiffResult diff;

    /** Every debt read from the file — kept so the diff can be recomputed without re-uploading. */
    private List<Debt> extractedDebts = new ArrayList<>();

    /** Active debts in the system that the statement no longer lists. */
    private List<Debt> missingFromStatement = new ArrayList<>();

    /** Active debts currently stored for the card (all of them are deactivated by SOURCE_OF_TRUTH). */
    private int existingActiveCount;

    private String error;
    private boolean needsPdfPassword;

    /** True when re-uploading the file for this card could fix the error (e.g. a missing password). */
    private boolean retryable;

    public BulkStatementItem(String fileName, String debtAccountCode, BulkItemAction action) {
        this.fileName = fileName;
        this.debtAccountCode = debtAccountCode;
        this.action = action;
    }

    public boolean hasError() {
        return error != null;
    }

    /** Whether executing the batch will touch this card. */
    public boolean isActionable() {
        return !hasError() && action != BulkItemAction.SKIP;
    }

    /** Debts SOURCE_OF_TRUTH will insert as fresh records. */
    public int sourceOfTruthInsertCount() {
        if (diff == null) return 0;
        return diff.newDebts().size() + diff.updatedDebts().size() + diff.unchangedDebts().size();
    }

    public void clearResult() {
        diff = null;
        extractedDebts = new ArrayList<>();
        missingFromStatement = new ArrayList<>();
        existingActiveCount = 0;
        error = null;
        needsPdfPassword = false;
        retryable = false;
    }
}
