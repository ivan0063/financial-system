package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.Debt;

import java.util.List;

public interface SourceOfTruthImportUseCase {
    List<Debt> replaceAllWithStatement(List<Debt> incomingDebts, String debtAccountCode);
}
