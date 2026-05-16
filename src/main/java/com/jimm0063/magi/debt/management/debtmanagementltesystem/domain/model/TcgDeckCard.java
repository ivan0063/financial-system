package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.enums.DeckBoard;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class TcgDeckCard implements Serializable {
    private Long id;
    private Long deckId;
    private String scryfallId;
    private String cardName;
    private String setCode;
    private String collectorNumber;
    private String imageUri;
    private int quantity;
    private DeckBoard board;
}
