package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
public class TcgCollection implements Serializable {
    private Long id;
    private String name;
    private String description;
    private Instant createdAt;
}
