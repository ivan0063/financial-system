package mx.magi.jimm0063.financial.system.financial.catalog.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "LOAN_DEBT", schema = "debts")
public class LoanDebt {
    @EmbeddedId
    private LoanDebtId id;

    @MapsId("loanCode")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "LOAN_CODE", nullable = false)
    private Loan loanCode;

    @MapsId("debtId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "DEBT_ID", nullable = false)
    private Debt debt;

}