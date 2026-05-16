package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class TcgCollectionCard implements Serializable {
    private Long id;
    private Long collectionId;
    private String scryfallId;
    private String cardName;
    private String setCode;
    private String collectorNumber;
    private String imageUri;
    private int quantity;
}
