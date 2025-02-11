package mx.magi.jimm0063.financial.system.financial.catalog.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import mx.magi.jimm0063.financial.system.debt.application.enums.PdfExtractorTypes;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "CARD")
public class Card {
    @Id
    @Size(max = 50)
    @Column(name = "CARD_CODE", nullable = false, length = 50)
    private String cardCode;

    @Size(max = 50)
    @Column(name = "CARD_NAME", length = 50)
    private String cardName;

    @Column(name = "CREDIT")
    private Double credit;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "FILE_TYPE", columnDefinition = "varchar(100)")
    private PdfExtractorTypes fileType;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BANK_CODE", nullable = false)
    private Bank bankCode;

    @Column(name = "ENABLED")
    private Boolean enabled;

    @OneToMany(mappedBy = "cardCode", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CardDebt> cardDebts;
}