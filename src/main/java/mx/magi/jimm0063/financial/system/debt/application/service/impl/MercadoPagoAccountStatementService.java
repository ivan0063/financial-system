package mx.magi.jimm0063.financial.system.debt.application.service.impl;

import mx.magi.jimm0063.financial.system.debt.application.service.AccountStatementService;
import mx.magi.jimm0063.financial.system.debt.domain.dto.DebtModel;
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

@Service("MERCADO_PAGO")
public class MercadoPagoAccountStatementService implements AccountStatementService {
    @Override
    public List<DebtModel> extractDebt(byte[] pdfFile) throws IOException {
        List<DebtModel> debtModels = new ArrayList<>();

        try (PDDocument document = Loader.loadPDF(new RandomAccessReadBuffer(pdfFile))) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);

            // Locate the "Saldo a meses con o sin intereses" table
            String[] lines = text.split("\n");
            boolean isDebtSection = false;

            // Regex to match the rows in the "Saldo a meses con o sin intereses" table
            // Updated to match the exact format of the table rows
            Pattern linePattern = Pattern.compile(
                    "(.+?)\\s+\\$\\s*([\\d,\\.]+)\\s+0%\\s+(\\d+)\\s+de\\s+(\\d+)\\s+\\$\\s*([\\d,\\.]+)\\s+\\$\\s*([\\d,\\.]+)"
            );

            for (String line : lines) {
                // Start processing when the "Saldo a meses con o sin intereses" section is found
                if (line.contains("Movimiento Monto total Tasa anual Mes Monto mensual Saldo restante")) {
                    isDebtSection = true;
                    continue;
                }

                if (isDebtSection) {
                    // Stop processing when an empty line or "Subtotal" is encountered
                    if (line.trim().isEmpty() || line.contains("Saldo a meses del periodo actual")) {
                        break;
                    }

                    // Match the line with the pattern
                    Matcher matcher = linePattern.matcher(line);
                    if (matcher.matches()) {
                        // Extract data and map it to the DebtModel class
                        debtModels.add(DebtModel.builder()
                                .name(matcher.group(1).trim()) // Movimiento (dynamic value)
                                .initialDebtAmount(parseDouble(matcher.group(2))) // Monto total
                                .monthAmount(parseDouble(matcher.group(5))) // Monto mensual
                                .monthsFinanced(Integer.parseInt(matcher.group(4))) // Y in "X de Y"
                                .monthsPaid(Integer.parseInt(matcher.group(3))) // X in "X de Y"
                                .debtPaid(parseDouble(matcher.group(6))) // Saldo restante
                                .build());
                    }
                }
            }
        }

        return debtModels;
    }

    // Helper method to parse double values with commas
    private Double parseDouble(String value) {
        return Double.parseDouble(value.replace(",", ""));
    }
}
