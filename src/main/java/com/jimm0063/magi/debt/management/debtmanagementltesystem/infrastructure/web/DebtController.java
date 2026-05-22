package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.web;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.DebtDuplicationPreventUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.FindAllDebtsUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.out.DebtRepository;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.Debt;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.mapper.DebtMapper;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model.CreateDebtReq;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/debt")
public class DebtController {
    private final DebtRepository debtRepository;
    private final DebtMapper debtMapper;
    private final FindAllDebtsUseCase findAllDebtsUseCase;
    private final DebtDuplicationPreventUseCase debtDuplicationPreventUseCase;

    public DebtController(DebtRepository debtRepository, DebtMapper debtMapper, FindAllDebtsUseCase findAllDebtsUseCase, DebtDuplicationPreventUseCase debtDuplicationPreventUseCase) {
        this.debtRepository = debtRepository;
        this.debtMapper = debtMapper;
        this.findAllDebtsUseCase = findAllDebtsUseCase;
        this.debtDuplicationPreventUseCase = debtDuplicationPreventUseCase;
    }

    @PostMapping("/{debtAccountCode}")
    public ResponseEntity<Debt> createDebt(@RequestBody CreateDebtReq createDebtReq, @PathVariable String debtAccountCode) {
        return ResponseEntity.ok(debtRepository.save(debtMapper.toModel(createDebtReq), debtAccountCode));
    }

    @PutMapping
    public ResponseEntity<Debt> updateDebt(@RequestBody Debt debt) {
        return ResponseEntity.ok(debtRepository.update(debt));
    }

    @DeleteMapping("/{debtId}")
    public ResponseEntity<Debt> updateDebt(@PathVariable Integer debtId) {
        debtRepository.delete(debtId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all/{debtAccountCode}")
    public ResponseEntity<List<Debt>> getAllActiveDebtsByDebtAccount(@PathVariable String debtAccountCode) {
        return ResponseEntity.ok(findAllDebtsUseCase.getActiveByDebtAccount(debtAccountCode));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Debt>> getActiveDebts(@RequestHeader String email) {
        return ResponseEntity.ok(this.debtRepository.findAllDebtsByUser(email));
    }

    @GetMapping("/set/hash/sum")
    public ResponseEntity<List<Debt>> setHashSum(@RequestHeader String email) {
        List<Debt> debts = this.debtRepository.findAllDebtsByUser(email);

        debts.forEach(debt -> {
                    if(Optional.ofNullable(debt.getHashSum()).isPresent()) return;

                    debt.setHashSum(debtDuplicationPreventUseCase.getHashSum(debt, debt.getDebtAccount().getCode()));
                });
        debts = this.debtRepository.saveAll(debts);
        return ResponseEntity.ok(debts);
    }
}
