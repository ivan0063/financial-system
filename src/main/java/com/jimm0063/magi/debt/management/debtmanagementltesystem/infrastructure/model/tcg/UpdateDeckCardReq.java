package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model.tcg;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.enums.DeckBoard;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateDeckCardReq {
    @Min(1)
    private int quantity;
    private DeckBoard board;
}
