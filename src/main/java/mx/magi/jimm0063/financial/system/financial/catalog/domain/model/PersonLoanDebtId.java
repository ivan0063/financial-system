package mx.magi.jimm0063.financial.system.financial.catalog.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class PersonLoanDebtId implements Serializable {
    private static final long serialVersionUID = 7584351864725179093L;
    @Size(max = 50)
    @NotNull
    @Column(name = "LOAN_CODE", nullable = false, length = 50)
    private String loanCode;

    @Size(max = 36)
    @NotNull
    @Column(name = "DEBT_ID", nullable = false, length = 36)
    private String debtId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PersonLoanDebtId entity = (PersonLoanDebtId) o;
        return Objects.equals(this.loanCode, entity.loanCode) &&
                Objects.equals(this.debtId, entity.debtId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(loanCode, debtId);
    }

}