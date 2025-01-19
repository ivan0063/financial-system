package mx.magi.jimm0063.financial.system.financial.catalog.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "FIXED_EXPENSE_CATALOG", schema = "debts")
public class FixedExpenseCatalog {
    @Id
    @Size(max = 50)
    @Column(name = "FIXED_EXPENSE_CODE", nullable = false, length = 50)
    private String fixedExpenseCode;

    @Size(max = 100)
    @Column(name = "NAME", length = 100)
    private String name;

}