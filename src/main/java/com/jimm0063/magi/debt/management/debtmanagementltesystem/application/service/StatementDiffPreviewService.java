package com.jimm0063.magi.debt.management.debtmanagementltesystem.application.service;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.DebtDuplicationPreventUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.PreviewStatementDiffUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.out.DebtAccountRepository;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.out.DebtRepository;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.exceptions.EntityNotFoundException;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.Debt;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.DebtAccount;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.StatementDiffResult;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.adapter.AccountStatementFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Service
public class StatementDiffPreviewService implements PreviewStatementDiffUseCase {

    private final DebtAccountRepository debtAccountRepository;
    private final DebtRepository debtRepository;
    private final AccountStatementFactory statementFactory;
    private final DebtDuplicationPreventUseCase hashService;
    private final StatementDiffService diffService;

    public StatementDiffPreviewService(
            DebtAccountRepository debtAccountRepository,
            DebtRepository debtRepository,
            AccountStatementFactory statementFactory,
            DebtDuplicationPreventUseCase hashService,
            StatementDiffService diffService) {
        this.debtAccountRepository = debtAccountRepository;
        this.debtRepository = debtRepository;
        this.statementFactory = statementFactory;
        this.hashService = hashService;
        this.diffService = diffService;
    }

    @Override
    public StatementDiffResult preview(MultipartFile file, String debtAccountCode) throws IOException {
        DebtAccount debtAccount = debtAccountRepository
                .findDebtAccountByCodeAndActiveTrue(debtAccountCode)
                .orElseThrow(() -> new EntityNotFoundException("DebtAccount " + debtAccountCode));

        List<Debt> extractedDebts = statementFactory
                .getStrategy(debtAccount.getAccountStatementType())
                .extractDebts(file, debtAccount);

        extractedDebts.forEach(d -> {
            if (d.getHashSum() == null)
                d.setHashSum(hashService.getHashSum(d, debtAccountCode));
            if (d.getOriginalAmount() == null && d.getMonthlyPayment() != null && d.getMaxFinancingTerm() != null)
                d.setOriginalAmount(d.getMonthlyPayment().multiply(BigDecimal.valueOf(d.getMaxFinancingTerm())));
        });

        List<Debt> existingDebts = debtRepository
                .findAllDebtsByDebtAccountAndActiveTrue(debtAccountCode)
                .stream()
                .peek(d -> { if (d.getHashSum() == null) d.setHashSum(hashService.getHashSum(d, debtAccountCode)); })
                .toList();

        return diffService.computeDiff(debtAccountCode, debtAccount.getAccountStatementType(),
                extractedDebts, existingDebts);
    }
}
