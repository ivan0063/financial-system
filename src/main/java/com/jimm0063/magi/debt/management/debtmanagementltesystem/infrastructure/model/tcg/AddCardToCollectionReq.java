package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model.tcg;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddCardToCollectionReq {
    @NotBlank
    private String scryfallId;
    @NotBlank
    private String cardName;
    @NotBlank
    private String setCode;
    private String collectorNumber;
    private String imageUri;
    @Min(1)
    private int quantity = 1;
}
