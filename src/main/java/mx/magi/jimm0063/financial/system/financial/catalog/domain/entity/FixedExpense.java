package mx.magi.jimm0063.financial.system.financial.catalog.domain.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "FIXED_EXPENSE")
public class FixedExpense {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "FIXED_EXPNSE_ID")
    private String fixedExpenseId;
    @NotNull
    private String name;
    @NotNull
    @Column(name = "COST_AMOUNT")
    private Double costAmount;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FIXED_EXPENSE_CODE", nullable = false)
    private FixedExpenseCatalog fixedExpenseCode;
    @NotNull
    private Boolean finalized;
}
