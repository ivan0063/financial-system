package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.dto;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

public record MonthProgressionDto(
        YearMonth month,
        String displayName,
        BigDecimal totalPayment,
        List<CardProgressionDto> cards
) {}
