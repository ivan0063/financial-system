package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DebtListForm {
    private List<CreateDebtReq> debts = new ArrayList<>();
    private List<DebtTypeOverrideReq> typeOverrides = new ArrayList<>();
}
