package com.jimm0063.magi.debt.management.debtmanagementltesystem.application.service;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.AccountStatementDataExtractionUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.enums.DebtTypeEnum;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.Debt;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.DebtAccount;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("RAPPI")
public class RappiAccountStatementService implements AccountStatementDataExtractionUseCase {

    private static final String SIN_INTERESES_MARKER = "COMPRAS Y CARGOS DIFERIDOS A MESES SIN INTERESES";
    private static final String CON_INTERESES_MARKER = "COMPRAS Y CARGOS DIFERIDOS A MESES CON INTERESES";
    private static final List<String> REGULAR_SECTION_MARKERS = List.of(
            "CARGOS, ABONOS Y COMPRAS REGULARES",
            "CARGOS,COMPRAS Y ABONOS REGULARES",
            "ATENCIÓN DE QUEJAS"
    );

    // Date: YYYY-MM-DD (standard) or YYYY-MMDD (day merged without separator by PDFBox)
    // followed by optional whitespace before description
    private static final String DATE_PATTERN = "(\\d{4}-\\d{2}(?:-\\s*)?\\d{2})";

    // SIN INTERESES columns: date | description | $original | $balance | $monthly | N de M | rate%
    private static final Pattern SIN_INTERESES_ENTRY = Pattern.compile(
            DATE_PATTERN +
            "\\s*(.+?)" +
            "\\s+\\$([\\d,]+\\.\\d{2})" +   // monto original
            "\\s+\\$[\\d,]+\\.\\d{2}" +      // saldo pendiente (skip)
            "\\s+\\$([\\d,]+\\.\\d{2})" +    // pago requerido (monthly payment)
            "\\s+(\\d+)\\s+de\\s+(\\d+)"     // N de M
    );

    // CON INTERESES columns: date | description | $original | $balance | $intereses | $IVA | $monthly | N de M | rate%
    private static final Pattern CON_INTERESES_ENTRY = Pattern.compile(
            DATE_PATTERN +
            "\\s*(.+?)" +
            "\\s+\\$([\\d,]+\\.\\d{2})" +   // monto original
            "\\s+\\$[\\d,]+\\.\\d{2}" +      // saldo pendiente (skip)
            "\\s+\\$[\\d,]+\\.\\d{2}" +      // intereses del periodo (skip)
            "\\s+\\$[\\d,]+\\.\\d{2}" +      // IVA de intereses (skip)
            "\\s+\\$([\\d,]+\\.\\d{2})" +    // pago requerido (monthly payment)
            "\\s+(\\d+)\\s+de\\s+(\\d+)"     // N de M
    );

    @Override
    public List<Debt> extractDebts(MultipartFile accountStatement, DebtAccount debtAccount) {
        List<Debt> debts = new ArrayList<>();
        try (PDDocument document = Loader.loadPDF(new RandomAccessReadBuffer(accountStatement.getBytes()))) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            debts.addAll(parseSinIntereses(text));
            debts.addAll(parseAllConIntereses(text));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return debts;
    }

    private List<Debt> parseSinIntereses(String fullText) {
        List<String> endMarkers = new ArrayList<>(REGULAR_SECTION_MARKERS);
        endMarkers.add(CON_INTERESES_MARKER);
        String section = extractSection(fullText, SIN_INTERESES_MARKER, endMarkers);
        if (section == null) return List.of();
        return parseEntries(SIN_INTERESES_ENTRY, normalizeText(section));
    }

    private List<Debt> parseAllConIntereses(String fullText) {
        List<Debt> debts = new ArrayList<>();
        int searchFrom = 0;
        while (true) {
            int start = fullText.indexOf(CON_INTERESES_MARKER, searchFrom);
            if (start < 0) break;
            start += CON_INTERESES_MARKER.length();

            int end = fullText.length();
            int nextCon = fullText.indexOf(CON_INTERESES_MARKER, start);
            if (nextCon >= 0 && nextCon < end) end = nextCon;
            for (String marker : REGULAR_SECTION_MARKERS) {
                int pos = fullText.indexOf(marker, start);
                if (pos >= 0 && pos < end) end = pos;
            }

            debts.addAll(parseEntries(CON_INTERESES_ENTRY, normalizeText(fullText.substring(start, end))));
            searchFrom = end;
        }
        return debts;
    }

    private List<Debt> parseEntries(Pattern pattern, String normalizedText) {
        List<Debt> debts = new ArrayList<>();
        Matcher matcher = pattern.matcher(normalizedText);
        while (matcher.find()) {
            String rawDate = matcher.group(1).replaceAll("\\s+", "");
            // Normalize YYYY-MMDD → YYYY-MM-DD when second hyphen was lost during extraction
            if (rawDate.length() == 9) {
                rawDate = rawDate.substring(0, 7) + "-" + rawDate.substring(7);
            }

            Debt debt = new Debt();
            debt.setDescription(matcher.group(2).trim());
            debt.setOperationDate(rawDate);
            debt.setOriginalAmount(new BigDecimal(matcher.group(3).replace(",", "")));
            debt.setMonthlyPayment(new BigDecimal(matcher.group(4).replace(",", "")));
            debt.setCurrentInstallment(Integer.parseInt(matcher.group(5)));
            debt.setMaxFinancingTerm(Integer.parseInt(matcher.group(6)));
            debt.setActive(true);
            debt.setDebtType(DebtTypeEnum.CARD);
            debts.add(debt);
        }
        return debts;
    }

    private String extractSection(String text, String startMarker, List<String> endMarkers) {
        int start = text.indexOf(startMarker);
        if (start < 0) return null;
        start += startMarker.length();

        int end = text.length();
        for (String endMarker : endMarkers) {
            int pos = text.indexOf(endMarker, start);
            if (pos >= 0 && pos < end) end = pos;
        }
        return text.substring(start, end);
    }

    private String normalizeText(String text) {
        return text.replaceAll("\\r?\\n", " ").replaceAll("\\s{2,}", " ").trim();
    }
}
