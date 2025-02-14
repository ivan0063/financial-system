package mx.magi.jimm0063.financial.system.debt.web.controller;

import mx.magi.jimm0063.financial.system.debt.application.dto.DebtModel;
import mx.magi.jimm0063.financial.system.debt.application.service.DebtService;
import mx.magi.jimm0063.financial.system.financial.catalog.domain.entity.Debt;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/debt")
public class DebtManagementController {
    private final DebtService debtService;

    public DebtManagementController(DebtService debtService) {
        this.debtService = debtService;
    }

    @GetMapping("/delete/{debtId}")
    public ResponseEntity<DebtModel> deleteDebt(@PathVariable String debtId) {
        return ResponseEntity.ok(debtService.deleteDebt(debtId));
    }
}
