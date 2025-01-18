package mx.magi.jimm0063.financial.system.financial.catalog.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "LOANS")
public class Loan {
    @Id
    @Size(max = 50)
    @Column(name = "LOAN_CODE", nullable = false, length = 50)
    private String loanCode;

    @Size(max = 100)
    @Column(name = "DESCRIPTION", length = 100)
    private String description;

    @Column(name = "LOAN_DEBT")
    private Double loanDebt;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BANK_CODE", nullable = false)
    private Bank bankCode;

    @Column(name = "DISABLED")
    private Boolean disabled;

}