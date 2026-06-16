package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.Debt;

import java.util.List;

public interface LoadDebtList {
    default List<Debt> saveUnrepeated(List<Debt> debts, String debtAccountCode) {
        return saveUnrepeated(debts, debtAccountCode, List.of());
    }
    List<Debt> saveUnrepeated(List<Debt> debts, String debtAccountCode, List<String> overrideIgnoredHashes);
}
