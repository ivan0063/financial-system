package com.jimm0063.magi.debt.management.debtmanagementltesystem.application.service;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.AccountStatementDataExtractionUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.enums.DebtTypeEnum;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.Debt;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.DebtAccount;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.PalacioMsiRowModel;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("PALACIO")
public class PalacioAccountStatementService implements AccountStatementDataExtractionUseCase {

    // Example:
    // FECHA DE CORTE 12.noviembre.2025
    private static final Pattern CUTOFF_PATTERN = Pattern.compile(
            "FECHA\\s+DE\\s+CORTE\\s+(?<dd>\\d{1,2})\\.(?<mon>[a-záéíóúñ]+)\\.(?<yyyy>\\d{4})",
            Pattern.CASE_INSENSITIVE
    );

    private static final String SUMMARY_START = "Resumen de Mensualidades";
    private static final String MSI_SECTION = "Mensualidades sin Intereses";

    // Example row:
    // oct.-ene. Plazo 12 S/Int 3 gracia $7,178.80 $5,384.08 10 de 12 $1,794.68
    // oct.-abr. Plazo 6 meses sin intereses $909.00 $757.50 2 de 6 $151.50
    //
    // Captures:
    // period, planText, saldoPendiente, n, m, monthlyPayment
    private static final Pattern MSI_ROW = Pattern.compile(
            "^(?<period>[a-z]{3}\\.-[a-z]{3}\\.)\\s+" +
                    "(?<plan>Plazo\\s+\\d+.+?)\\s+" +
                    "\\$(?<saldoAnterior>[\\d,]+\\.\\d{2})\\s+" +
                    "\\$(?<saldoPend>[\\d,]+\\.\\d{2})\\s+" +
                    "(?<n>\\d+)\\s+de\\s+(?<m>\\d+)\\s+" +
                    "\\$(?<pay>[\\d,]+\\.\\d{2})\\s*$",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern TOTAL_LINE = Pattern.compile("^Total:\\s+\\$.*$", Pattern.CASE_INSENSITIVE);

    @Override
    public List<Debt> extractDebts(MultipartFile accountStatement, DebtAccount debtAccount) {
        try {
            byte[] bytes = accountStatement.getBytes();
            String text = extractText(bytes);

            LocalDate cutoff = extractCutoffDate(text).orElse(null);

            List<String> msiLines = extractMsiLines(text);
            List<PalacioMsiRowModel> rows = parseMsiLines(msiLines);

            List<Debt> debts = new ArrayList<>(rows.size());
            for (PalacioMsiRowModel r : rows) {
                debts.add(mapToDebt(r, cutoff, debtAccount));
            }
            return debts;

        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to extract debts from PALACIO statement PDF", e);
        }
    }

    private String extractText(byte[] pdfBytes) throws Exception {
        try (PDDocument doc = Loader.loadPDF(pdfBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            return stripper.getText(doc);
        }
    }

    private Optional<LocalDate> extractCutoffDate(String text) {
        Matcher m = CUTOFF_PATTERN.matcher(text);
        if (!m.find()) return Optional.empty();

        int dd = Integer.parseInt(m.group("dd"));
        int yyyy = Integer.parseInt(m.group("yyyy"));
        int mon = mxMonthToNumberFull(m.group("mon"));

        return Optional.of(LocalDate.of(yyyy, mon, dd));
    }

    private List<String> extractMsiLines(String text) {
        List<String> lines = Arrays.asList(text.split("\\R"));
        List<String> out = new ArrayList<>();

        boolean inSummary = false;
        boolean inMsi = false;

        for (String raw : lines) {
            String line = normalizeSpaces(raw);
            if (line.isBlank()) continue;

            if (!inSummary) {
                if (line.equalsIgnoreCase(SUMMARY_START)) {
                    inSummary = true;
                }
                continue;
            }

            if (!inMsi) {
                if (line.equalsIgnoreCase(MSI_SECTION)) {
                    inMsi = true;
                }
                continue;
            }

            if (TOTAL_LINE.matcher(line).matches()) break;

            // Skip header line if present
            if (line.toLowerCase(Locale.ROOT).contains("plan de financiamiento")
                    && line.toLowerCase(Locale.ROOT).contains("saldo")) {
                continue;
            }

            out.add(line);
        }

        return out;
    }

    private List<PalacioMsiRowModel> parseMsiLines(List<String> lines) {
        List<PalacioMsiRowModel> result = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();

        for (String line : lines) {
            if (buffer.length() > 0) buffer.append(' ');
            buffer.append(line);

            String candidate = buffer.toString().trim();
            Matcher m = MSI_ROW.matcher(candidate);
            if (m.matches()) {
                result.add(toRow(m));
                buffer.setLength(0);
            } else if (candidate.length() > 700) {
                buffer.setLength(0);
            }
        }
        return result;
    }

    private PalacioMsiRowModel toRow(Matcher m) {
        String period = normalizeSpaces(m.group("period"));
        String plan = normalizeSpaces(m.group("plan"));
        BigDecimal saldoPend = money(m.group("saldoPend"));

        int n = Integer.parseInt(m.group("n"));
        int total = Integer.parseInt(m.group("m"));

        BigDecimal pay = money(m.group("pay"));

        // Extract "Plazo X" months from plan text
        Integer planMonths = extractPlanMonths(plan).orElse(total);

        return new PalacioMsiRowModel(period, plan, planMonths, n, total, pay, saldoPend);
    }

    private Optional<Integer> extractPlanMonths(String planText) {
        Matcher mm = Pattern.compile("Plazo\\s+(\\d+)", Pattern.CASE_INSENSITIVE).matcher(planText);
        if (!mm.find()) return Optional.empty();
        return Optional.of(Integer.parseInt(mm.group(1)));
    }

    private Debt mapToDebt(PalacioMsiRowModel r, LocalDate cutoff, DebtAccount debtAccount) {
        Debt d = new Debt();
        d.setActive(true);

        // v1 schema uses varchar operation_date: use cutoff ISO for traceability in tests
        d.setOperationDate(cutoff != null ? cutoff.toString() : null);

        d.setDescription(("PALACIO MSI | " + r.getPeriod() + " | " + r.getPlanText()).trim());
        d.setMaxFinancingTerm(r.getPlanMonths());

        // N de M
        setCurrentInstallmentSafe(d, r.getCurrentInstallment());

        d.setMonthlyPayment((r.getMonthlyPayment()));

        d.setOriginalAmount(null); // computed downstream as monthlyPayment × maxFinancingTerm

        // Optional: you might want to store saldo pendiente somewhere else; your v1 Debt doesn't have it.
        // If you added a field later, set it here.
        d.setDebtType(DebtTypeEnum.CARD);

        d.setDebtAccount(debtAccount);
        return d;
    }

    private void setCurrentInstallmentSafe(Debt d, Integer value) {
        try {
            d.getClass().getMethod("setCurrentInstallment", Integer.class).invoke(d, value);
        } catch (Exception ignored) {
            try {
                d.getClass().getMethod("setCurrentInstallment", int.class).invoke(d, value == null ? 0 : value);
            } catch (Exception ignored2) {}
        }
    }

    // ---------- Utils ----------
    private static BigDecimal money(String raw) {
        return new BigDecimal(raw.replace(",", "").trim()).setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal toDoubletoDouble22(BigDecimal v) {
        if (v == null) return new BigDecimal("0");
        return v.setScale(2, RoundingMode.HALF_UP);
    }

    private static String normalizeSpaces(String s) {
        return s == null ? "" : s.replaceAll("\\s+", " ").trim();
    }

    private static int mxMonthToNumberFull(String mon) {
        String m = mon.toLowerCase(Locale.ROOT).replace(".", "").trim();
        return switch (m) {
            case "enero" -> 1;
            case "febrero" -> 2;
            case "marzo" -> 3;
            case "abril" -> 4;
            case "mayo" -> 5;
            case "junio" -> 6;
            case "julio" -> 7;
            case "agosto" -> 8;
            case "septiembre", "setiembre" -> 9;
            case "octubre" -> 10;
            case "noviembre" -> 11;
            case "diciembre" -> 12;
            default -> throw new IllegalArgumentException("Unknown MX month (Palacio): " + mon);
        };
    }
}