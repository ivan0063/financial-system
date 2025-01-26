package mx.magi.jimm0063.financial.system.financial.catalog.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "FINANCIAL_STATE")
public class FinancialStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private Double salary;
    private Double savings;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "EMAIL", nullable = false)
    private User user;
}
