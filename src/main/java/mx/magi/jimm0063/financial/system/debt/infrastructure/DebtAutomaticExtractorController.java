package mx.magi.jimm0063.financial.system.debt.infrastructure;

import mx.magi.jimm0063.financial.system.debt.application.component.AccountStatementFactory;
import mx.magi.jimm0063.financial.system.debt.application.service.AccountStatementService;
import mx.magi.jimm0063.financial.system.debt.application.service.DataBaseLoaderService;
import mx.magi.jimm0063.financial.system.debt.domain.dto.DebtModel;
import mx.magi.jimm0063.financial.system.debt.domain.dto.ManualDebtRequest;
import mx.magi.jimm0063.financial.system.debt.domain.enums.PdfExtractorTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/debt/management")
public class DebtAutomaticExtractorController {
    private final AccountStatementFactory accountStatementFactory;
    private final DataBaseLoaderService dataBaseLoaderService;

    public DebtAutomaticExtractorController(AccountStatementFactory accountStatementFactory, DataBaseLoaderService dataBaseLoaderService) {
        this.accountStatementFactory = accountStatementFactory;
        this.dataBaseLoaderService = dataBaseLoaderService;
    }

    @PostMapping("/extranct/debts")
    public ResponseEntity<List<DebtModel>> extractDebtsFromAccountStatement(@RequestParam("file") MultipartFile file,
                                                                      @RequestParam PdfExtractorTypes type) throws Exception {
        if(file.isEmpty()) throw new RuntimeException("There is no valid file in the request");

        byte[] pdf = file.getBytes();
        AccountStatementService accountStatementService = accountStatementFactory.getStrategy(type);
        List<DebtModel> debtsExtracted = accountStatementService.extractDebt(pdf);

        return ResponseEntity.ok(debtsExtracted);
    }

    @PostMapping("/load/debts/{cardCode}")
    public ResponseEntity<List<DebtModel>> loadDebts(@RequestParam("file") MultipartFile accountStatement,
                                                     @PathVariable String cardCode) throws IOException {
        if(accountStatement.isEmpty()) throw new RuntimeException("There is no valid file in the request");
        byte[] accountStatementFile = accountStatement.getBytes();
        List<DebtModel> loadedDebts = this.dataBaseLoaderService.loadDebtFromAccountStatement(accountStatementFile, cardCode);

        return ResponseEntity.ok(loadedDebts);
    }

    @PostMapping("/load/manual/debts/{cardCode}")
    public ResponseEntity<List<DebtModel>> loadDebts(@RequestBody ManualDebtRequest manualDebts,
                                                     @PathVariable String cardCode) throws IOException {
        List<DebtModel> loadedDebts = this.dataBaseLoaderService.loadDebts(manualDebts.getDebts(), cardCode);
        return ResponseEntity.ok(loadedDebts);
    }

    @PostMapping("/load/report/debts")
    public ResponseEntity<?> loadDebtsFromXML(@RequestParam("file") MultipartFile debtsFile) {
        if(debtsFile.isEmpty()) throw new RuntimeException("There is no valid file in the request");

        return ResponseEntity.ok(this.dataBaseLoaderService.importDebtsFromCSV(debtsFile));
    }
}
