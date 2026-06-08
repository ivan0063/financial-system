package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.enums.DebtTypeEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DebtTypeOverrideReq {
    private String hashSum;
    private DebtTypeEnum debtType;
}
