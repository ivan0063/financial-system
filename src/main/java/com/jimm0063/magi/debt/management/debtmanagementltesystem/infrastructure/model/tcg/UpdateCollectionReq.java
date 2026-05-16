package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model.tcg;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCollectionReq {
    @NotBlank
    private String name;
    private String description;
}
