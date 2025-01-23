package mx.magi.jimm0063.financial.system.debt.web.controller;

import mx.magi.jimm0063.financial.system.debt.application.component.AccountStatementFactory;
import mx.magi.jimm0063.financial.system.debt.application.dto.DebtModel;
import mx.magi.jimm0063.financial.system.debt.application.enums.PdfExtractorTypes;
import mx.magi.jimm0063.financial.system.debt.application.service.AccountStatement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/debt/management")
public class DebtManagementController {
    private final AccountStatementFactory accountStatementFactory;

    public DebtManagementController(AccountStatementFactory accountStatementFactory) {
        this.accountStatementFactory = accountStatementFactory;
    }

    @PostMapping("/extranct/debts")
    public ResponseEntity<List<DebtModel>> extractDebtsFromAccountStatement(@RequestParam("file") MultipartFile file,
                                                                      @RequestParam PdfExtractorTypes type) throws Exception {
        if(file.isEmpty()) throw new Exception("There is no valid file in the request");

//        File pdf = new File(Objects.requireNonNull(file.getOriginalFilename()));
//        pdf.createNewFile();
//        FileOutputStream fos = new FileOutputStream(pdf);
//        fos.write(file.getBytes());
//        fos.close();
        byte[] pdf = file.getBytes();
        AccountStatement accountStatement = accountStatementFactory.getStrategy(type);
        List<DebtModel> debtsExtracted = accountStatement.extractDebt(pdf);

        return ResponseEntity.ok(debtsExtracted);
    }
}
