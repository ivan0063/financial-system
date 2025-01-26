package mx.magi.jimm0063.financial.system.financial.catalog.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "CARD_DEBT")
public class CardDebt {
    @EmbeddedId
    private CardDebtId id;

    @MapsId("cardCode")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CARD_CODE", nullable = false)
    private Card cardCode;

    @MapsId("debtId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "DEBT_ID", nullable = false)
    private Debt debt;
}