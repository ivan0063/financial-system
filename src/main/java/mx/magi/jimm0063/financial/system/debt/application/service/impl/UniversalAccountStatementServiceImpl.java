package mx.magi.jimm0063.financial.system.debt.application.service.impl;

import mx.magi.jimm0063.financial.system.debt.application.dto.DebtModel;
import mx.magi.jimm0063.financial.system.debt.application.service.AccountStatementService;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("UNIVERSAL")
public class UniversalAccountStatementServiceImpl implements AccountStatementService {
    @Override
    public List<DebtModel> extractDebt(byte[] pdfFile) {
        List<DebtModel> debts = new ArrayList<>();
        try (PDDocument document = Loader.loadPDF(new RandomAccessReadBuffer(pdfFile))) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            Pattern pattern = Pattern.compile(
                    "(\\d{2}-[a-zA-Z]{3}-\\d{4})\\s+" +       // Date (not used directly)
                            "(.+?)\\s+\\$" +                         // Description (name)
                            "([\\d,]+\\.\\d{2})\\s+\\$" +            // Original amount
                            "([\\d,]+\\.\\d{2})\\s+\\$" +            // Pending balance (not used directly)
                            "([\\d,]+\\.\\d{2})\\s+" +               // Monthly payment
                            "(\\d+)\\s+de\\s+(\\d+)"                 // Payment progress (X de Y)
            );

            String[] lines = text.split("\\r?\\n");
            boolean startParsing = false;

            for (String line : lines) {
                if (line.contains("COMPRAS Y CARGOS DIFERIDOS A MESES SIN INTERESES")) {
                    startParsing = true;
                    continue;
                }

                if (startParsing && (line.contains("---") || line.contains("CARGOS,COMPRAS Y ABONOS REGULARES"))) {
                    break;
                }

                if (startParsing) {
                    Matcher matcher = pattern.matcher(line.trim());
                    if (matcher.find()) {

                        DebtModel debt = DebtModel.builder()
                                .initialDebtAmount(Double.parseDouble(matcher.group(3).replace(",", "")))
                                .monthAmount(Double.parseDouble(matcher.group(5).replace(",", "")))
                                .monthsFinanced(Integer.parseInt(matcher.group(7)))
                                .monthsPaid(Integer.parseInt(matcher.group(6)))
                                .name(matcher.group(2).trim())
                                .build();
                        debt.setDebtPaid(debt.getMonthAmount() * debt.getMonthsPaid());

                        debts.add(debt);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return debts;
    }
}