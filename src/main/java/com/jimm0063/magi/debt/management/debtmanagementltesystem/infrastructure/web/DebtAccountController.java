package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.web;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.DebtAccountStatusUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.FindAllDebtAccountUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.out.DebtAccountRepository;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.dto.DebtAccountStatusDto;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.enums.AccountStatementType;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.DebtAccount;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.mapper.DebtAccountMapper;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model.CreateDebtAccountReq;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/debt/account")
public class DebtAccountController {
    private final DebtAccountStatusUseCase debtAccountStatusUseCase;
    private final DebtAccountRepository debtAccountRepository;
    private final DebtAccountMapper debtAccountMapper;
    private final FindAllDebtAccountUseCase findAllDebtAccountUseCase;

    public DebtAccountController(DebtAccountStatusUseCase debtAccountStatusUseCase,
                                 DebtAccountRepository debtAccountRepository,
                                 DebtAccountMapper debtAccountMapper,
                                 FindAllDebtAccountUseCase findAllDebtAccountUseCase) {
        this.debtAccountStatusUseCase = debtAccountStatusUseCase;
        this.debtAccountRepository = debtAccountRepository;
        this.debtAccountMapper = debtAccountMapper;
        this.findAllDebtAccountUseCase = findAllDebtAccountUseCase;
    }

    @PostMapping("/{financialProviderCode}")
    public ResponseEntity<DebtAccount> createDebtAccount(@RequestBody CreateDebtAccountReq createDebtAccountReq,
                                                          @PathVariable String financialProviderCode) {
        return ResponseEntity.ok(this.debtAccountRepository.save(debtAccountMapper.toModel(createDebtAccountReq), financialProviderCode));
    }

    @PutMapping
    public ResponseEntity<DebtAccount> updateDebtAccount(@RequestBody DebtAccount debtAccount) {
        return ResponseEntity.ok(this.debtAccountRepository.update(debtAccount));
    }

    @PatchMapping("/{code}/statement-type")
    public ResponseEntity<DebtAccount> updateStatementType(@PathVariable String code,
                                                            @RequestParam AccountStatementType accountStatementType) {
        return ResponseEntity.ok(debtAccountRepository.updateStatementType(code, accountStatementType));
    }

    @DeleteMapping("/{debtAccountCode}")
    public ResponseEntity<Void> deleteDebtAccount(@PathVariable("debtAccountCode") String debtAccountCode) {
        debtAccountRepository.delete(debtAccountCode);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/status/{debtAccountCode}")
    public ResponseEntity<DebtAccountStatusDto> debtAccountStatus(@PathVariable("debtAccountCode") String debtAccountCode) {
        return ResponseEntity.ok(debtAccountStatusUseCase.getStatus(debtAccountCode));
    }

    @GetMapping("/all/{financialProviderCode}")
    public ResponseEntity<List<DebtAccount>> findAllDebtAccount(@PathVariable String financialProviderCode) {
        return ResponseEntity.ok(findAllDebtAccountUseCase.getActiveByFinancialProvider(financialProviderCode));
    }
}
