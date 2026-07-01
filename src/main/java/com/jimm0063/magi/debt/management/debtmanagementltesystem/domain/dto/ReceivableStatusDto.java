package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.dto;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.Receivable;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.ReceivablePayment;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
public class ReceivableStatusDto {
    private Receivable receivable;
    private BigDecimal totalRepaid;
    private BigDecimal pendingAmount;
    private Double percentPaid;
    private Boolean fullyPaid;
    private List<ReceivablePayment> payments;
}
