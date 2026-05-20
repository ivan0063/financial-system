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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses RappiCard statements issued by Banorte (2025+), which use the standard
 * CONDUSEF "Estado de Cuenta Universal" layout — identical to Joy/BBVA but with
 * ISO dates (yyyy-MM-dd) and a trailing rate% column instead of NA.
 */
@Service("RAPPI")
public class RappiAccountStatementService implements AccountStatementDataExtractionUseCase {

    private static final String SECTION_START = "COMPRAS Y CARGOS DIFERIDOS A MESES SIN INTERESES";

    // Stop when we hit the interest-bearing section or regular charges
    private static final List<String> SECTION_END_MARKERS = List.of(
            "COMPRAS Y CARGOS DIFERIDOS A MESES CON INTERESES",
            "CARGOS, ABONOS",
            "CARGOS,ABONOS"
    );

    // yyyy-MM-dd  description  $original  $pending  $pago_requerido  N de M  rate%
    private static final Pattern ROW_PATTERN = Pattern.compile(
            "(\\d{4}-\\d{2}-\\d{2})\\s+" +
                    "(.+?)\\s+\\$" +
                    "([\\d,]+\\.\\d{2})\\s+\\$" +
                    "([\\d,]+\\.\\d{2})\\s+\\$" +
                    "([\\d,]+\\.\\d{2})\\s+" +
                    "(\\d+)\\s+de\\s+(\\d+)"
    );

    @Override
    public List<Debt> extractDebts(MultipartFile accountStatement, DebtAccount debtAccount) {
        List<Debt> debts = new ArrayList<>();
        try (PDDocument document = Loader.loadPDF(accountStatement.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            String text = stripper.getText(document);

            boolean inSection = false;
            for (String rawLine : text.split("\\r?\\n")) {
                String line = rawLine.trim();

                if (line.contains(SECTION_START)) {
                    inSection = true;
                    continue;
                }

                if (inSection && SECTION_END_MARKERS.stream().anyMatch(line::contains)) {
                    break;
                }

                if (!inSection || line.isBlank()) continue;

                Matcher m = ROW_PATTERN.matcher(line);
                if (!m.find()) continue;

                Debt debt = new Debt();
                debt.setOperationDate(m.group(1));
                debt.setDescription(m.group(2).trim());
                debt.setOriginalAmount(new BigDecimal(m.group(3).replace(",", "")));
                debt.setMonthlyPayment(new BigDecimal(m.group(5).replace(",", "")));
                debt.setCurrentInstallment(Integer.parseInt(m.group(6)));
                debt.setMaxFinancingTerm(Integer.parseInt(m.group(7)));
                debt.setActive(true);
                debt.setDebtType(DebtTypeEnum.CARD);
                debt.setDebtAccount(debtAccount);
                debts.add(debt);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to extract Rappi (Banorte) debts from PDF", e);
        }
        return debts;
    }
}
