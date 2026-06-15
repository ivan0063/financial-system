package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.web;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.GetIgnorableDebtsUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.MarkDebtAsIgnorableUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.RemoveIgnorableDebtUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.IgnorableDebt;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model.MarkDebtIgnorableReq;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/debt/ignorable")
public class IgnorableDebtController {
    private final MarkDebtAsIgnorableUseCase markDebtAsIgnorableUseCase;
    private final RemoveIgnorableDebtUseCase removeIgnorableDebtUseCase;
    private final GetIgnorableDebtsUseCase getIgnorableDebtsUseCase;

    public IgnorableDebtController(MarkDebtAsIgnorableUseCase markDebtAsIgnorableUseCase,
                                   RemoveIgnorableDebtUseCase removeIgnorableDebtUseCase,
                                   GetIgnorableDebtsUseCase getIgnorableDebtsUseCase) {
        this.markDebtAsIgnorableUseCase = markDebtAsIgnorableUseCase;
        this.removeIgnorableDebtUseCase = removeIgnorableDebtUseCase;
        this.getIgnorableDebtsUseCase = getIgnorableDebtsUseCase;
    }

    @PostMapping
    public ResponseEntity<IgnorableDebt> markAsIgnorable(@RequestBody MarkDebtIgnorableReq req) {
        return ResponseEntity.ok(markDebtAsIgnorableUseCase.markAsIgnorable(req.getHashSum(), req.getReason()));
    }

    @DeleteMapping("/{hashSum}")
    public ResponseEntity<Void> removeIgnore(@PathVariable String hashSum) {
        removeIgnorableDebtUseCase.removeIgnore(hashSum);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<IgnorableDebt>> getAllIgnorableDebts() {
        return ResponseEntity.ok(getIgnorableDebtsUseCase.getAllIgnorableDebts());
    }
}
