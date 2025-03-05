package mx.magi.jimm0063.financial.system.financial.catalog.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "CARD_PAYMENT")
public class CardPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "CARD_PAYMENT_ID", nullable = false)
    private String cardPaymentId;

    @CreationTimestamp
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "MONTH_AMOUNT_UPDATED")
    private Double monthAmountUpdated;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CARD_CODE", nullable = false)
    private Card card;

    @OneToMany(mappedBy = "cardPayment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DebtPayment> debtPayments;
}
