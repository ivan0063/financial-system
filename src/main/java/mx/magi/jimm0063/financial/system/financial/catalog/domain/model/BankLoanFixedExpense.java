package mx.magi.jimm0063.financial.system.financial.catalog.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "BANK_LOAN_FIXED_EXPENSE")
public class BankLoanFixedExpense {
    @EmbeddedId
    private BankLoanFixedExpnseId id;

    @MapsId("loanCode")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "LOAN_CODE", nullable = false)
    private BankLoan bankLoanCode;

    @MapsId("fixedExpenseId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FIXED_EXPNSE_ID", nullable = false)
    private FixedExpense fixedExpenseId;
}