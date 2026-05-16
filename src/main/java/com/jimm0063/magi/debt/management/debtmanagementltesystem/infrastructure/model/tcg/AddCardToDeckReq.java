package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model.tcg;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.enums.DeckBoard;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddCardToDeckReq {
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
    @NotNull
    private DeckBoard board = DeckBoard.MAINBOARD;
}
