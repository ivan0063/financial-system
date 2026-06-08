package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AccountSummaryDto {
    private String code;
    private String name;
    private Integer payDay;
    private BigDecimal creditLimit;
    private int activeDebtCount;
    private Double monthlyTotal;
    private Double estimatedRemainingBalance;
    private int maxMonthsRemaining;
    private boolean payoffFeasible;
    private double savingsCoveragePercent;
}
