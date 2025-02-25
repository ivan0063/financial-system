package mx.magi.jimm0063.financial.system.debt.application.service;

import mx.magi.jimm0063.financial.system.financial.catalog.domain.entity.Debt;

import java.io.IOException;
import java.util.List;

public interface DebtPaymentExcelService {
    byte[] generateExcel(List<Debt> payments) throws IOException;
}
