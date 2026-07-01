package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class ReceivablePaymentReq {
    private BigDecimal amount;
    private String note;
}
