package mx.magi.jimm0063.financial.system.financial.catalog.infrastructure;

import mx.magi.jimm0063.financial.system.financial.catalog.application.service.DebtAssociatedEntitiesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/catalog/debt/entity")
public class DebtEntityController {
    private final DebtAssociatedEntitiesService debtAssociatedEntitiesService;

    public DebtEntityController(DebtAssociatedEntitiesService debtAssociatedEntitiesService) {
        this.debtAssociatedEntitiesService = debtAssociatedEntitiesService;
    }

    @GetMapping("/")
    public ResponseEntity<?> getDebtAssociatedEntities() {
        return ResponseEntity.ok(this.debtAssociatedEntitiesService.getAssociatedEntities());
    }
}
