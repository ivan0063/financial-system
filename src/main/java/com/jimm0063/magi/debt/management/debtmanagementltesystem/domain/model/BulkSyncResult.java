package com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model;

import java.io.Serializable;
import java.util.List;

/** Outcome of executing a bulk statement import, one entry per file. */
public record BulkSyncResult(List<Entry> entries) implements Serializable {

    public enum Outcome { APPLIED, NO_CHANGES, SKIPPED, FAILED }

    public record Entry(
            String fileName,
            String debtAccountCode,
            String accountName,
            String mode,
            Outcome outcome,
            int inserted,
            int updated,
            int deactivated,
            String note
    ) implements Serializable {}

    public long count(Outcome outcome) {
        return entries.stream().filter(e -> e.outcome() == outcome).count();
    }
}
