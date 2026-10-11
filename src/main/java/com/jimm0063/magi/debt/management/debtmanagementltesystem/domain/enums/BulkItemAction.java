package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.enums;

/** What a bulk statement import should do with one card once the user hits "execute". */
public enum BulkItemAction {
    /** Insert new debts, advance installments, deactivate completed ones. */
    UPDATE,
    /** Rebuild the card from 0: deactivate every active debt and insert the statement's debts. */
    SOURCE_OF_TRUTH,
    /** Leave the card untouched. */
    SKIP;

    public SyncMode toSyncMode() {
        return this == SOURCE_OF_TRUTH ? SyncMode.SOURCE_OF_TRUTH : SyncMode.UPDATE;
    }
}
