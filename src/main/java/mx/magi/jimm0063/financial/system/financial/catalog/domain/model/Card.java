package mx.magi.jimm0063.financial.system.financial.catalog.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "CARD", schema = "debts")
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

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BANK_CODE", nullable = false)
    private Bank bankCode;

    @Column(name = "ENABLED")
    private Boolean enabled;

}