package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class FixedExpense {
    private Integer id;
    private String name;
    private BigDecimal monthlyCost;
    private Integer paymentDay;
    private String description;
    private Boolean active;
    private DebtSysUser debtSysUser;
    private FixedExpenseCatalog fixedExpenseCatalog;
}
