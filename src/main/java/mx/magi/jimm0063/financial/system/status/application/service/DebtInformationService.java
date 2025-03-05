package mx.magi.jimm0063.financial.system.status.application.service;

import mx.magi.jimm0063.financial.system.status.domain.CardDebtStatus;
import mx.magi.jimm0063.financial.system.status.domain.GlobalDebtStatus;

public interface DebtInformationService {
    GlobalDebtStatus debtStatus(String email);
    CardDebtStatus debtEntityStatus(String entityId);
}
