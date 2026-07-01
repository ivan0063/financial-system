package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Table(name = "receivable")
@Entity
@Setter
@Getter
public class ReceivableEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "debtor_name")
    private String debtorName;
    private String description;
    @Column(name = "principal_amount")
    private BigDecimal principalAmount;
    @Column(name = "lent_date")
    private LocalDate lentDate;
    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
    private Boolean active;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "debt_sys_user", nullable = false)
    private DebtSysUserEntity debtSysUser;
    @OneToMany(mappedBy = "receivable")
    private List<ReceivablePaymentEntity> payments;
}
