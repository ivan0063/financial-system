package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class IgnorableDebt {
    private Integer id;
    private String hashSum;
    private String reason;
    private Boolean active;
    private Instant createdAt;
}
