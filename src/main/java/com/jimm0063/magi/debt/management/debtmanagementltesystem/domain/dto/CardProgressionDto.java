package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.dto;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.Debt;

import java.math.BigDecimal;
import java.util.List;

public record CardProgressionDto(
        String accountCode,
        String accountName,
        List<Debt> closingDebts,
        int activeDebtCount,
        BigDecimal cardPayment,
        BigDecimal remainingAmount
) {}
