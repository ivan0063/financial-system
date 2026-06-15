package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.Instant;

@Table(name = "ignorable_debt")
@Entity
@Getter
@Setter
public class IgnorableDebtEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "hash_sum", unique = true, nullable = false)
    private String hashSum;

    @Column(nullable = false)
    private String reason;

    private Boolean active;

    @CreationTimestamp
    private Instant createdAt;
}
