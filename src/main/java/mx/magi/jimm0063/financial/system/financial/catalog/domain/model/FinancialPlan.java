package mx.magi.jimm0063.financial.system.financial.catalog.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "FINANCIAL_PLAN")
public class FinancialPlan {
    @Id
    @Size(max = 50)
    @Column(name = "FINANCIAL_PLAN_CODE", nullable = false, length = 50)
    private String financialPlanCode;

    @Size(max = 100)
    @Column(name = "DESCRIPTION", length = 100)
    private String description;

    @NotNull
    @Column(name = "MONTHS_FINANCED", nullable = false)
    private Integer monthsFinanced;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BANK_CODE", nullable = false)
    private Bank bankCode;

    @Column(name = "ENABLED")
    private Boolean enabled;

}