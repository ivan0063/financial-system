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
            //String text = new String(stripper.getText(document).getBytes("ISO-8859-1"), "UTF-8");

            // Regex para extraer: Fecha, Descripción, Monto Original, Pago Requerido, Núm. de Pago
//            Pattern pattern = Pattern.compile(
//                    "(\\d{2}-[A-Za-z]{3}-\\d{4})\\s+" + // Fecha (ej: 09-nov-2023)
//                            "(.+?)\\s+" + // Descripción (hasta encontrar el monto)
//                            "\\$(\\d{1,3}(?:,\\d{3})*\\.\\d{2})\\s+" + // Monto original (ej: $4,041.00)
//                            "\\$(\\d{1,3}(?:,\\d{3})*\\.\\d{2})\\s+" + // Saldo pendiente (ignorado)
//                            "\\$(\\d{1,3}(?:,\\d{3})*\\.\\d{2})\\s+" + // Pago requerido (ej: $225.00)
//                            "(\\d+\\s+de\\s+\\d+)" // Núm. de pago (ej: 15 de 18)
//            );
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
                        DebtModel debt = new DebtModel();
                        debt.setName(matcher.group(2).trim());
                        debt.setInitialDebtAmount(Double.parseDouble(matcher.group(3).replace(",", "")));
                        debt.setMonthAmount(Double.parseDouble(matcher.group(5).replace(",", "")));
                        debt.setMonthsPaid(Integer.parseInt(matcher.group(6)));
                        debt.setMonthsFinanced(Integer.parseInt(matcher.group(7)));
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