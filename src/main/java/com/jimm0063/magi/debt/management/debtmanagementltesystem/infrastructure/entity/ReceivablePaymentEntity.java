package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Table(name = "receivable_payment")
@Entity
@Setter
@Getter
public class ReceivablePaymentEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;
    private BigDecimal amount;
    private String note;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receivable", nullable = false)
    private ReceivableEntity receivable;
}
