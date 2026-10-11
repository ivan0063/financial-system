package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.BulkStatementBatch;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.BulkSyncResult;

public interface SyncBulkStatementUseCase {
    /** Applies every actionable item with its chosen action, all in a single transaction. */
    BulkSyncResult sync(BulkStatementBatch batch);
}
