package com.jimm0063.magi.debt.management.debtmanagementltesystem.application.service;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.AccountStatementDataExtractionUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.enums.DebtTypeEnum;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.Debt;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.DebtAccount;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses a user-supplied CSV file into debts for bulk import.
 *
 * Expected CSV header (first row is always skipped):
 *   description,operation_date,original_amount,monthly_payment,current_installment,max_financing_term,debt_type
 *
 * Columns:
 *   description         - Free-text label for the debt (wrap in quotes if it contains commas)
 *   operation_date      - ISO date: YYYY-MM-DD
 *   original_amount     - Total debt amount (decimal, e.g. 35000.00)
 *   monthly_payment     - Monthly instalment amount (decimal)
 *   current_installment - Installment already paid (integer, use 0 for a new debt)
 *   max_financing_term  - Total number of instalments (integer)
 *   debt_type           - One of: CARD, LOAN, PEOPLE, FOR_LIFE_PLAN
 */
@Service("CSV")
public class CsvAccountStatementService implements AccountStatementDataExtractionUseCase {

    private static final int COL_DESCRIPTION = 0;
    private static final int COL_OPERATION_DATE = 1;
    private static final int COL_ORIGINAL_AMOUNT = 2;
    private static final int COL_MONTHLY_PAYMENT = 3;
    private static final int COL_CURRENT_INSTALLMENT = 4;
    private static final int COL_MAX_FINANCING_TERM = 5;
    private static final int COL_DEBT_TYPE = 6;
    private static final int REQUIRED_COLUMNS = 7;

    @Override
    public List<Debt> extractDebts(MultipartFile accountStatement, DebtAccount debtAccount) {
        List<Debt> debts = new ArrayList<>();
        try {
            String content = new String(accountStatement.getBytes(), StandardCharsets.UTF_8);
            String[] lines = content.split("\\r?\\n");

            for (int i = 1; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.isEmpty()) continue;

                String[] fields = parseCsvLine(line);
                if (fields.length < REQUIRED_COLUMNS) continue;

                Debt debt = new Debt();
                debt.setDescription(fields[COL_DESCRIPTION].trim());
                debt.setOperationDate(fields[COL_OPERATION_DATE].trim());
                debt.setOriginalAmount(new BigDecimal(fields[COL_ORIGINAL_AMOUNT].trim()));
                debt.setMonthlyPayment(new BigDecimal(fields[COL_MONTHLY_PAYMENT].trim()));
                debt.setCurrentInstallment(Integer.parseInt(fields[COL_CURRENT_INSTALLMENT].trim()));
                debt.setMaxFinancingTerm(Integer.parseInt(fields[COL_MAX_FINANCING_TERM].trim()));
                debt.setDebtType(DebtTypeEnum.valueOf(fields[COL_DEBT_TYPE].trim().toUpperCase()));
                debt.setActive(true);
                debts.add(debt);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read CSV file", e);
        }
        return debts;
    }

    /**
     * Splits a CSV line respecting double-quoted fields (quotes allow embedded commas).
     */
    private String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                fields.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString());
        return fields.toArray(new String[0]);
    }
}
