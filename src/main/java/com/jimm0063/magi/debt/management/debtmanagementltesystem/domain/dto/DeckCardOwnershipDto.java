package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.dto;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.enums.DeckBoard;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeckCardOwnershipDto {
    private Long id;
    private String scryfallId;
    private String cardName;
    private String setCode;
    private String collectorNumber;
    private String imageUri;
    private int quantity;
    private DeckBoard board;
    private boolean owned;
    private int ownedQuantity;
}
