package com.jimm0063.magi.debt.management.debtmanagementltesystem.application.service;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.AccountStatementDataExtractionUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.enums.DebtTypeEnum;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.Debt;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.DebtAccount;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service("CSV_UPLOAD")
public class CsvAccountStatementService implements AccountStatementDataExtractionUseCase {

    @Override
    public List<Debt> extractDebts(MultipartFile accountStatement, DebtAccount debtAccount) {
        List<Debt> debts = new ArrayList<>();

        try (CSVReader reader = new CSVReader(
                new InputStreamReader(accountStatement.getInputStream(), StandardCharsets.UTF_8))) {

            reader.skip(1); // skip header row

            String[] row;
            while ((row = reader.readNext()) != null) {
                if (row.length == 0 || isBlank(row[0])) continue;

                Debt debt = new Debt();
                debt.setActive(true);
                debt.setDebtAccount(debtAccount);

                debt.setDescription(row[0].trim());
                debt.setOperationDate(row.length > 1 ? blankToNull(row[1]) : null);
                debt.setOriginalAmount(row.length > 2 ? parseBigDecimal(row[2]) : null);
                debt.setMonthlyPayment(row.length > 3 ? parseBigDecimal(row[3]) : null);
                debt.setCurrentInstallment(row.length > 4 ? parseIntOrDefault(row[4], 1) : 1);
                debt.setMaxFinancingTerm(row.length > 5 ? parseIntOrDefault(row[5], 1) : 1);
                debt.setDebtType(row.length > 6 ? parseDebtType(row[6]) : DebtTypeEnum.CARD);

                debts.add(debt);
            }
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException("Failed to parse CSV file", e);
        }

        return debts;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String blankToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }

    private BigDecimal parseBigDecimal(String value) {
        if (isBlank(value)) return null;
        try {
            return new BigDecimal(value.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer parseIntOrDefault(String value, int defaultValue) {
        if (isBlank(value)) return defaultValue;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private DebtTypeEnum parseDebtType(String value) {
        if (isBlank(value)) return DebtTypeEnum.CARD;
        try {
            return DebtTypeEnum.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return DebtTypeEnum.CARD;
        }
    }
}
