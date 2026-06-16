package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Table(name = "fixed_expense")
@Entity
@Setter
@Getter
public class FixedExpenseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    @Column(name = "monthly_cost")
    private BigDecimal monthlyCost;
    @Column(name = "payment_day")
    private Integer paymentDay;
    private String description;
    private Boolean active;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "debt_sys_user", nullable = false)
    private DebtSysUserEntity debtSysUser;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fixed_expenses_catalog", nullable = false)
    private FixedExpenseCatalogEntity fixedExpenseCatalog;
}
