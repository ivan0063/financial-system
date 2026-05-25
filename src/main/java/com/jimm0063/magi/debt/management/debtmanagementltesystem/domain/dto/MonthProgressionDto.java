package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.dto;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.Debt;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

public record MonthProgressionDto(
        YearMonth month,
        String displayName,
        BigDecimal totalPayment,
        List<CardProgressionDto> cards
) {
    public boolean hasClosingDebts() {
        return cards.stream().anyMatch(c -> !c.closingDebts().isEmpty());
    }

    public List<Debt> allClosingDebts() {
        return cards.stream()
                .flatMap(c -> c.closingDebts().stream())
                .toList();
    }

    public int totalActiveDebtCount() {
        return cards.stream().mapToInt(CardProgressionDto::activeDebtCount).sum();
    }
}
