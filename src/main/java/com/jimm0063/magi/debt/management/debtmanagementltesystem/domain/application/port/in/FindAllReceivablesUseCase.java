package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.Receivable;

import java.util.List;

public interface FindAllReceivablesUseCase {
    List<Receivable> getByEmail(String email);
}
