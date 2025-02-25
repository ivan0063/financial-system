package mx.magi.jimm0063.financial.system.debt.application.service;

import mx.magi.jimm0063.financial.system.debt.domain.dto.DebtModel;

public interface DebtService {
    DebtModel deleteDebt(String debtId);
}
