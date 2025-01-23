package mx.magi.jimm0063.financial.system.debt.application.service;

import mx.magi.jimm0063.financial.system.debt.application.dto.DebtModel;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface AccountStatement {
    /**
     * This method in meant to receive a file and the type of account statement
     * to efectivly extract the debts  on it
     *
     * @return Debts encountered if there are not MSI debts it will return an empty list
     */
    List<DebtModel> extractDebt(byte[] pdfFile) throws IOException;
}
