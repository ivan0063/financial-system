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
    @Override
    public List<Debt> extractDebts(MultipartFile accountStatement, DebtAccount debtAccount) {
        List<Debt> debts = new ArrayList<>();
        try (PDDocument document = Loader.loadPDF(new RandomAccessReadBuffer(accountStatement.getBytes()))) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            Pattern pattern = Pattern.compile(
                    "(\\d{4}-\\d{2}-\\d{2}|\\d{2}-[a-zA-Z]{3}-\\d{4})\\s+" + // Date (ISO or Spanish)
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

                if (startParsing && (line.contains("---")
                        || line.contains("CARGOS,COMPRAS Y ABONOS REGULARES")
                        || line.contains("COMPRAS Y CARGOS DIFERIDOS A MESES CON INTERESES"))) {
                    break;
                }

                if (startParsing) {
                    Matcher matcher = pattern.matcher(line.trim());
                    if (matcher.find()) {
                        Debt debt = new Debt();
                        debt.setId(null);
                        debt.setDescription(matcher.group(2).trim());
                        debt.setOperationDate(matcher.group(1).trim());
                        debt.setCurrentInstallment(Integer.parseInt(matcher.group(6)));
                        debt.setMaxFinancingTerm(Integer.parseInt(matcher.group(7)));
                        debt.setOriginalAmount(
                                new BigDecimal(matcher.group(3).replace(",", ""))
                        );

                        debt.setMonthlyPayment(
                                new BigDecimal(matcher.group(5).replace(",", ""))
                        );
                        debt.setActive(true);
                        debt.setDebtType(DebtTypeEnum.CARD);

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
