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

}