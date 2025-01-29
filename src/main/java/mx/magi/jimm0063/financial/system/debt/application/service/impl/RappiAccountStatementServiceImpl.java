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

@Service("RAPPI")
public class RappiAccountStatementServiceImpl implements AccountStatementService {
    @Override
    public List<DebtModel> extractDebt(byte[] pdfFile) throws IOException {
        List<DebtModel> debtModels = new ArrayList<>();

        try (PDDocument document = Loader.loadPDF(new RandomAccessReadBuffer(pdfFile))) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);

            // Locate the "Movimientos diferidos" table
            String[] lines = text.split("\n");
            boolean isDebtSection = false;

            Pattern linePattern = Pattern.compile(
                    "^(\\d{4}-\\d{2}-\\d{2})\\s+(.+?)\\s+\\$\\s*([\\d,\\.]+)\\s+\\$\\s*([\\d,\\.]+)\\s+\\$\\s*([\\d,\\.]+)\\s+(\\d+)\\s+de\\s+(\\d+)\\s+\\$\\s*([\\d,\\.]+)$"
            );

            for (String line : lines) {
                if (line.contains("Movimientos diferidos")) {
                    isDebtSection = true;
                    continue;
                }

                if (isDebtSection) {
                    if (line.trim().isEmpty() || line.startsWith("Subtotal")) {
                        break;
                    }

                    // Match the line with the pattern
                    Matcher matcher = linePattern.matcher(line);
                    if (matcher.matches()) {
//                        DebtModel debtModel = new DebtModel();
//                        debtModel.setName(matcher.group(2).trim());
//                        debtModel.setInitialDebtAmount(Double.parseDouble(matcher.group(3).replace(",", "")));
//                        debtModel.setDebtPaid(Double.parseDouble(matcher.group(4).replace(",", "")));
//                        debtModel.setMonthsPaid(Integer.parseInt(matcher.group(6)));
//                        debtModel.setMonthsFinanced(Integer.parseInt(matcher.group(7)));
//                        debtModel.setMonthAmount(Double.parseDouble(matcher.group(8).replace(",", "")));

                        debtModels.add(DebtModel.builder()
                                        .name(matcher.group(2).trim())
                                        .initialDebtAmount(Double.parseDouble(matcher.group(3).replace(",", "")))
                                        .monthAmount(Double.parseDouble(matcher.group(8).replace(",", "")))
                                        .monthsFinanced(Integer.parseInt(matcher.group(7)))
                                        .monthsPaid(Integer.parseInt(matcher.group(6)))
                                        .debtPaid(Double.parseDouble(matcher.group(4).replace(",", "")))
                                        .build());
                    }
                }
            }
        }

        return debtModels;
    }
}