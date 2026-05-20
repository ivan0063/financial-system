package com.jimm0063.magi.debt.management.debtmanagementltesystem.application.service;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.AccountStatementDataExtractionUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.Debt;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.DebtAccount;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service("BBVA")
public class BbvaAccountStatementService implements AccountStatementDataExtractionUseCase {

    private final UniversalAccountStatementService universalService;

    public BbvaAccountStatementService(UniversalAccountStatementService universalService) {
        this.universalService = universalService;
    }

    @Override
    public List<Debt> extractDebts(MultipartFile accountStatement, DebtAccount debtAccount) {
        return universalService.extractDebts(accountStatement, debtAccount);
    }
}
