package mx.magi.jimm0063.financial.system.financial.catalog.domain.entity;

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
@Table(name = "BANK")
public class Bank {
    @Id
    @Size(max = 50)
    @Column(name = "BANK_CODE", nullable = false, length = 50)
    private String bankCode;

    @Size(max = 50)
    @Column(name = "NAME", length = 50)
    private String name;

    @Column(name = "DISABLED")
    private Boolean disabled;
}