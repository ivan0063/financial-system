package mx.magi.jimm0063.financial.system.financial.catalog.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

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

    @NotNull
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "bankCode")
    @RestResource(path = "card", rel = "card")
    private List<Card> cards;

}