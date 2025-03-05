package mx.magi.jimm0063.financial.system.status.infrastructure;

import mx.magi.jimm0063.financial.system.status.application.service.DebtInformationService;
import mx.magi.jimm0063.financial.system.status.domain.CardDebtStatus;
import mx.magi.jimm0063.financial.system.status.domain.GlobalDebtStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/debt/information")
public class DebtInformationController {
    private DebtInformationService debtInformationService;

    public DebtInformationController(DebtInformationService debtInformationService) {
        this.debtInformationService = debtInformationService;
    }

    @GetMapping("/entity/statement/{entityStatementId}")
    public ResponseEntity<CardDebtStatus> getPaymentEntityStatement(@PathVariable String entityStatementId) {
        return ResponseEntity.ok(debtInformationService.debtEntityStatus(entityStatementId));
    }

    @GetMapping("/financial/statement")
    public ResponseEntity<GlobalDebtStatus> getFinancialStatement(@RequestParam String email) {
        return ResponseEntity.ok(debtInformationService.debtStatus(email));
    }
}
