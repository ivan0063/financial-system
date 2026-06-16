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

@Service("UNIVERSAL")
public class UniversalAccountStatementService implements AccountStatementDataExtractionUseCase {

    private static final String SIN_INTERESES_MARKER = "COMPRAS Y CARGOS DIFERIDOS A MESES SIN INTERESES";
    private static final String CON_INTERESES_MARKER = "COMPRAS Y CARGOS DIFERIDOS A MESES CON INTERESES";
    private static final List<String> REGULAR_SECTION_MARKERS = List.of(
            "CARGOS, ABONOS Y COMPRAS REGULARES",
            "CARGOS,COMPRAS Y ABONOS REGULARES",
            "ATENCIÓN DE QUEJAS",
            "---"
    );

    // Supports both ISO (yyyy-MM-dd) and Spanish (dd-MMM-yyyy) date formats
    private static final String DATE_PATTERN =
            "(\\d{4}-\\d{2}-\\d{2}|\\d{2}-[a-zA-Z]{3}-\\d{4})";

    // SIN INTERESES columns: date | description | $original | $balance | $monthly | N de M
    private static final Pattern SIN_INTERESES_ENTRY = Pattern.compile(
            DATE_PATTERN +
            "\\s+(.+?)" +
            "\\s+\\$([\\d,]+\\.\\d{2})" +   // monto original
            "\\s+\\$[\\d,]+\\.\\d{2}" +      // saldo pendiente (skip)
            "\\s+\\$([\\d,]+\\.\\d{2})" +    // pago requerido (monthly payment)
            "\\s+(\\d+)\\s+de\\s+(\\d+)"     // N de M
    );

    // CON INTERESES columns: date | description | $original | $balance | $intereses | $IVA | $monthly | N de M
    private static final Pattern CON_INTERESES_ENTRY = Pattern.compile(
            DATE_PATTERN +
            "\\s+(.+?)" +
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

            debts.addAll(parseAllSinIntereses(text));
            debts.addAll(parseAllConIntereses(text));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return debts;
    }

    private List<Debt> parseAllSinIntereses(String fullText) {
        List<Debt> debts = new ArrayList<>();
        int searchFrom = 0;
        while (true) {
            int start = fullText.indexOf(SIN_INTERESES_MARKER, searchFrom);
            if (start < 0) break;
            start += SIN_INTERESES_MARKER.length();

            // End at the next sub-section header or higher-level section
            int end = fullText.length();
            int nextSin = fullText.indexOf(SIN_INTERESES_MARKER, start);
            if (nextSin >= 0 && nextSin < end) end = nextSin;
            int nextCon = fullText.indexOf(CON_INTERESES_MARKER, start);
            if (nextCon >= 0 && nextCon < end) end = nextCon;
            for (String marker : REGULAR_SECTION_MARKERS) {
                int pos = fullText.indexOf(marker, start);
                if (pos >= 0 && pos < end) end = pos;
            }

            debts.addAll(parseEntries(SIN_INTERESES_ENTRY, normalizeText(fullText.substring(start, end))));
            searchFrom = end;
        }
        return debts;
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
            Debt debt = new Debt();
            debt.setDescription(matcher.group(2).trim());
            debt.setOperationDate(matcher.group(1).trim());
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

    private String normalizeText(String text) {
        return text.replaceAll("\\r?\\n", " ").replaceAll("\\s{2,}", " ").trim();
    }
}
