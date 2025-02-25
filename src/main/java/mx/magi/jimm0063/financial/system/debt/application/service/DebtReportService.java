package mx.magi.jimm0063.financial.system.debt.application.service;

import java.io.IOException;

public interface DebtReportService {
    byte[] generateDebtReport(String email) throws IOException;
}
