package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
public class CreateReceivableReq {
    private String debtorName;
    private String description;
    private BigDecimal principalAmount;
    private LocalDate lentDate;
}
