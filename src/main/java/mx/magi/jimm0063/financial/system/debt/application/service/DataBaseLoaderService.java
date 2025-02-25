package mx.magi.jimm0063.financial.system.debt.application.service;

import mx.magi.jimm0063.financial.system.debt.domain.dto.DebtModel;

import java.io.IOException;
import java.util.List;

public interface DataBaseLoaderService {
    /**
     * This method is meant to load debts into the DB directly from the pdf file
     * account statement¡
     *
     * @param accountStatement byte array of the pdf file
     * @param cardCode String with the card code (Card Id) to load and relate the debts to this card
     * @return
     */
    List<DebtModel> loadDebtFromAccountStatement(byte[] accountStatement, String cardCode) throws IOException;

    /**
     * This method is meant to load This method is meant to load debts into the DB directly
     *
     * @param debtModels
     * @param cardCode
     * @return
     */
    List<DebtModel> loadDebts(List<DebtModel> debtModels, String cardCode);
}
