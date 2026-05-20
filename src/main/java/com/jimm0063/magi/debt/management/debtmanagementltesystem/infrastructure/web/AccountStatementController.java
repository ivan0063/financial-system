package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.web;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.DebtDuplicationPreventUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.ExtractFromFileUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.FilterDebtsUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.LoadDebtList;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.enums.AccountStatementType;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.Debt;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/account/statement")
public class AccountStatementController {
    private final ExtractFromFileUseCase extractFromFileUseCase;
    private final FilterDebtsUseCase filterDebtsUseCase;
    private final DebtDuplicationPreventUseCase debtDuplicationPreventUseCase;
    private final LoadDebtList loadDebtList;

    public AccountStatementController(ExtractFromFileUseCase extractFromFileUseCase,
                                      FilterDebtsUseCase filterDebtsUseCase,
                                      DebtDuplicationPreventUseCase debtDuplicationPreventUseCase,
                                      LoadDebtList loadDebtList) {
        this.extractFromFileUseCase = extractFromFileUseCase;
        this.filterDebtsUseCase = filterDebtsUseCase;
        this.debtDuplicationPreventUseCase = debtDuplicationPreventUseCase;
        this.loadDebtList = loadDebtList;
    }

    @PostMapping(path = "/extract/{debtAccountCode}", consumes = "multipart/form-data")
    public ResponseEntity<List<Debt>> extractDebts(@RequestParam("file") MultipartFile accountStatement,
                                                   @PathVariable String debtAccountCode,
                                                   @RequestParam AccountStatementType accountStatementType) throws IOException {
        List<Debt> accountStatementDebts = extractFromFileUseCase.extractDebts(accountStatement, debtAccountCode, accountStatementType)
                .stream().peek(debt -> {
                    String sum = this.debtDuplicationPreventUseCase.getHashSum(debt, debtAccountCode);
                    debt.setHashSum(sum);
                })
                .toList();
        List<Debt> filteredAccountStatementDebts = this.filterDebtsUseCase.filterAccountStatementDebts(accountStatementDebts, debtAccountCode);
        return ResponseEntity.ok(filteredAccountStatementDebts);
    }

    @PostMapping(path = "/sync/{debtAccountCode}", consumes = "multipart/form-data")
    public ResponseEntity<List<Debt>> syncDebts(@RequestParam("file") MultipartFile accountStatement,
                                                @PathVariable String debtAccountCode,
                                                @RequestParam AccountStatementType accountStatementType) throws IOException {
        List<Debt> statementDebts = extractFromFileUseCase.extractDebts(accountStatement, debtAccountCode, accountStatementType)
                .stream()
                .peek(d -> d.setHashSum(this.debtDuplicationPreventUseCase.getHashSum(d, debtAccountCode)))
                .toList();

        this.filterDebtsUseCase.deactivateObsoleteDebts(statementDebts, debtAccountCode);

        List<Debt> saved = this.loadDebtList.saveUnrepeated(statementDebts, debtAccountCode);
        return ResponseEntity.ok(saved);
    }
}
