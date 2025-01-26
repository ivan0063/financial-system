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
@Table(name = "USER")
public class User {
    @Id
    @Size(max = 100)
    @Column(name = "EMAIL", nullable = false, length = 100)
    private String email;

    @Size(max = 200)
    @Column(name = "NAME", length = 200)
    private String name;

    @Size(max = 100)
    @Column(name = "PASSWORD", length = 100)
    private String password;

    @Column(name = "ENABLED")
    private Boolean enabled;

}