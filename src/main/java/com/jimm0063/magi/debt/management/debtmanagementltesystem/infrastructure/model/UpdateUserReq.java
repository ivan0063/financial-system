package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateUserReq {
    private String email;
    private Double salary;
    private Double savings;
}
