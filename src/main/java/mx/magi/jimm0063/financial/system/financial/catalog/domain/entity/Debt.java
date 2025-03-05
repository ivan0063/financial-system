package mx.magi.jimm0063.financial.system.financial.catalog.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "DEBT")
public class Debt {
    @Id
    @Size(max = 150)
    @Column(name = "DEBT_ID", nullable = false, length = 150)
    private String debtId;

    @Column(name = "CREATED_AT")
    @CreationTimestamp(source = SourceType.DB)
    private LocalDateTime createdAt;

    @Size(max = 100)
    @Column(name = "NAME", length = 100)
    private String name;

    @Column(name = "INITIAL_DEBT_AMOUNT")
    private Double initialDebtAmount;

    @Column(name = "DEBT_PAID")
    private Double debtPaid;

    @Column(name = "MONTHS_FINANCED")
    private Integer monthsFinanced;

    @Column(name = "MONTHS_PAID")
    private Integer monthsPaid;

    @Column(name = "MONTH_AMOUNT")
    private Double monthAmount;

    @Column(name = "DISABLED")
    private Boolean disabled;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CARD_CODE")
    private Card card;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOAN_CODE")
    private PersonLoan personLoan;
}