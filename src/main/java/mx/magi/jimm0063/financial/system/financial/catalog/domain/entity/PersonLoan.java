package mx.magi.jimm0063.financial.system.financial.catalog.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "PERSON_LOAN")
public class PersonLoan {
    @Id
    @Size(max = 50)
    @Column(name = "LOAN_CODE", nullable = false, length = 50)
    private String loanCode;

    @Size(max = 100)
    @Column(name = "DESCRIPTION", length = 100)
    private String description;

    @Column(name = "DISABLED")
    private Boolean disabled;

    @OneToMany(mappedBy = "personLoan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Debt> debts;
}