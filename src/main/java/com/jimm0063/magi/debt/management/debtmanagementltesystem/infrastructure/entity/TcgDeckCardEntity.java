package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.entity;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.enums.DeckBoard;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Table(name = "tcg_deck_card")
@Entity
@Getter
@Setter
public class TcgDeckCardEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deck_id", nullable = false)
    private TcgDeckEntity deck;
    @Column(name = "scryfall_id", nullable = false)
    private String scryfallId;
    @Column(name = "card_name", nullable = false)
    private String cardName;
    @Column(name = "set_code")
    private String setCode;
    @Column(name = "collector_number")
    private String collectorNumber;
    @Column(name = "image_uri")
    private String imageUri;
    @Column(nullable = false)
    private int quantity;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeckBoard board;
}
