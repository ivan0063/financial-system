package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.web;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.DebtDuplicationPreventUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.ExtractFromFileUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.FilterDebtsUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.LoadDebtList;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.dto.AccountStatementPreviewDto;
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

    /**
     * Extracts debts from an account statement file and returns a categorized preview.
     * <p>
     * This endpoint is read-only — no data is persisted. It is intended as the first
     * step before {@code /sync}, allowing the client to display a diff to the user.
     *
     * @param accountStatement     the uploaded account statement file (multipart)
     * @param debtAccountCode      the code identifying the debt account to compare against
     * @param accountStatementType the format/type of the uploaded statement
     * @return a preview containing:
     *         <ul>
     *           <li>{@code newDebts} — debts present in the statement but not yet in the database</li>
     *           <li>{@code installmentUpdates} — debts already in the database whose current
     *               installment has advanced in the statement, with previous and new installment values</li>
     *         </ul>
     * @throws IOException if the uploaded file cannot be read
     */
    @PostMapping(path = "/extract/{debtAccountCode}", consumes = "multipart/form-data")
    public ResponseEntity<AccountStatementPreviewDto> extractDebts(
            @RequestParam("file") MultipartFile accountStatement,
            @PathVariable String debtAccountCode,
            @RequestParam AccountStatementType accountStatementType) throws IOException {
        List<Debt> accountStatementDebts = extractFromFileUseCase.extractDebts(accountStatement, debtAccountCode, accountStatementType)
                .stream().peek(debt -> {
                    String sum = this.debtDuplicationPreventUseCase.getHashSum(debt, debtAccountCode);
                    debt.setHashSum(sum);
                })
                .toList();
        AccountStatementPreviewDto preview = this.filterDebtsUseCase.previewAccountStatement(accountStatementDebts, debtAccountCode);
        return ResponseEntity.ok(preview);
    }

    /**
     * Syncs an account statement file with the database.
     * <p>
     * This endpoint persists changes derived from the statement:
     * <ol>
     *   <li>Deactivates debts currently active in the database that are no longer present
     *       in the statement (i.e., fully paid off or removed)</li>
     *   <li>Saves new debts and updates the {@code currentInstallment} of existing debts
     *       that have advanced since the last sync</li>
     * </ol>
     *
     * @param accountStatement     the uploaded account statement file (multipart)
     * @param debtAccountCode      the code identifying the debt account to sync
     * @param accountStatementType the format/type of the uploaded statement
     * @return the list of debts that were newly saved or updated in this sync
     * @throws IOException if the uploaded file cannot be read
     */
    @PostMapping(path = "/sync/{debtAccountCode}", consumes = "multipart/form-data")
    public ResponseEntity<List<Debt>> syncDebts(
            @RequestParam("file") MultipartFile accountStatement,
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
