package com.jimm0063.magi.debt.management.debtmanagementltesystem.application.service;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.AccountStatementDataExtractionUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.enums.DebtTypeEnum;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.Debt;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.DebtAccount;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses Mercado Pago credit card statements (2025+ format).
 *
 * Installment data lives in the "Saldo a meses con o sin intereses" section:
 *   Movimiento         Monto total  Tasa anual  Mes       Monto mensual  Saldo restante
 *   MERCADO PAGO 1     $2,244.40    0%          11 de 18  $124.68        $872.76
 *
 * The cutoff date from the page header is used as the operation date for all rows.
 */
@Service("MERCADO_PAGO")
public class MercadoPagoAccountStatementService implements AccountStatementDataExtractionUseCase {

    private static final String SECTION_START = "Saldo a meses con o sin intereses";
    private static final String SECTION_END   = "Saldo a meses de este periodo";

    // description  $total  rate%  N de M  $monthly  $remaining
    private static final Pattern ROW_PATTERN = Pattern.compile(
            "(.+?)\\s+\\$\\s*" +
                    "([\\d,]+\\.\\d{2})\\s+" +
                    "\\d+%\\s+" +
                    "(\\d+)\\s+de\\s+(\\d+)\\s+" +
                    "\\$\\s*([\\d,]+\\.\\d{2})\\s+" +
                    "\\$\\s*[\\d,]+\\.\\d{2}"
    );

    // "Fecha de corte\n21 abril" — may span two lines after text extraction
    private static final Pattern CUTOFF_PATTERN = Pattern.compile(
            "Fecha\\s+de\\s+corte\\s+(\\d{1,2})\\s+([a-záéíóúñ]+)",
            Pattern.CASE_INSENSITIVE
    );

    @Override
    public List<Debt> extractDebts(MultipartFile accountStatement, DebtAccount debtAccount) {
        List<Debt> debts = new ArrayList<>();
        try (PDDocument document = Loader.loadPDF(accountStatement.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            String text = stripper.getText(document);

            String operationDate = extractCutoffDate(text);

            boolean inSection = false;
            for (String rawLine : text.split("\\r?\\n")) {
                String line = rawLine.trim();

                if (line.contains(SECTION_START)) {
                    inSection = true;
                    continue;
                }

                if (inSection && line.contains(SECTION_END)) break;

                if (!inSection || line.isBlank()) continue;

                Matcher m = ROW_PATTERN.matcher(line);
                if (!m.find()) continue;

                Debt debt = new Debt();
                debt.setDescription(m.group(1).trim());
                debt.setOriginalAmount(new BigDecimal(m.group(2).replace(",", "")));
                debt.setCurrentInstallment(Integer.parseInt(m.group(3)));
                debt.setMaxFinancingTerm(Integer.parseInt(m.group(4)));
                debt.setMonthlyPayment(new BigDecimal(m.group(5).replace(",", "")));
                debt.setOperationDate(operationDate);
                debt.setActive(true);
                debt.setDebtType(DebtTypeEnum.CARD);
                debt.setDebtAccount(debtAccount);
                debts.add(debt);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to extract Mercado Pago debts from PDF", e);
        }
        return debts;
    }

    private String extractCutoffDate(String text) {
        Matcher m = CUTOFF_PATTERN.matcher(text);
        if (!m.find()) return LocalDate.now().toString();
        try {
            int day = Integer.parseInt(m.group(1));
            int month = spanishMonthToNumber(m.group(2));
            // Statements don't include a year in the cutoff line; infer from context
            int year = LocalDate.now().getYear();
            return LocalDate.of(year, month, day).toString();
        } catch (Exception e) {
            return LocalDate.now().toString();
        }
    }

    private static int spanishMonthToNumber(String mon) {
        return switch (mon.toLowerCase(Locale.ROOT).replace(".", "").trim()) {
            case "enero"                       -> 1;
            case "febrero"                     -> 2;
            case "marzo"                       -> 3;
            case "abril"                       -> 4;
            case "mayo"                        -> 5;
            case "junio"                       -> 6;
            case "julio"                       -> 7;
            case "agosto"                      -> 8;
            case "septiembre", "setiembre"     -> 9;
            case "octubre"                     -> 10;
            case "noviembre"                   -> 11;
            case "diciembre"                   -> 12;
            default -> throw new IllegalArgumentException("Unknown Spanish month: " + mon);
        };
    }
}
