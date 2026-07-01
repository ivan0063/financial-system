package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Setter
@Getter
public class Receivable implements Serializable {
    private Integer id;
    private String debtorName;
    private String description;
    private BigDecimal principalAmount;
    private LocalDate lentDate;
    private Instant createdAt;
    private Instant updatedAt;
    private Boolean active;
    private DebtSysUser debtSysUser;
}
