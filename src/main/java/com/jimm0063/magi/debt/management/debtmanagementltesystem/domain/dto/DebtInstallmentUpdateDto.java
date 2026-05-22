package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.dto;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.Debt;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DebtInstallmentUpdateDto {
    private Debt debt;
    private Integer previousInstallment;
    private Integer newInstallment;
}
