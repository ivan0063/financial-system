package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model.tcg;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCollectionCardReq {
    @Min(1)
    private int quantity;
}
