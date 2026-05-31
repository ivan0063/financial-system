package com.jimm0063.magi.debt.management.debtmanagementltesystem.application.service;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.AccountStatementDataExtractionUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.enums.DebtTypeEnum;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.Debt;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.DebtAccount;
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
 * Parses Liverpool Visa and Liverpool Departmental credit card statements.
 *
 * Both statement types share the same layout. Installments appear in a table
 * whose rows have the plan name "XX MENS S/INTERESES" (e.g. "13 MENS S/INTERESES").
 * Because the PDFs are owner-restricted (text extraction blocked), text is obtained
 * via OCR through {@link PdfTextExtractorService}.
 *
 * Example row (after OCR):
 *   21-SEP 003 13 MENS S/INTERESES 8 2,248.87 0.00 321.25 1,927.62
 *   date   seg totalMonths          curr monthly saldoAnt cargos  saldoCorte
 */
@Service("LIVERPOOL")
public class LiverpoolAccountStatementService implements AccountStatementDataExtractionUseCase {

    // "FECHA DE CORTE 15-MAY-2026"
    private static final Pattern CUTOFF_PATTERN = Pattern.compile(
            "FECHA\\s+DE\\s+CORTE\\s+(\\d{1,2})[\\-\\.](\\w{3})[\\-\\.](\\d{4})",
            Pattern.CASE_INSENSITIVE
    );

    // DD-MMM  SEG  TOTAL_MONTHS MENS S[/]?INTERESES  CURR  MONTHLY  SALDO_ANT  CARGOS  SALDO_CORTE
    // Tolerates OCR artifacts: missing commas/dots in amounts, "S/INTERESES" or "SINTERESES"
    private static final Pattern MSI_ROW = Pattern.compile(
            "^(\\d{1,2})[\\-\\.](\\w{3})\\s+" +           // date DD-MMM
            "\\d{3}\\s+" +                                  // segmento (ignored)
            "(\\d{2})\\s+MENS\\s+S[/]?INTERESES\\s+" +     // total months
            "(\\d+)\\s+" +                                  // current installment
            "([\\d,\\.]+)\\s+" +                           // monthly payment (OCR may garble decimals)
            "([\\d,\\.]+)\\s+" +                           // saldo anterior
            "([\\d,\\.]+)\\s+" +                           // cargos del periodo / pago mínimo
            "([\\d,\\.]+)\\s*$",                           // saldo al corte
            Pattern.CASE_INSENSITIVE
    );

    private final PdfTextExtractorService pdfExtractor;

    public LiverpoolAccountStatementService(PdfTextExtractorService pdfExtractor) {
        this.pdfExtractor = pdfExtractor;
    }

    @Override
    public List<Debt> extractDebts(MultipartFile accountStatement, DebtAccount debtAccount) {
        try {
            String text = pdfExtractor.extractText(accountStatement.getBytes());
            String cutoffDate = extractCutoffDate(text);
            return parseMsiRows(text, cutoffDate, debtAccount);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse Liverpool statement", e);
        }
    }

    private String extractCutoffDate(String text) {
        Matcher m = CUTOFF_PATTERN.matcher(text);
        if (!m.find()) return LocalDate.now().toString();
        try {
            int day = Integer.parseInt(m.group(1));
            int month = spanishAbbrevToMonth(m.group(2));
            int year = Integer.parseInt(m.group(3));
            return LocalDate.of(year, month, day).toString();
        } catch (Exception e) {
            return LocalDate.now().toString();
        }
    }

    private List<Debt> parseMsiRows(String text, String cutoffDate, DebtAccount debtAccount) {
        List<Debt> debts = new ArrayList<>();
        for (String raw : text.split("\\R")) {
            String line = raw.trim();
            Matcher m = MSI_ROW.matcher(line);
            if (!m.matches()) continue;

            int totalMonths = Integer.parseInt(m.group(3));
            int currentInstallment = Integer.parseInt(m.group(4));
            BigDecimal monthly = parseMoney(m.group(5));
            BigDecimal saldoCorte = parseMoney(m.group(8));

            Debt debt = new Debt();
            debt.setActive(true);
            debt.setOperationDate(cutoffDate);
            debt.setDescription(buildDescription(m.group(1), m.group(2), totalMonths));
            debt.setMaxFinancingTerm(totalMonths);
            debt.setCurrentInstallment(currentInstallment);
            debt.setMonthlyPayment(monthly);
            // originalAmount not present in Liverpool statements; use saldo al corte as proxy
            debt.setOriginalAmount(saldoCorte);
            debt.setDebtType(DebtTypeEnum.CARD);
            debt.setDebtAccount(debtAccount);
            debts.add(debt);
        }
        return debts;
    }

    private String buildDescription(String day, String month, int totalMonths) {
        return String.format("LIVERPOOL MSI | %s-%s | %d MENS", day, month.toUpperCase(Locale.ROOT), totalMonths);
    }

    /**
     * Parses a money string that may have OCR artifacts (missing decimal point or comma).
     * Tries standard parsing first; if that fails, inserts a decimal two places from the end.
     */
    private static BigDecimal parseMoney(String raw) {
        String clean = raw.replace(",", "");
        try {
            return new BigDecimal(clean);
        } catch (NumberFormatException e) {
            // OCR dropped decimal point: e.g. "224887" → "2248.87"
            if (clean.length() > 2) {
                String fixed = clean.substring(0, clean.length() - 2) + "." + clean.substring(clean.length() - 2);
                return new BigDecimal(fixed);
            }
            return BigDecimal.ZERO;
        }
    }

    private static int spanishAbbrevToMonth(String abbrev) {
        return switch (abbrev.toLowerCase(Locale.ROOT).replace(".", "").trim()) {
            case "ene", "jan" -> 1;
            case "feb"        -> 2;
            case "mar"        -> 3;
            case "abr", "apr" -> 4;
            case "may"        -> 5;
            case "jun"        -> 6;
            case "jul"        -> 7;
            case "ago", "aug" -> 8;
            case "sep"        -> 9;
            case "oct"        -> 10;
            case "nov"        -> 11;
            case "dic", "dec" -> 12;
            default -> throw new IllegalArgumentException("Unknown month abbreviation: " + abbrev);
        };
    }
}
