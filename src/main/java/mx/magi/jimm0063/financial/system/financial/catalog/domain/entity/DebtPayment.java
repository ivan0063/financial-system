package mx.magi.jimm0063.financial.system.financial.catalog.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "DEBT_PAYMENT")
public class DebtPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "DEBT_PAYMENT_ID", nullable = false)
    private String debtPaymentId;

    @CreationTimestamp
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "PREV_DEBT_PAID")
    private Double prevDebtPaid;

    @Column(name = "PREV_MONTHS_PAID")
    private Integer prevMonthsPaid;

    @Column(name = "DEBT_PAID")
    private Double debtPaid;

    @Column(name = "MONTHS_PAID")
    private Integer monthsPaid;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CARD_PAYMENT", nullable = false)
    private CardPayment cardPayment;
}
