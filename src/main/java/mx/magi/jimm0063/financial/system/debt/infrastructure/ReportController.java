package mx.magi.jimm0063.financial.system.debt.infrastructure;

import mx.magi.jimm0063.financial.system.debt.application.service.impl.DefaultDebtReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/debt/report")
public class ReportController {
    private final DefaultDebtReportService debtReportService;

    public ReportController(DefaultDebtReportService debtReportService) {
        this.debtReportService = debtReportService;
    }

    @GetMapping("/{email}")
    public ResponseEntity<byte[]> getReport(@PathVariable String email) throws IOException {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=debt_payments.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(debtReportService.generateDebtReport(email));
    }
}
