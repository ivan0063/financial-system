package mx.magi.jimm0063.financial.system.financial.catalog.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "PERSON_LOAN_DEBT")
public class PersonLoanDebt {
    @EmbeddedId
    private PersonLoanDebtId id;

    @MapsId("loanCode")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "LOAN_CODE", nullable = false)
    private PersonLoan personLoanCode;

    @MapsId("debtId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "DEBT_ID", nullable = false)
    private Debt debt;
}