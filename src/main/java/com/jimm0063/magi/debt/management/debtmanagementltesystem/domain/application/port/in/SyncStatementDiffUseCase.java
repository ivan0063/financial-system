package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.enums.SyncMode;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.StatementDiffResult;

public interface SyncStatementDiffUseCase {
    void sync(StatementDiffResult diffResult, SyncMode mode);
}
