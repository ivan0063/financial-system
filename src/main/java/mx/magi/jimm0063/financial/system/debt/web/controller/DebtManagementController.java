package mx.magi.jimm0063.financial.system.debt.web.controller;

import mx.magi.jimm0063.financial.system.debt.application.dto.DebtModel;
import mx.magi.jimm0063.financial.system.debt.application.service.DebtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
