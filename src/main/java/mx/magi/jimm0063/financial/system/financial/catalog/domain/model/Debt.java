package mx.magi.jimm0063.financial.system.financial.catalog.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "DEBT", schema = "debts")
public class Debt {
    @Id
    @Size(max = 36)
    @Column(name = "DEBT_ID", nullable = false, length = 36)
    private String debtId;

    @Column(name = "CREATED_AT")
    @CreationTimestamp
    private Instant createdAt;

    @Size(max = 100)
    @Column(name = "NAME", length = 100)
    private String name;

    @Column(name = "INITIAL_DEBT_AMOUNT")
    private Double initialDebtAmount;

    @Column(name = "DEBT_PAID")
    private Double debtPaid;

    @Column(name = "MONTHS_FINANCED")
    private Double monthsFinanced;

    @Column(name = "MONTHS_PAID")
    private Double monthsPaid;

    @Column(name = "DISABLED")
    private Boolean disabled;

}