package mx.magi.jimm0063.financial.system.financial.catalog.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class CardDebtId implements Serializable {
    @Serial
    private static final long serialVersionUID = -5697389804794093989L;
    @Size(max = 50)
    @NotNull
    @Column(name = "CARD_CODE", nullable = false, length = 50)
    private String cardCode;

    @Size(max = 36)
    @NotNull
    @Column(name = "DEBT_ID", nullable = false, length = 36)
    private String debtId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CardDebtId entity = (CardDebtId) o;
        return Objects.equals(this.cardCode, entity.cardCode) &&
                Objects.equals(this.debtId, entity.debtId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardCode, debtId);
    }

}