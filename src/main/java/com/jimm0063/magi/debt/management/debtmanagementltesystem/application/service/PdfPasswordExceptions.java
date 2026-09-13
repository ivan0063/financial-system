package com.jimm0063.magi.debt.management.debtmanagementltesystem.application.service;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.exceptions.PdfPasswordRequiredException;

/** Builds the right {@link PdfPasswordRequiredException} message for a failed PDF open attempt. */
final class PdfPasswordExceptions {

    private PdfPasswordExceptions() {}

    static PdfPasswordRequiredException forAttempt(String password) {
        boolean tried = password != null && !password.isBlank();
        return new PdfPasswordRequiredException(
                tried ? "Incorrect password for this PDF." : "This PDF is password protected.", tried);
    }
}
