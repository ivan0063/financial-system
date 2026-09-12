package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.Debt;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.DebtAccount;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AccountStatementDataExtractionUseCase {
    List<Debt> extractDebts(MultipartFile accountStatement, DebtAccount debtAccount);

    /**
     * Same as {@link #extractDebts(MultipartFile, DebtAccount)} but for encrypted PDFs
     * that need a password to open. Strategies that don't parse PDFs (or don't care
     * about encryption) can ignore the password and fall back to the two-arg method.
     */
    default List<Debt> extractDebts(MultipartFile accountStatement, DebtAccount debtAccount, String password) {
        return extractDebts(accountStatement, debtAccount);
    }
}
